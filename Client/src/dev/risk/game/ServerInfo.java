package dev.risk.game;

import java.net.SocketAddress;

/**
 * 10.02.2015
 *
 * @author Dreistein
 */
public class ServerInfo {

    protected String name;
    protected int player;
    protected int maxPlaye;
    protected boolean password;
    protected String mapName;
    protected SocketAddress address;

    public ServerInfo(String name, int player, int maxPlayer, boolean password, String mapName, SocketAddress address) {
        this.name = name;
        this.player = player;
        this.maxPlaye = maxPlayer;
        this.password = password;
        this.mapName = mapName;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getMaxPlayer() {
        return maxPlaye;
    }

    public boolean isPassword() {
        return password;
    }

    public String getMapName() {
        return mapName;
    }

    public SocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                ", player=" + player +
                ", maxPlayer=" + maxPlaye +
                ", password=" + password +
                ", mapName='" + mapName + '\'' +
                ", address=" + address +
                '}';
    }
}
