package dev.risk.game;

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
    protected Continent continent;

    protected Player owner;
    protected int troops;

    protected Country(int id, String name) {
        neighbors = new ArrayList<>();
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

    public Continent getContinent() {
        return continent;
    }

    public class Builder {

        Country country;

        public Builder(int id, String name) {
            country = new Country(id, name);
        }

        public Builder addNeighbor(int n) {
            country.neighbors.add(n);
            return this;
        }

        public Builder setContinent(Continent c) {
            country.continent = c;
            return this;
        }

        public Country create() {
            return country;
        }
    }
}
