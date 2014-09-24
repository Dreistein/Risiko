package dev.risk.network.packet;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 11.09.2014
 *
 * @author Dreistein
 */
public class PingResult {

    protected UUID pingId;
    protected InetAddress address;

    protected Instant requestTime;
    protected Instant responseTime;
    protected Instant returnTime;

    public PingResult(InetAddress address, PingPacket ping, PongPacket pong) {
        returnTime = Instant.now();
        if (address == null) {
            throw new IllegalArgumentException("Param adress mustn't be null!");
        }
        if (ping == null) {
            throw new IllegalArgumentException("Param ping mustn't be null!");
        }
        if (pong == null) {
            throw new IllegalArgumentException("Param pong mustn't be null!");
        }
        if (ping.uuid != pong.uuid) {
            throw new IllegalArgumentException("The UUID's of ping and pong are not matching!");
        }

        pingId = ping.uuid;
        this.address = address;

        requestTime = ping.getTimestamp();
        responseTime = pong.getTimestamp();
    }

    public UUID getPingId() {
        return pingId;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public Duration getTimeToResponse() {
        return Duration.between(requestTime, responseTime);
    }

    public Duration getTimeToReturn() {
        return Duration.between(requestTime, returnTime);
    }

    @Override
    public String toString() {
        return "Ping from " + address.getHostAddress() + ": time=" + getTimeToReturn().toMillis() + "ms";
    }
}
