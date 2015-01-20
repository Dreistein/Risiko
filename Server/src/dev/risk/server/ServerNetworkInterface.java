package dev.risk.server;

import dev.risk.event.Observable;
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

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class ServerNetworkInterface extends Observable implements Runnable {

    private Log log = LogFactory.getLog(ServerNetworkInterface.class);

    protected GameInfo info;

    protected HashMap<InetSocketAddress, Player> player;
    protected ArrayList<Packet> pendingPackets;

    protected DatagramSocket serverSocket;
    protected int packetIdC;
    protected byte playerIdC;

    public ServerNetworkInterface(GameInfo info) throws IOException {
        serverSocket = new DatagramSocket(3157);
        serverSocket.setSoTimeout(500);
        this.info = info;
        player = new HashMap<>();
        pendingPackets = new ArrayList<>();
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
    protected boolean send(UDPPacket packet, InetSocketAddress address) {
        return send(packet, address, true);
    }
    public boolean sendAll(UDPPacket packet) {
        packet.setPacketID(generatePacketID());
        boolean b = true;
        for (Player p : player.values()) {
            b &= send(packet, p.getAddress());
        }
        return b;
    }
    public boolean sendOthers(UDPPacket packet, Player p) {
        packet.setPacketID(generatePacketID());
        boolean b = true;
        for (Player player1 : player.values()) {
            if (player1 != p) {
                b &= send(packet, player1.address);
            }
        }
        return b;
    }
    protected void sendServerMessage(String msg) {
        byte[] byteMessage = UDPPacket.serialize(msg);
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
            long time = player.get(key).getElapsedTime().toMillis();
            if (time > 33000) {
                //@TODO notify observers of disconnection event
                player.remove(key);
            } else if (time > 3000) {
                //@TODO notify observers of not responding player
                UDPPacket packet = new UDPPacket(UDPPacket.TYPE_LEAVE);
                send(packet, key, false);
            }
        }
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
                checkConnections();
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
                    continue;
                } else if (packet.getType() == UDPPacket.TYPE_ACK) {
                    for (Packet p : pendingPackets) {
                        //remove the acknowledged packet
                        if (p.getAddress().equals(address) && p.getPacketID() == packet.getPacketID()) {
                            pendingPackets.remove(p);
                            break;
                        }
                    }
                    continue; //continue with next packet
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
                            break;
                        case UDPPacket.TYPE_LEAVE:
                            onLeave(packet, p);
                            break;
                        case UDPPacket.TYPE_JOIN:
                            //DCed player joined again
                            break;
                    }
                    continue; //continue with next packet
                }

                //non-player reply
                if (packet.getType() == UDPPacket.TYPE_STATUS) {
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_SERVER_INFO);
                    response.setPacketID(choosePacketId(packet));

                    byte[] name = UDPPacket.serialize(info.getServerName());
                    byte[] payload = new byte[name.length+3];

                    //copy server status
                    payload[0] = (byte) player.size();
                    payload[1] = (byte) info.getMaxPlayer();
                    payload[2] = 0;
                    if (!info.getPassword().isEmpty()) {
                        payload[2] = 1; //set password flag
                    }

                    //copy server name
                    System.arraycopy(name,0,payload,3,name.length);

                    //send packet
                    response.setPayload(payload);
                    send(response, address);
                } else if (packet.getType() == UDPPacket.TYPE_JOIN) {
                    String errorMsg = "No player slots available!";
                    if (player.size() < info.getMaxPlayer()) {
                        String name = UDPPacket.deserializeString(packet.getPayload());
                        String pass = UDPPacket.deserializeString(packet.getPayload(), name.length());//@Todo hash password with username
                        if (!info.getPassword().isEmpty() && info.getPassword().equals(pass)) {
                            //add new player
                            Player p = new Player(++playerIdC, name, address);
                            player.put(address, p);

                            //prepare join packet
                            UDPPacket response = new UDPPacket(UDPPacket.TYPE_JOIN);
                            byte[] bName = UDPPacket.serialize(p.getName());
                            byte[] payload = new byte[1+bName.length];
                            payload[0] = p.getId();
                            System.arraycopy(bName,0,payload,1,bName.length);
                            response.setPayload(payload);
                            sendAll(response);

                            //send player-list
                            response = new UDPPacket(UDPPacket.TYPE_PLAYER_LIST);
                            response.setPacketID(generatePacketID());
                            ArrayList<Byte>  player_list = new ArrayList<>();
                            player_list.add((byte)player.size());
                            //add all player
                            for (Player player1 : player.values()) {
                                player_list.add(player1.getId());
                                for (byte b : UDPPacket.serialize(player1.getName())) {
                                    player_list.add(b);
                                }
                            }
                            payload = new byte[player_list.size()];
                            int i = 0;
                            for (Byte aByte : player_list) {
                                payload[i++] = aByte;
                            }
                            response.setPayload(payload);
                            send(response, address);

                            continue; //continue with next packet
                        } else {
                            errorMsg = "Wrong Password!";
                        }
                    }
                    //Send NAck
                    UDPPacket response = new UDPPacket(UDPPacket.TYPE_NACK);
                    response.setPacketID(choosePacketId(packet));
                    byte[] payload = UDPPacket.serialize(errorMsg);
                    response.setPayload(payload);
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
        byte[] data = packet.getPayload();
        String msg = UDPPacket.deserializeString(data, 1);
        byte[] byteMessage = UDPPacket.serialize(msg);

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
            UDPPacket response = new UDPPacket(UDPPacket.TYPE_CHAT, generatePacketID());
            payload[1] = 1; //set private message
            response.setPayload(payload);
            send(response, to.address);
            return;
        }

        UDPPacket response = new UDPPacket(UDPPacket.TYPE_CHAT);
        payload[1] = 0; //not a private message
        response.setPayload(data);
        sendOthers(response, p);
    }

    protected void onLeave(UDPPacket packet, Player p) {
        if (p.id == 1) {
            byte id = packet.getPayload()[0];
            Player kickPlayer = null;
            for (Player searchPlayer : player.values()) {
                if (searchPlayer.id == id) {
                    kickPlayer = searchPlayer;
                    break;
                }
            }
            if (kickPlayer == null) {
                UDPPacket response = new UDPPacket(UDPPacket.TYPE_ERROR);
                response.setPacketID(generatePacketID());
                
            }
            String msg = UDPPacket.deserializeString(packet.getPayload(), 1);
        }
    }
}
