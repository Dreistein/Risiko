package dev.risk.game;

import java.util.*;
import java.util.Map;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class Client implements Observer {

    private java.util.Map<Integer, Player> player;
    private byte playerId;

    private ClientNetworkInterface ni;

    public Client(ServerInfo info, String password) {

    }

    public Player getSelf() {
        return player.get(playerId);
    }

    public Collection<Player> getPlayer() {
        return player.values();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void stop() {

    }
}
