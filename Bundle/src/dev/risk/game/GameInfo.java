package dev.risk.game;

/**
 * 10.01.2015
 *
 * @author Dreistein
 */
public class GameInfo {
    protected int maxPlayer;
    protected int actualPlayer;
    protected String gameName;
    protected Map map;
    protected String password;

    public GameInfo(int maxPlayer, String gameName, Map map) {
        this.maxPlayer = maxPlayer;
        this.gameName = gameName;
        this.map = map;
        this.password = "";
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getActualPlayer() {
        return actualPlayer;
    }

    public void setActualPlayer(int actualPlayer) {
        this.actualPlayer = actualPlayer;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
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
