package dev.risk.server;

import dev.risk.game.Player;
import dev.risk.network.ChatEvent;
import dev.risk.network.Event;
import dev.risk.network.JoinEvent;
import dev.risk.network.LeaveEvent;
import dev.risk.packet.UDPPacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class ServerNetworkInterface extends Observable implements Runnable {

    private Log log = LogFactory.getLog(ServerNetworkInterface.class);

    protected Collection<Packet> pendingPackets;

    protected DatagramSocket serverSocket;
    protected int packetIdC;

    public ServerNetworkInterface() throws IOException {
        serverSocket = new DatagramSocket(3157);
        serverSocket.setSoTimeout(500);
        pendingPackets = new ConcurrentLinkedDeque<>();
    }
    protected int generatePacketID() {
        return packetIdC++;
    }
    protected int choosePacketId(UDPPacket packet) {
        if (packet.getPacketID() == 0) {
            return generatePacketID();
        }
        return packet.getPacketID();
    }
    public boolean send(UDPPacket packet, InetSocketAddress address, boolean setPending) {
        try {
            byte[] data = packet.serialize();
            DatagramPacket dgp = new DatagramPacket(data, data.length, address);
            serverSocket.send(dgp);
            if (setPending)
                pendingPackets.add(new Packet(address, packet));
            return true;
        } catch (IOException e) {
            log.error("Couldn't send Packet!", e);
            return false;
        }
    }
    public boolean ackPacket(UDPPacket original, InetSocketAddress sender) {
        UDPPacket packet = new UDPPacket(UDPPacket.TYPE_ACK);
        packet.setPacketID(original);
        return send(packet, sender);
    }
    public boolean denyRequest(UDPPacket original, InetSocketAddress sender) {
        UDPPacket packet = new UDPPacket(UDPPacket.TYPE_NACK);
        packet.setPacketID(original);
        return send(packet, sender);
    }
    protected boolean send(UDPPacket packet, Player p) {
        return send(packet, p.getAddress());
    }
    protected boolean send(UDPPacket packet, InetSocketAddress address) {
        return send(packet, address, true);
    }
    public boolean sendAll(UDPPacket packet, Collection<Player> player) {
        packet.setPacketID(generatePacketID());
        boolean b = true;
        for (Player p : player) {
            b &= send(packet, p.getAddress());
        }
        return b;
    }
    public boolean sendOthers(UDPPacket packet, Collection<Player> others, Player player) {
        boolean success = true;
        for (Player p : others) {
            if (p == player)
                continue;
            success &= send(packet, p.getAddress(), true);
        }
        return success;
    }
    protected void checkPacketLoss() {
        for (Packet p : pendingPackets) {
            if (p.getElapsedTime().toMillis() > 1000) {
                pendingPackets.remove(p);
                UDPPacket packet = p.getPacket();
                send(packet, p.getAddress());
            }
        }
    }

    @Override
    public void run() {
        Thread thread = Thread.currentThread();
        byte[] buffer = new byte[1024];
        Instant lastCheck = Instant.now();
        while (!thread.isInterrupted()) {
            if (Duration.between(lastCheck, Instant.now()).abs().toMillis() > 500) {
                checkPacketLoss();
                lastCheck = Instant.now();
            }
            try {
                DatagramPacket dgp = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(dgp);
                InetSocketAddress address = new InetSocketAddress(dgp.getAddress(), dgp.getPort());

                UDPPacket packet = new UDPPacket(dgp.getData());

                //process ping and acknowledge packets
                if (packet.getType() == UDPPacket.TYPE_PING) {
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_PING, packet.getPacketID(), packet.getTime());
                    send(response, address);
                    Event event = new Event(address, packet);
                    setChanged();
                    notifyObservers(event);
                    continue;
                } else if (packet.getType() == UDPPacket.TYPE_ACK) {
                    for (Packet p : pendingPackets) {
                        //remove the acknowledged packet
                        if (p.getAddress().equals(address) && p.getPacketID() == packet.getPacketID()) {
                            pendingPackets.remove(p);
                        }
                    }
                    continue; //continue with next packet
                }

                setChanged();
                switch(packet.getType()) {
                    case UDPPacket.TYPE_STATUS:
                        onStatus(packet, address);
                        break;
                    case UDPPacket.TYPE_CHAT:
                        onChat(packet, address);
                        break;
                    case UDPPacket.TYPE_JOIN:
                        onJoin(packet, address);
                        break;
                    case UDPPacket.TYPE_LEAVE:
                        onLeave(packet, address);
                        break;
                    default:
                        notifyObservers(new Event(address, packet));
                }
            } catch (SocketTimeoutException | IllegalArgumentException e) {
                log.debug("Wrong packet or timeout!", e);
            } catch (Exception e) {
                log.debug("Exception occurred!", e);
            }
        }
        serverSocket.close();
    }

    protected void onStatus(UDPPacket p, InetSocketAddress addr) {
        Event event = new Event(addr, p);
        notifyObservers(event);
    }

    protected void onChat(UDPPacket p, InetSocketAddress addr) {
        byte[] data = p.getPayload();
        String msg = UDPPacket.deserializeString(data, 1);
        ChatEvent event = new ChatEvent(addr, p, msg, data[0]);
        notifyObservers(event);
    }

    protected void onJoin(UDPPacket p, InetSocketAddress addr) {
        String name = UDPPacket.deserializeString(p.getPayload());
        String pass = UDPPacket.deserializeString(p.getPayload(), name.length()+1);
        JoinEvent event = new JoinEvent(addr, p, name, pass);
        notifyObservers(event);
    }

    protected void onLeave(UDPPacket p, InetSocketAddress addr) {
        byte id = p.getPayload()[0];
        String msg = UDPPacket.deserializeString(p.getPayload(), 1);
        Event event = new LeaveEvent(addr, p, id, msg);
        notifyObservers(event);
    }
}
