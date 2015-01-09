package dev.risk.server;

import dev.risk.game.GameMap;

import dev.risk.packet.UDPPacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * 06.01.2015
 *
 * @author Dreistein
 */
public class MultiplayerServer {

    private Log log = LogFactory.getLog(MultiplayerServer.class);

    private String name;                        //String containing Server name
    private String password;                    //password string
    private HashMap<Integer, Player> player;    //Stack containing all Players
    private int maxPlayer = 4;                  //maximum amount of player slots
    private GameMap map;                        //Current Game map

    private Thread loopThread;
    private ServerLoop loop;
    private DatagramSocket serverSocket;

    protected MultiplayerServer() {

    }

    protected void init() throws IOException {
        if (map == null) {
            map = GameMap.getDefault();
        }
        player = new HashMap<>();
        try {
            serverSocket = new DatagramSocket(3157);
            loop = new ServerLoop();
            loopThread = new Thread(loop);
            loopThread.start();
        } catch (SocketException e) {
            String msg = "Could not connect to Socket!";
            log.error(msg, e);
            throw new IOException(msg, e);
        }
    }

    protected void sendAll(UDPPacket packet) throws IOException {
        for (Player p : player.values()) {
            sendPlayer(packet, p);
        }
    }
    protected void sendPlayer(UDPPacket packet, Player p) throws IOException {
        byte[] sendData = packet.serialize();
        DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, p.getAddress(), p.getPort());
        serverSocket.send(answearPacket);
    }


    class ServerLoop implements Runnable {

        @Override
        public void run() {
            Thread t = Thread.currentThread();
            byte[] receiveBuffer = new byte[2048];
            while (!t.isInterrupted()) {
                try {
                    //receive Packet
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);
                    log.debug("Received Packet");
                    byte[] data = receivePacket.getData();
                    UDPPacket request = new UDPPacket(data);

                    //process Packet
                    switch (request.getType()) {
                        //ping request
                        case UDPPacket.TYPE_PING:
                            onPing(receivePacket, request);
                            break;
                        //server status request
                        case UDPPacket.TYPE_STATUS_REQ:
                            onStatus(receivePacket, request);
                            break;
                        //join request
                        case UDPPacket.TYPE_JOIN_REQ:
                            onJoinRequest(receivePacket, request);
                            break;
                        default:
                            UDPPacket answer = new UDPPacket(UDPPacket.TYPE_ERROR);
                            InetAddress addr = receivePacket.getAddress();
                            int port = receivePacket.getPort();
                            byte[] sendData = answer.serialize();
                            DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, addr, port);
                            serverSocket.send(answearPacket);
                    }

                } catch (IOException e) {
                    log.error("Failed to receive Packet!", e);
                } catch (IllegalArgumentException e) {
                    log.info("Received a wrong Packet!");
                }
            }
            serverSocket.close();
        }

        protected void onPing(DatagramPacket packet, UDPPacket request) throws IOException {
            UDPPacket answer = new UDPPacket(UDPPacket.TYPE_PING); //ping back
            if (request.getRequestID() != 0)
                answer.setRequestID(request.getRequestID());
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            byte[] sendData = answer.serialize();
            DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, addr, port);
            serverSocket.send(answearPacket);
        }
        protected void onStatus(DatagramPacket packet, UDPPacket request) throws IOException {
            UDPPacket answer = new UDPPacket(UDPPacket.TYPE_STATUS);
            byte[] nameBytes = (name+'\0').getBytes(); //server name
            byte[] payloadData = new byte[nameBytes.length+3];
            payloadData[0] = (byte)player.size();   //set amount of current player
            payloadData[1] = (byte)maxPlayer;       //set amount of maximum player
            payloadData[2] = 0x00;
            if (!"".equals(password)) { //if password isn't empty
                payloadData[2] &= (byte)0x80;  //set password flag
            }
            System.arraycopy(nameBytes,0,payloadData,3,nameBytes.length); //append server name
            answer.setPayload(payloadData);

            if (request.getRequestID() != 0)
                answer.setRequestID(request.getRequestID());
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            byte[] sendData = answer.serialize();
            DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, addr, port);
            serverSocket.send(answearPacket);
        }
        protected void onJoinRequest(DatagramPacket packet, UDPPacket request) throws IOException {
            UDPPacket answer;
            if (player.size() < maxPlayer) {
                byte[] payload = request.getPayload();
                String name = getStringSz(payload);
                String pass = getStringSz(payload, name.length());
                if (!password.isEmpty() && !password.equals(pass)) {
                    answer = new UDPPacket(UDPPacket.TYPE_JOIN_NACK);
                } else {
                    int i = 0; //generate new player id
                    for (;;i++) {
                        boolean hasID = false;
                        for (Player p : player.values()) {
                            if (p.id == i) {
                                hasID = true;
                                break;
                            }
                        }
                        if (!hasID) {
                            break;
                        }
                    }
                    Player p = new Player(i, name, packet.getAddress(), packet.getPort());
                    player.put(i, p);

                    payload = new byte[name.length()+2];
                    payload[0] = (byte)i;
                    payload[payload.length-1] = 0; //null terminated string
                    System.arraycopy(name.getBytes(), 0, payload, 1, name.length());

                    answer = new UDPPacket(UDPPacket.TYPE_JOIN);
                    answer.setPayload(payload);
                    sendAll(answer);
                }
            } else {
                answer = new UDPPacket(UDPPacket.TYPE_JOIN_NACK);
                if (request.getRequestID() != 0)
                    answer.setRequestID(request.getRequestID());
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                byte[] sendData = answer.serialize();
                DatagramPacket answearPacket = new DatagramPacket(sendData, sendData.length, addr, port);
                serverSocket.send(answearPacket);
            }
        }
        protected String getStringSz(byte[] data) {
            return getStringSz(data, 0);
        }
        protected String getStringSz(byte[] data, int startpos){
            int i = startpos;
            for (;i<data.length;i++) {
                if (data[i] == 0)
                    break;
            }
            byte[] string = Arrays.copyOfRange(data, startpos,i);
            return new String(string);
        }
    }

    public static class Factory {
        MultiplayerServer server;

        public Factory(String serverName) {
            server = new MultiplayerServer();
            server.name = serverName;
            server.password = "";
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
        public Factory withPassword(String pass) {
            server.password = pass;
            return this;
        }
    }
}
