package dev.risk.server;

import dev.risk.packet.UDPPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class Packet {

    private InetSocketAddress address;
    private Instant time;
    private UDPPacket packet;

    public Packet(InetSocketAddress address, UDPPacket packet) {
        this.address = address;
        this.packet = packet;
        time = Instant.now();
    }

    protected InetSocketAddress getAddress() {
        return address;
    }

    protected int getPacketID() {
        return packet.getPacketID();
    }

    protected UDPPacket getPacket() {
        return packet;
    }

    protected Duration getElapsedTime() {
        return Duration.between(time, Instant.now()).abs();
    }
}
