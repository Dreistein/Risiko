package dev.risk.game;

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class GameInfo {
    protected int maxPlayer;
    protected String serverName;
    protected Map map;
    protected String password;

    public GameInfo(int maxPlayer, String gameName, Map map) {
        this.maxPlayer = maxPlayer;
        this.serverName = gameName;
        this.map = map;
        this.password = "";
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
