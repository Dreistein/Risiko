package dev.risk.server.game;

import dev.risk.game.Map;
import dev.risk.game.Player;

import java.util.List;

/**
 * 28.01.2015
 *
 * @author Dreistein
 */
public abstract class Setup {
    public abstract Map setupGame(List<Player> p, Map m);
}
