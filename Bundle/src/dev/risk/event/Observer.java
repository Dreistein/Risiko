package dev.risk.event;

/**
 * 17.01.2015
 *
 * @author Dreistein
 */
@Deprecated
public interface Observer {
    public void update(Observable sender, Event e);
}
