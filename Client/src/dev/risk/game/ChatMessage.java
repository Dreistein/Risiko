package dev.risk.game;

import java.time.Instant;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class ChatMessage {

    private Instant time;
    private boolean isPrivate;
    private String message;
    private Player player;

    public ChatMessage(Player p, boolean isPrivate, String message) {
        this(Instant.now(), p, isPrivate, message);
    }

    public ChatMessage(Instant time, Player p, boolean isPrivate, String message) {
        this.player = p;
        this.time = time;
        this.isPrivate = isPrivate;
        this.message = message;
    }

    public Instant getTime() {
        return time;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getMessage() {
        return message;
    }

    public Player getPlayer() {
        return player;
    }
}
