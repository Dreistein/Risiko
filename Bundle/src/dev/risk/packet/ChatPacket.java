package dev.risk.packet;

import dev.risk.game.Player;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class ChatPacket extends UDPPacket {

    public ChatPacket(Player sender, String message, boolean isPrivate) {
        super(TYPE_CHAT);
        byte[] msg = serialize(message);
        byte[] data = new byte[msg.length+2];

        data[0] = sender.getId();
        data[1] = (byte)(isPrivate ? 1 : 0);
        System.arraycopy(msg,0,data,2,msg.length);

        setPayload(data);
    }

    public ChatPacket(String message) {
        super(TYPE_CHAT);

        byte[] msg = serialize(message);
        byte[] data = new byte[msg.length+2];

        data[0] = 0;
        data[1] = 0;
        System.arraycopy(msg,0,data,2,msg.length);

        setPayload(data);
    }
}
