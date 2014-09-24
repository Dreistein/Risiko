package dev.risk.network.packet;

import java.time.Instant;
import java.util.UUID;

/**
 * 11.09.2014
 *
 * @author Dreistein
 */
public class PongPacket {

    protected Instant timestamp;
    protected UUID uuid;

    public PongPacket(PingPacket packet) {
        timestamp = Instant.now();
        uuid = packet.uuid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }
}
