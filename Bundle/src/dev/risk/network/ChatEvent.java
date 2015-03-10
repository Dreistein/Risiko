package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class ChatEvent extends Event {

    private int senderId = -1;
    private int receiverId;
    private String message;

    public ChatEvent(InetSocketAddress sender, UDPPacket original, String message) {
        this(sender, original, message, 0);

    }
    public ChatEvent(InetSocketAddress sender, UDPPacket original, String message, int receiverId) {
        super(sender, original);
        this.message = message;
        this.receiverId = receiverId;
    }

    public ChatEvent(InetSocketAddress sender, UDPPacket original, int senderId, int receiverId, String message) {
        super(sender, original);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public boolean isPrivate() {
        return receiverId != 0;
    }

    public boolean isRequest() {
        return senderId == -1;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }
}
