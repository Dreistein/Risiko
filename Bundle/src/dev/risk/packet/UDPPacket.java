package dev.risk.packet;

import java.nio.ByteBuffer;
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
    public static final byte TYPE_STATUS_REQ    = 0x02;
    public static final byte TYPE_STATUS        = 0x03;
    public static final byte TYPE_JOIN_REQ      = 0x04;
    public static final byte TYPE_JOIN          = 0x05;
    public static final byte TYPE_JOIN_NACK     = 0x06;
    public static final byte TYPE_LEAVE         = 0x07;
    public static final byte TYPE_CHAT          = 0x08;
    public static final byte TYPE_ERROR         = 0x0F;

    protected byte type;
    protected int requestID;
    protected byte[] payload;


    public UDPPacket(byte[] data) throws IllegalArgumentException {
        if (!isRiskPacket(data))
            throw new IllegalArgumentException("Not a Risk Packet!");
        type = data[8];

        ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 9, 9+4));
        requestID = bb.getInt();

        byte[] paylenBy = new byte[4];
        Arrays.fill(paylenBy, (byte)0);
        System.arraycopy(data,13,paylenBy,1,3);
        bb = ByteBuffer.wrap(paylenBy);
        int payloadLength = bb.getInt();

        if (payloadLength > 0) {
            payload = Arrays.copyOfRange(data, 16, 16 + payloadLength);
        } else {
            payload = new byte[0];
        }
    }

    public UDPPacket(byte type) {
        this.type = type;
        payload = new byte[0];
    }

    protected boolean isRiskPacket(byte[] data) {
        if (data.length < 16) //the header size is 16 bytes
            return false;

        for (int i = 0; i<prefix.length; i++) {
            if (data[i] != prefix[i])
                return false;
        }
        return true;
    }

    public byte[] serialize() {
        byte[] data = new byte[16+payload.length];
        Arrays.fill(data, (byte)0);

        //prefix
        System.arraycopy(prefix, 0, data, 0, prefix.length);
        //request type
        data[8] = type;

        //request ID
        byte[] reqIDb = ByteBuffer.allocate(4).putInt(requestID).array();
        System.arraycopy(reqIDb,0,data,9,4);

        //payload
        if (payload.length > 0) {
            byte[] paylenb = ByteBuffer.allocate(4).putInt(payload.length).array();
            System.arraycopy(paylenb, 1, data, 13, 3);
            System.arraycopy(payload,0,data,16,payload.length);
        }

        return data;
    }

    public byte getType() {
        return type;
    }

    public int getRequestID() {
        return requestID;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
