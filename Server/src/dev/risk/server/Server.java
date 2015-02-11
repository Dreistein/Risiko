package dev.risk.server;

import dev.risk.event.Event;
import dev.risk.event.Observable;
import dev.risk.event.Observer;
import dev.risk.game.GameInfo;
import dev.risk.game.Player;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * 17.01.2015
 *
 * @author Dreistein
 */
public class Server implements Observer {

    private GameInfo info;
    private ServerNetworkInterface ni;
    private Thread serverThread;

    private HashMap<InetSocketAddress, Player> player;
    private int playerIdC;

    public Server(GameInfo info) {
        this.info = info;

    }


    @Override
    public void update(Observable sender, Event e) {

    }
}
