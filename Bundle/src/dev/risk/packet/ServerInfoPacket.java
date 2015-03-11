package dev.risk.packet;

import dev.risk.game.GameInfo;
import dev.risk.game.Player;

import java.util.Collection;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class ServerInfoPacket extends UDPPacket {

    public ServerInfoPacket(GameInfo info, Collection<Player> p) {
        super(TYPE_SERVER_INFO);

        byte[] serverName = serialize(info.getServerName());
        byte[] mapName = serialize(info.getMap().getName());
        byte[] data = new byte[3+serverName.length+mapName.length];

        //copy player info
        data[0] = (byte) p.size();
        data[1] = (byte) info.getMaxPlayer();

        //set password flag
        if (!info.getPassword().isEmpty())
            data[2] = 1;

        //copy server name
        System.arraycopy(serverName,0,data,3,serverName.length);
        //copy map name
        System.arraycopy(mapName,0,data,3+serverName.length,mapName.length);


        setPayload(data);
    }
}
