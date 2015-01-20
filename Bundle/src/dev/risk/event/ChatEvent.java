package dev.risk.event;

import dev.risk.game.Player;

import java.time.Instant;

/**
 * 17.01.2015
 *
 * @author Dreistein
 */
public class ChatEvent extends Event {

    public static final String name = "EVENT_CHAT";

    protected String message;
    protected Player sender;
    protected Player receiver;

    public ChatEvent(String message, Player sender, Instant time) {
        this(message, sender, null, time);
    }

    public ChatEvent(String message, Player sender, Player receiver, Instant time) {
        super(name, new Object[0]);
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.when = time;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
