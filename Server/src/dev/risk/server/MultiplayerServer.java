package dev.risk.server;

import dev.risk.game.GameMap;
import dev.risk.game.Player;

import dev.risk.packet.UDPPacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Stack;

/**
 * 06.01.2015
 *
 * @author Dreistein
 */
public class MultiplayerServer {

    private Log log = LogFactory.getLog(MultiplayerServer.class);

    private String name;            //String containing Server name
    private Stack<Player> player;   //Stack containing all Players
    private int maxPlayer = 4;      //maximum amount of player slots
    private GameMap map;            //Current Game map

    private Thread serverLoop;

    protected MultiplayerServer() {

    }

    protected void init() throws IOException {
        if (map == null) {
            map = GameMap.getDefault();
        }
        player = new Stack<>();
        try {
            final DatagramSocket serverSocket = new DatagramSocket(3157);
            final byte[] receiveData = new byte[1024];
            serverLoop = new Thread(() -> {
                Thread t = Thread.currentThread();
                while (!t.isInterrupted()) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        log.info("received packet");
                        byte[] data = receivePacket.getData();
                        UDPPacket request = new UDPPacket(data);
                        UDPPacket answear;
                        switch (request.getType()) {
                            case UDPPacket.TYPE_PING:
                                answear = new UDPPacket(UDPPacket.TYPE_PING);
                                break;
                            case UDPPacket.TYPE_STATUS_REQ:
                                log.info("Received a status request!");
                                answear = new UDPPacket(UDPPacket.TYPE_STATUS);
                                byte[] nameBytes = (name+'\0').getBytes();
                                byte[] payloadData = new byte[nameBytes.length+2];
                                payloadData[0] = (byte)player.size();
                                payloadData[1] = (byte)maxPlayer;
                                System.arraycopy(nameBytes,0,payloadData,2,nameBytes.length);
                                answear.setPayload(payloadData);
                                break;
                            case UDPPacket.TYPE_JOIN_REQ:
                                answear = new UDPPacket(UDPPacket.TYPE_JOIN_NACK);
                                break;
                            default:
                                answear = new UDPPacket(UDPPacket.TYPE_ERROR);
                        }
                        if (request.getRequestID() != 0)
                            answear.setRequestID(request.getRequestID());

                        InetAddress addr = receivePacket.getAddress();
                        int port = receivePacket.getPort();
                        byte[] sendData = answear.serialize();
                        DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, addr, port);
                        serverSocket.send(answearPacket);
                    } catch (IOException e) {
                        log.error("Failed to receive Packet!", e);
                    } catch (IllegalArgumentException e) {
                        log.info("Received a wrong Packet!");
                        //@Todo answear with a error packet
                    }
                }
                serverSocket.close();
            });
            serverLoop.start();
        } catch (SocketException e) {
            String msg = "Could not connect to Socket!";
            log.error(msg, e);
            throw new IOException(msg, e);
        }
    }

    public static class Factory {
        MultiplayerServer server;

        public Factory(String serverName) {
            server = new MultiplayerServer();
            server.name = serverName;
        }
        public MultiplayerServer create() throws IOException {
            server.init();
            return server;
        }

        public Factory withMap(GameMap map) {
            server.map = map;
            return this;
        }
        public Factory withMaxPlayer(int amount) {
            if (!(amount > 1 && amount <= 10)) {
                throw new IllegalArgumentException("Player amount must be in range 2 to 10!");
            }
            server.maxPlayer = amount;
            return this;
        }
    }
}
