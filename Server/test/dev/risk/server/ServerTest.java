package dev.risk.server;

import dev.risk.game.GameInfo;
import dev.risk.game.Map;
import dev.risk.packet.UDPPacket;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class ServerTest extends TestCase {

    Thread server;
    DatagramSocket socket;
    int port;
    String gameName = "TESTGAME";
    String password = "TESTPASS";

    public void setUp() throws Exception {
        super.setUp();
        port = 3157;
        GameInfo info = new GameInfo(2, gameName, Map.getDefault());
        info.setPassword(password);
        server = new Thread(new ServerNetworkInterface(info));
        server.start();
        socket = new DatagramSocket();
        socket.setSoTimeout(10000);
    }

    @Test
    public void testPing() {
        try {
            UDPPacket pingPacket = new UDPPacket(UDPPacket.TYPE_PING);
            byte[] expected = pingPacket.serialize();
            byte[] actual;

            DatagramPacket packet = new DatagramPacket(expected, expected.length);
            packet.setPort(port);
            packet.setAddress(InetAddress.getLocalHost());
            socket.send(packet);

            socket.receive(packet);
            actual = packet.getData();

            org.junit.Assert.assertArrayEquals("False Ping return!", expected, actual);

        } catch (IOException e) {
            org.junit.Assert.fail("Socket timed out!");
        }
    }

    public void tearDown() throws Exception {
        server.interrupt();
        socket.close();
    }
}