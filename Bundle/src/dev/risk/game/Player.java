package dev.risk.game;

import java.awt.*;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

/**
 * 07.01.2015
 *
 * @author Dreistein
 */
public class Player {
    /**
     * Unique Player ID assigned by Server
     */
    protected byte id;

    /**
     * The network address identifying the player; used by server only
     */
    protected InetSocketAddress address;

    /**
     * The String representing the name of the player
     */
    protected String name;

    /**
     * Time of last packet received by player; used by server only
     */
    protected Instant lastPacket;

    /**
     * Amount of troops the player is able to assign to his countries
     */
    protected int pendingTroops;

    /**
     * Player Color;
     */
    protected Color color;

    public Player(byte id, String name) {
        this(id, name, null);
    }

    public Player(byte id, String name, InetSocketAddress addr) {
        this.id = id;
        this.name = name;
        this.address = addr;

        Random r = new Random();
        switch (id) {
            case 0:
                color = Color.LIGHT_GRAY;
                break;
            case 1:
                color = Color.decode("0xffff00");
                break;
            case 2:
                color = Color.decode("0x00babc");
                break;
            case 3:
                color = Color.decode("0x9afff6e");
                break;
            case 4:
                color = Color.magenta;
                break;
            case 5:
                color = Color.decode("0xff7800");
                break;
            case 6:
                color = Color.decode("0x00a504");
                break;
            default:
                color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
        }
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

    public int getPendingTroops() {
        return pendingTroops;
    }

    public void setPendingTroops(int pendingTroops) {
        this.pendingTroops = pendingTroops;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
