package dev.risk.server.game;

import dev.risk.game.Country;
import dev.risk.game.Map;
import dev.risk.game.Player;

import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 28.01.2015
 *
 * @author Dreistein
 */
public class AlternateSetup extends Setup {
    @Override
    public Map setupGame(List<Player> p, Map m) {
        Random r = new Random();

        Stack<Country> countries = new Stack<>();
        countries.addAll(m.getCountries().values());
        int i = 0;
        while (countries.size() > 0) {
            Country c = countries.remove(r.nextInt(countries.size()));
            c.setOwner(p.get(i));
            c.setTroops(1);
            if (++i >= p.size()) {
                i = 0;
            }
        }
        int troops;
        switch (p.size()) {
            case 2:
                troops = 40;
                break;
            case 3:
                troops = 35;
                break;
            case 4:
                troops = 30;
                break;
            case 5:
                troops = 25;
                break;
            case 6:
            default:
                troops = 20;
                break;
        }

        for (Player player : p) {
            player.setPendingTroops(troops);
        }
        return m;
    }
}
