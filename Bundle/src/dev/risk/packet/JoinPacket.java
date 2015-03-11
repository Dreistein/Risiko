package dev.risk.packet;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class JoinPacket extends UDPPacket {
    public JoinPacket(byte id, String name) {
        super(TYPE_JOIN);

        byte[] bName = serialize(name);
        byte[] data = new byte[1+bName.length];

        //copy data
        data[0] = id;
        System.arraycopy(bName,0,data,1,bName.length);

        setPayload(data);
    }

    public JoinPacket(String name, String password) {
        super(TYPE_JOIN);

        byte[] bName = serialize(name);
        byte[] pass = serialize(password);
        byte[] data = new byte[bName.length+pass.length];

        System.arraycopy(bName,0,data,0,bName.length);
        System.arraycopy(pass,0,data,bName.length,pass.length);

        setPayload(data);
    }
}
