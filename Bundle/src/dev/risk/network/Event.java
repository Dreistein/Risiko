package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class Event {
    protected InetSocketAddress sender;
    protected UDPPacket original;

    public Event(InetSocketAddress sender, UDPPacket original) {
        this.sender = sender;
        this.original = original;
    }

    public InetSocketAddress getSender() {
        return sender;
    }

    public UDPPacket getOriginal() {
        return original;
    }
}
