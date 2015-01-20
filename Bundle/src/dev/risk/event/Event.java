package dev.risk.event;

import java.time.Instant;

/**
 * 17.01.2015
 *
 * @author Dreistein
 */
public class Event {

    public String eventName;
    public Instant when;
    public Object[] args;

    public Event(String eventName, Object[] args) {
        this.when = Instant.now();
        this.eventName = eventName;
        this.args = args;
    }

    public String getEventName() {
        return eventName;
    }

    public Instant getWhen() {
        return when;
    }

    public Object[] getArgs() {
        return args;
    }
}
