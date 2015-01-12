package dev.risk.game;

import java.awt.*;
import java.util.ArrayList;
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
    protected Color hover;
    protected Color selected;

    protected Continent(int id, String name, int bonus) {
        this.id = id;
        this.name = name;
        this.bonus = bonus;
        this.countries = new ArrayList<>();
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

    public Color getHover() {
        return hover;
    }

    public Color getSelected() {
        return selected;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public static class Builder {
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

        public Builder setColor(Color c) {
            continent.color = c;
            return this;
        }

        public Builder setHover(Color c) {
            continent.hover = c;
            return this;
        }

        public Builder setSelected(Color c) {
            continent.selected = c;
            return this;
        }

        public Continent create() {
            return continent;
        }
    }
}
