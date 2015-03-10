package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class LeaveEvent extends Event {

    public static final int REQUEST     = -1;
    public static final int DISCONNECT  = 0;
    public static final int DC          = 0;
    public static final int STANDARD    = 1;
    public static final int KICK        = 2;

    private int playerId;
    private int type;
    private String message;

    public LeaveEvent(InetSocketAddress sender, UDPPacket original, int playerId, String message) {
        super(sender, original);
        this.playerId = playerId;
        type = REQUEST;
        this.message = message;
    }

    public LeaveEvent(InetSocketAddress sender, UDPPacket original, int playerId, int type, String message) {
        super(sender, original);
        this.playerId = playerId;
        this.type = type;
        this.message = message;
    }

    public boolean isRequest() {
        return type == REQUEST;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
