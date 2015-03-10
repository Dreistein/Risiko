package dev.risk.network;

import dev.risk.packet.UDPPacket;

import java.net.InetSocketAddress;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class SetupEvent extends Event {

    private int[][] countries;

    public SetupEvent(InetSocketAddress sender, UDPPacket original, int[][] countries) {
        super(sender, original);
    }

    /**
     * Countries are formatted in a matrix int[m][n]. The first index m specifies the country id, the second j specifies the data.<br />
     * index m = 0 gives the playerId which owns the country<br />
     * index m = 1 gives the troops in the country
     * @return the country matrix
     */
    public int[][] getCountries() {
        return countries;
    }
}
