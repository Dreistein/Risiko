package dev.risk.game;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class ClientNetworkInterface implements Runnable {

    ServerInfo serverInfo;
    DatagramSocket socket;

    public ClientNetworkInterface(ServerInfo si) throws IOException{
        serverInfo = si;
        socket = new DatagramSocket();
    }

    @Override
    public void run() {

    }
}
