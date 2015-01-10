package dev.risk.server;

import dev.risk.game.GameInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class Server extends Observable implements Runnable {

    private Log log = LogFactory.getLog(Server.class);

    protected GameInfo info;

    protected HashMap<InetSocketAddress, Player> player;
    protected ArrayList<Packet> pendingPackets;

    protected DatagramSocket serverSocket;
    protected int packetIdC;
    protected byte playerIdC = 1;

    public Server(GameInfo info) throws IOException {
        serverSocket = new DatagramSocket(3157);
        serverSocket.setSoTimeout(500);
        this.info = info;
        player = new HashMap<>();
        pendingPackets = new ArrayList<>();
    }

    protected int getPacketId() {
        return packetIdC++;
    }
    protected void send(UDPPacket packet, InetSocketAddress address) throws IOException{
        byte[] data = packet.serialize();
        DatagramPacket dgp = new DatagramPacket(data, data.length, address);
        serverSocket.send(dgp);
        pendingPackets.add(new Packet(address, packet));
    }
    protected void sendAll(UDPPacket packet) {
        packet.setPacketID(getPacketId());
        for (Player p : player.values()) {
            try {
                send(packet, p.getAddress());
            } catch (IOException e) {
                log.error("Couldn't send packet!", e);
            }
        }
    }
    protected void sendOthers(UDPPacket packet, Player p) {
        packet.setPacketID(getPacketId());
        for (Player player1 : player.values()) {
            if (player1 != p) {
                try {
                    send(packet, player1.address);
                } catch (IOException e) {
                    log.error("Couldn't send packet!", e);
                }
            }
        }
    }
    protected void sendServerMessage(String msg) {
        byte[] byteMessage = UDPPacket.serializeString(msg);
        byte[] payload = new byte[2+byteMessage.length];
        payload[0] = 0; //server id
        payload[1] = 0; //not private
        System.arraycopy(byteMessage, 0, payload, 2 , byteMessage.length);
        UDPPacket packet = new UDPPacket(UDPPacket.TYPE_CHAT);
        packet.setPayload(payload);
        sendAll(packet);
    }
    protected void checkConnections() {
        for (InetSocketAddress key : player.keySet()) {
            if (player.get(key).getElapsedTime().toMillis() > 3000) {
                player.remove(key);
                //@TODO notify observers of disconnection event
            }
        }
    }
    protected void checkPacketLoss() {
        for (Packet p : pendingPackets) {
            if (p.getElapsedTime().toMillis() > 1000) {
                pendingPackets.remove(p);
                UDPPacket packet = p.getPacket();
                packet.setPacketID(getPacketId());
                try {
                    send(packet, p.getAddress());
                } catch (IOException e) {
                    log.error("Couldn't resend packet!", e);
                }
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
                checkConnections();
                checkPacketLoss();
                lastCheck = Instant.now();
            }
            try {
                DatagramPacket dgp = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(dgp);
                InetSocketAddress address = new InetSocketAddress(dgp.getAddress(), dgp.getPort());

                UDPPacket packet = new UDPPacket(dgp.getData());

                //process ping and ack packets
                if (packet.getType() == UDPPacket.TYPE_PING) {
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_PING, packet.getPacketID(), packet.getTime());
                    send(response, address);
                    continue;
                } else if (packet.getType() == UDPPacket.TYPE_ACK) {
                    for (Packet p : pendingPackets) {
                        if (p.getAddress().equals(address) && p.getPacketID() == packet.getPacketID()) {
                            pendingPackets.remove(p);
                            break;
                        }
                    }
                    continue;
                }

                //process packets from registered players
                if (player.containsKey(address)) {
                    Player p = player.get(address);

                    //reset elapsed time since last packet was received
                    p.resetElapsedTime();

                    //ack received package
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_ACK, packet.getPacketID());
                    send(response, address);

                    //handle package
                    switch (packet.getType()) {
                        case UDPPacket.TYPE_CHAT:
                            onChat(packet, p);
                    }
                    continue;
                }

                //non-player reply
                if (packet.getType() == UDPPacket.TYPE_STATUS_REQ) {
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_STATUS);
                    if (packet.getPacketID() == 0) {
                        response.setPacketID(getPacketId());
                    } else {
                        response.setPacketID(packet.getPacketID());
                    }
                    byte[] name = UDPPacket.serializeString(info.getGameName());
                    byte[] payload = new byte[name.length+3];
                    payload[0] = (byte) player.size();
                    payload[1] = (byte) info.getMaxPlayer();
                    payload[2] = 0;
                    if (!info.getPassword().isEmpty()) {
                        payload[2] = 1;
                    }
                    System.arraycopy(name,0,payload,3,name.length);
                    response.setPayload(payload);
                    send(response, address);
                } else if (packet.getType() == UDPPacket.TYPE_JOIN_REQ) {

                    if (player.size() < info.getMaxPlayer()) {
                        String name = UDPPacket.desirializeString(packet.getPayload());
                        String pass = UDPPacket.desirializeString(packet.getPayload(), name.length());
                        if (!info.getPassword().isEmpty() && info.getPassword().equals(pass)) {
                            Player p = new Player(playerIdC++, name, address);
                            player.put(address, p);
                            UDPPacket response = new UDPPacket(UDPPacket.TYPE_JOIN);
                            byte[] payload = new byte[2+name.length()];
                            payload[0] = p.getId();
                            byte[] bName = UDPPacket.serializeString(p.getName());
                            System.arraycopy(bName,0,payload,1,bName.length);
                            response.setPayload(payload);
                            sendAll(response);
                            continue;
                        }
                    }
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_JOIN_NACK);
                    if (packet.getPacketID() == 0) {
                        response.setPacketID(getPacketId());
                    } else {
                        response.setPacketID(packet.getPacketID());
                    }
                    send(response, address);
                }
            } catch (SocketTimeoutException | IllegalArgumentException e) {
                log.debug("Wrong packet or timeout!", e);
            } catch (Exception e) {
                log.debug("Exception occurred!", e);
            }
        }
        serverSocket.close();
    }

    protected void onChat(UDPPacket packet, Player p) {
        try {
            byte[] data = packet.getPayload();
            String msg = UDPPacket.desirializeString(data, 1);
            byte[] byteMessage = UDPPacket.serializeString(msg);

            byte[] payload = new byte[byteMessage.length+2];
            payload[0] = p.getId();
            System.arraycopy(byteMessage, 0, payload, 2, byteMessage.length);

            //handle private messages
            if (data[0] != 0) {
                Player to = null;
                for (Player p1 : player.values()) {
                    if (p1.getId() == data[0]) {
                        to = p1;
                        break;
                    }
                }
                if (to == null) {
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_ERROR, packet.getPacketID());
                    send(response, p.getAddress());
                    return;
                }
                UDPPacket response = new UDPPacket(UDPPacket.TYPE_CHAT, getPacketId());
                payload[1] = 1; //set private message
                response.setPayload(payload);
                send(response, to.address);
                return;
            }

            UDPPacket response = new UDPPacket(UDPPacket.TYPE_CHAT);
            payload[1] = 0; //not a private message
            response.setPayload(data);
            sendOthers(response, p);
        } catch (IOException e) {
            log.error("Couldn't send chat data!", e);
        }
    }
}
