package dev.risk.game;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * 24.09.2014
 *
 * @author Dreistein
 */
public class Continent {

    protected int id;
    protected String name;
    protected List<Country> countries;
    protected int bonus;
    protected Color color;

    protected Continent(int id, String name, int bonus) {
        this.id = id;
        this.name = name;
        this.bonus = bonus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBonus() {
        return bonus;
    }

    public Color getColor() {
        return color;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public class Builder {
        protected Continent continent;

        public Builder(int id, String name, int bonus) {
            continent = new Continent(id, name, bonus);
        }

        public Builder setBonus(int b) {
            continent.bonus = b;
            return this;
        }

        public Builder addCountry(Country c) {
            continent.countries.add(c);
            return this;
        }

        public Builder addCountries(Collection<Country> c) {
            continent.countries.addAll(c);
            return this;
        }

        public Continent create() {
            return continent;
        }
    }
}
