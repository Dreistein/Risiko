package dev.risk.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 24.09.2014
 *
 * @author Dreistein
 */
public class Country {

    protected int id;
    protected String name;
    protected List<Integer> neighbors;
    protected int continent;
    protected List<Polygon> area;

    protected Player owner;
    protected int troops;

    protected Country(int id, String name) {
        neighbors = new ArrayList<>();
        area = new ArrayList<>();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getNeighbors() {
        return neighbors;
    }

    public int getContinent() {
        return continent;
    }

    public Polygon[] getArea() {
        return area.toArray(new Polygon[area.size()]);
    }

    public static class Builder {

        Country country;

        public Builder(int id, String name) {
            country = new Country(id, name);
        }

        public Builder addNeighbor(int n) {
            country.neighbors.add(n);
            return this;
        }

        public Builder setContinent(int c) {
            country.continent = c;
            return this;
        }

        public Builder addArea(Polygon p) {
            country.area.add(p);
            return this;
        }

        public Country create() {
            return country;
        }
    }
}
