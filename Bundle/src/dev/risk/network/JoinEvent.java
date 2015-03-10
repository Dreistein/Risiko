package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class JoinEvent extends Event {

    private int id = -1;
    private String playerName;
    private String password = "";

    public JoinEvent(InetSocketAddress sender, UDPPacket original, String name, String password) {
        super(sender, original);
        this.playerName = name;
        this.password = password;
    }

    public JoinEvent(InetSocketAddress sender, UDPPacket original, int id, String playerName) {
        super(sender, original);
        this.id = id;
        this.playerName = playerName;
    }

    public boolean isRequest() {
        return id == -1;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPassword() {
        return password;
    }
}
