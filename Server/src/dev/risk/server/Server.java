package dev.risk.server;

import dev.risk.game.GameInfo;
import dev.risk.game.Player;
import dev.risk.network.*;
import dev.risk.packet.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

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
    byte uId = 1;

    public Server(GameInfo info) throws IOException {
        this.info = info;
        ni = new ServerNetworkInterface();
        ni.addObserver(this);
        serverThread = new Thread(ni);
        serverThread.start();
        player = new HashMap<>();
    }

    public void stop() {
        serverThread.interrupt();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof JoinEvent) {
            JoinEvent joinEvent = (JoinEvent) arg;

            boolean join = false;
            if (player.size() < info.getMaxPlayer()) {
                if (info.getPassword().isEmpty() || info.getPassword().equals(joinEvent.getPassword())) {
                    join = true;
                    byte id = uId++;

                    Player p = new Player(id, joinEvent.getPlayerName(), joinEvent.getSender());
                    player.put(joinEvent.getSender(), p);
                    UDPPacket packet = new JoinPacket(id, p.getName());
                    ni.sendAll(packet, player.values());
                    packet = new PlayerListPacket(player.values());
                    ni.sendAll(packet, player.values());
                }
            }
            if (!join) {
                ni.denyRequest(joinEvent.getOriginal(), joinEvent.getSender());
            }
        } else if (arg instanceof ChatEvent) {
            ChatEvent chatEvent = (ChatEvent) arg;

            //don't accept messages from non-players
            if (!player.containsKey(chatEvent.getSender())) {
                ni.denyRequest(chatEvent.getOriginal(), chatEvent.getSender());
                return;
            }

            if (chatEvent.isPrivate()) {
                Player receiver = null;
                for (Player candidate : player.values()) {
                    if (candidate.getId() == chatEvent.getReceiverId()) {
                        receiver = candidate;
                        break;
                    }
                }
                if (receiver != null) {
                    UDPPacket packet = new ChatPacket(
                            player.get(chatEvent.getSender()),
                            chatEvent.getMessage(),
                            true
                    );
                    ni.ackPacket(chatEvent.getOriginal(), chatEvent.getSender());
                    ni.send(packet, receiver);
                } else {
                    UDPPacket packet = new ChatPacket("Player not found!");
                    packet.setPacketID(chatEvent.getOriginal());
                    ni.send(packet, chatEvent.getSender());
                }
            } else {
                UDPPacket packet = new ChatPacket(
                        player.get(chatEvent.getSender()),
                        chatEvent.getMessage(),
                        false
                );
            }
        } else if (arg instanceof Event) {
            Event event = (Event) arg;
            switch (event.getOriginal().getType()) {
                case UDPPacket.TYPE_STATUS:
                    UDPPacket info = new ServerInfoPacket(this.info, player.values());
                    ni.send(info, event.getSender());
                    break;
                case UDPPacket.TYPE_PING:
                    if (player.containsKey(event.getSender())) {
                        player.get(event.getSender()).resetElapsedTime();
                    }
                    break;
            }
        }
    }
}
