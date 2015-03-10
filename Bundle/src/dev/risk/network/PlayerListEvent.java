package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class PlayerListEvent extends Event {

    private Map<Integer, String> player;

    public PlayerListEvent(InetSocketAddress sender, UDPPacket original, Map<Integer, String> player) {
        super(sender, original);
        this.player = player;
    }

    public Map<Integer, String> getPlayer() {
        return player;
    }
}
