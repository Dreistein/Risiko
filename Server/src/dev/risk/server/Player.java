package dev.risk.server;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

/**
 * 07.01.2015
 *
 * @author Dreistein
 */
public class Player {
    protected byte id;
    protected InetSocketAddress address;
    protected String name;
    protected Instant lastPacket;

    public Player(byte id, String name, InetSocketAddress addr) {
        this.id = id;
        this.name = name;
        this.address = addr;
        resetElapsedTime();
    }

    public byte getId() {
        return id;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public Duration getElapsedTime() {
        return Duration.between(lastPacket, Instant.now()).abs();
    }

    public void resetElapsedTime() {
        lastPacket = Instant.now();
    }
}
