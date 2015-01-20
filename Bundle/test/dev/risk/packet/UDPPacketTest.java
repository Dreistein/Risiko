package dev.risk.packet;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Random;

public class UDPPacketTest extends TestCase {
    byte[] bytePacket = new byte[] {-1,-1,-1,82,73,83,75,0,0,0,0,1,75,8,-66,-4,47,0,0,0,20,0,0,0,};
    UDPPacket udpPacket;

    public void setUp() throws Exception {
        super.setUp();
        udpPacket = new UDPPacket(bytePacket);
    }

    public void testIsRiskPacket() throws Exception {
        boolean success = true;
        byte[] b = Arrays.copyOf(bytePacket, 13);

        success &= !udpPacket.isRiskPacket(b);

        Random r = new Random();
        b = Arrays.copyOf(bytePacket, bytePacket.length);
        b[r.nextInt(8)] = (byte)r.nextInt();

        success &= !udpPacket.isRiskPacket(b);

        success &= udpPacket.isRiskPacket(bytePacket);

        assertTrue("failure - RISK UDP packet recognition failed!", success);
    }

    public void testSerialize() throws Exception {
        org.junit.Assert.assertArrayEquals(bytePacket, udpPacket.serialize());
    }

    public void testSerializeString() throws Exception {
        byte[] expected = new byte[] {82,73,83,75,0};
        org.junit.Assert.assertArrayEquals(expected, UDPPacket.serialize("RISK"));
    }

    public void testSerializeInt() throws Exception {
        byte[] expected = new byte[] {0x00,0x07,(byte)0xA1,(byte)0xA2};
        org.junit.Assert.assertArrayEquals(expected, UDPPacket.serialize(500130));
    }

    public void testSerializeLong() throws Exception {
        byte[] expected = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00,0x07,(byte)0xA1,(byte)0xA2};
        org.junit.Assert.assertArrayEquals(expected, UDPPacket.serialize(500130L));
    }

    public void testDeserializeString() throws Exception {
        assertEquals("RISK", UDPPacket.deserializeString(new byte[]{1,33,17,58,1,82,73,83,75,0},5));
    }

    public void testDeserializeInt() throws Exception {
        int i = UDPPacket.deserializeInt(new byte[] {0x00,0x07,(byte)0xA1,(byte)0xA2});
        int result = i-500130;
        org.junit.Assert.assertTrue(result == 0);
    }
}