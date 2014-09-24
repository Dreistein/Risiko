package dev.risk.network.packet;

import java.time.Instant;
import java.util.UUID;

/**
 * 11.09.2014
 *
 * @author Dreistein
 */
public class PingPacket {

    protected Instant timestamp;
    protected UUID uuid;

    public PingPacket() {
        timestamp = Instant.now();
        uuid = UUID.randomUUID();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }
}
