import dev.risk.packet.UDPPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 20.01.2015
 *
 * @author Dreistein
 */
public class DiscoverServers {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        UDPPacket udpPacket = new UDPPacket(UDPPacket.TYPE_STATUS, 0x15);
        byte[] data = udpPacket.serialize();
        DatagramPacket packet = new DatagramPacket(data, data.length);
        packet.setAddress(InetAddress.getByAddress(new byte[]{(byte)255,(byte)255,(byte)255,(byte)255}));
        packet.setPort(3157);

        socket.send(packet);
        byte[] buffer;
        while (!Thread.interrupted()) {
            try {
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                udpPacket = new UDPPacket(packet.getData());

                if (udpPacket.getType() != UDPPacket.TYPE_ACK) {
                    UDPPacket ackPacket = new UDPPacket(UDPPacket.TYPE_ACK);
                    ackPacket.setPacketID(udpPacket.getPacketID());
                    byte[] ackdata = ackPacket.serialize();
                    DatagramPacket ack = new DatagramPacket(ackdata, ackdata.length);
                    ack.setAddress(packet.getAddress());
                    ack.setPort(packet.getPort());
                    socket.send(ack);
                }

                System.out.println(packet.getSocketAddress());

                if (udpPacket.getType() == UDPPacket.TYPE_SERVER_INFO) {
                    byte[] payload = udpPacket.getPayload();
                    int player = payload[0];
                    int maxplayer = payload[1];

                    byte gametype = (byte) (payload[2] >> 4);
                    boolean password = ((1 & payload[2]) > 0);

                    String prot;
                    if (password) {
                        prot = "yes";
                    } else {
                        prot = "no";
                    }

                    String name = UDPPacket.deserializeString(payload, 3);

                    System.out.printf("%s: %d/%d Password: %s%n",name,player,maxplayer,prot);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
