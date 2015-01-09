package dev.risk.server;

import java.net.InetAddress;

/**
 * 07.01.2015
 *
 * @author Dreistein
 */
public class Player {
    protected int id;
    protected InetAddress address;
    protected int port;
    protected String name;

    public Player(int id, String name, InetAddress addr, int port) {
        this.id = id;
        this.port = port;
        this.name = name;
        this.address = addr;
    }

    public int getId() {
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }
}
