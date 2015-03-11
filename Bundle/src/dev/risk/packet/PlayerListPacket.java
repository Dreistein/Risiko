package dev.risk.packet;

import dev.risk.game.Player;

import java.util.Collection;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class PlayerListPacket extends UDPPacket {
    public PlayerListPacket(Collection<Player> players) {
        super(TYPE_PLAYER_LIST);

        byte[] buffer = new byte[1024];
        buffer[0] = (byte) players.size();
        int length = 1;

        for (Player player : players) {
            buffer[length++] = player.getId();
            byte[] name = serialize(player.getName());
            System.arraycopy(name,0,buffer,length,name.length);
            length += name.length;
        }

        byte[] data = new byte[length];
        System.arraycopy(buffer,0,data,0,length);
        setPayload(data);
    }
}
