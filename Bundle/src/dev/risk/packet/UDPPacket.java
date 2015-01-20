package dev.risk.packet;

import com.sun.istack.internal.NotNull;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

/**
 * 06.01.2015
 *
 * @author Dreistein
 */
public class UDPPacket {

    public static final byte[] prefix = new byte[] {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)82, (byte)73, (byte)83, (byte)75, (byte)0x00};
    public static final byte TYPE_PING          = 0x00;
    public static final byte TYPE_ACK           = 0x01;
    public static final byte TYPE_NACK          = 0x02;
    public static final byte TYPE_SERVER_INFO   = 0x03;
    public static final byte TYPE_STATUS        = 0x04;
    public static final byte TYPE_PLAYER_LIST   = 0x05;
    public static final byte TYPE_JOIN          = 0x06;
    public static final byte TYPE_LEAVE         = 0x07;
    public static final byte TYPE_CHAT          = 0x08;
    public static final byte TYPE_GAME_EVENT    = 0x09;
    public static final byte TYPE_ERROR         = 0x0F;

    protected byte type;
    protected int packetID;
    protected Instant time;
    protected byte[] payload;


    public UDPPacket(byte[] data) throws IllegalArgumentException {
        if (!isRiskPacket(data))
            throw new IllegalArgumentException("Not a Risk Packet!");
        type = data[8];

        ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 9, 9+8));
        time = Instant.ofEpochMilli(bb.getLong());

        bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 17, 17+4));
        packetID = bb.getInt();

        byte[] paylenBy = new byte[4];
        Arrays.fill(paylenBy, (byte)0);
        System.arraycopy(data,21,paylenBy,1,3);
        bb = ByteBuffer.wrap(paylenBy);
        int payloadLength = bb.getInt();

        if (payloadLength > 0) {
            payload = Arrays.copyOfRange(data, 24, 24 + payloadLength);
        } else {
            payload = new byte[0];
        }
    }
    public UDPPacket(byte type) {
        this(type, 0);
    }
    public UDPPacket(byte type, int packetID) {
        this(type, packetID, Instant.now());
    }
    public UDPPacket(byte type, int packetID, Instant time) {
        this.type = type;
        this.packetID = packetID;
        this.time = time;
        payload = new byte[0];
    }

    protected boolean isRiskPacket(byte[] data) {
        if (data.length < 24) //the header size is 16 bytes
            return false;

        for (int i = 0; i<prefix.length; i++) {
            if (data[i] != prefix[i])
                return false;
        }
        return true;
    }
    public byte[] serialize() {
        byte[] data = new byte[24+payload.length];
        Arrays.fill(data, (byte)0);

        //prefix
        System.arraycopy(prefix, 0, data, 0, prefix.length);
        //request type
        data[8] = type;

        //copy time
        System.arraycopy(serialize(time.toEpochMilli()), 0, data, 9, 8);
        //packet ID
        byte[] packetIDb = ByteBuffer.allocate(4).putInt(packetID).array();
        System.arraycopy(packetIDb,0,data,17,4);

        //payload
        if (payload.length > 0) {
            byte[] paylenb = ByteBuffer.allocate(4).putInt(payload.length).array();
            System.arraycopy(paylenb, 1, data, 21, 3);
            System.arraycopy(payload,0,data,16,payload.length);
        }

        return data;
    }

    public byte getType() {
        return type;
    }
    public int getPacketID() {
        return packetID;
    }
    public Instant getTime() {
        return time;
    }
    public byte[] getPayload() {
        return payload;
    }

    public void setPacketID(int requestID) {
        this.packetID = requestID;
    }
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    //helper
    public static byte[] serialize(@NotNull String s) {
        return (s+'\0').getBytes();
    }
    public static byte[] serialize(int n) {
        ByteBuffer bb = ByteBuffer.allocate(4).putInt(n);
        return bb.array();
    }
    public static byte[] serialize(long n) {
        ByteBuffer bb = ByteBuffer.allocate(8).putLong(n);
        return bb.array();
    }
    public static String deserializeString(byte[] b) {
        return deserializeString(b, 0);
    }
    public static String deserializeString(byte[] b, int startpos) {
        int i = startpos;
        for (; i < b.length; i++) {
            if (b[i] == 0) {
                byte[] data = Arrays.copyOfRange(b,startpos,i);
                return new String(data);
            }
        }
        return "";
    }
    public static int deserializeInt(byte[] b) {
        byte[] data = Arrays.copyOf(b, 4);
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getInt();
    }

}
