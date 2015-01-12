package dev.risk.game;

import com.sun.istack.internal.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

/**
 * 24.09.2014
 *
 * @author Dreistein
 */
public class Map {

    protected static Map defaultMap;
    protected static Log log = LogFactory.getLog(Map.class);

    protected String name;
    protected HashMap<Integer, Country> countries;
    protected HashMap<Integer, Continent> continents;
    protected ArrayList<Text> texts;
    protected ArrayList<Polygon> lines;

    public static Map getDefault() {
        if (defaultMap == null) {
            try {
                URL url = Map.class.getResource("/standard.map");
                defaultMap = loader.load(url);
            } catch (Exception e) {
                String msg = "Couldn't load map!";
                log.error(msg, e);
                return null;
            }
        }
        return defaultMap;
    }

    protected Map() {
        countries = new HashMap<>();
        continents = new HashMap<>();
        texts = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, Country> getCountries() {
        return countries;
    }

    public HashMap<Integer, Continent> getContinents() {
        return continents;
    }

    public ArrayList<Text> getTexts() {
        return texts;
    }

    public ArrayList<Polygon> getLines() {
        return lines;
    }

    public Country getCountry(int id) {
        return countries.get(id);
    }

    public Continent getContinent(int id) {
        return continents.get(id);
    }

    public static class loader {

        protected static Log log = LogFactory.getLog(loader.class);

        public static Map load(URL mapRes) throws IOException {
            String s = "";
            try (Scanner sc = new Scanner(mapRes.openStream())) {
                sc.useDelimiter("\\A"); //get the whole string
                s = sc.next();
            } catch (IOException e) {
                log.error("Could not load Map-File", e);
                throw e;
            }
            if (s.isEmpty())
                throw new IOException("The specified file is empty!");
            return parse(s);
        }

        public static Map parse(@NotNull String s) {
            if (s == null || s.isEmpty()) {
                throw new IllegalArgumentException("The String to parse mustn't be empty!");
            }
            s = s.replace("\r","");
            if (!(s.contains("\n-territory-\n") && s.contains("\n-continent-\n") && s.contains("\n-text-\n") && s.contains("\n-line-"))) {
                throw new IllegalArgumentException("The given Map is not valid!");
            }
            Map m = new Map();
            String[] lines = s.split("\n");
            m.name = lines[0];
            if (!"-continent-".equals(lines[1]))
                throw new IllegalArgumentException("The given Map is not valid!");

            Stack<Integer[]> countries = new Stack<>();
            Stack<Continent.Builder> builder = new Stack<>();
            HashMap<Integer,Integer> mapping = new HashMap<>();
            int i = 2;
            for (; i < lines.length; i++) {
                String line = lines[i];
                if ("-territory-".equals(line)) {
                    i++;
                    break;
                }
                String[] fragments = line.split(":");
                int id = Integer.parseInt(fragments[0]);
                int bonus = Integer.parseInt(fragments[2]);
                Continent.Builder b = new Continent.Builder(id, fragments[1], bonus);
                b.setColor(Color.decode(fragments[4].replace("#","0x")));   //color
                b.setHover(Color.decode(fragments[5].replace("#","0x")));   //hover
                b.setSelected(Color.decode(fragments[6].replace("#","0x")));//selected

                String[] countryIds = fragments[3]
                        .replace("{", "").replace("}","")
                        .split(",");
                Integer[] ids = new Integer[countryIds.length];
                for (int j = 0; j < countryIds.length; j++) {
                    ids[j] = Integer.parseInt(countryIds[j]);
                    mapping.put(ids[j], id);
                }
                countries.push(ids);
                builder.push(b);
            }
            for (; i < lines.length; i++) {
                String line = lines[i];
                if ("-text-".equals(line)) {
                    i++;
                    break;
                }
                String[] fragments = line.split(":");
                int id = Integer.parseInt(fragments[0]);
                Country.Builder b = new Country.Builder(id, fragments[1]);
                String neighbors[] = fragments[2]
                        .replaceAll("[\\{}]", "")
                        .split(",");
                for (String neighbor : neighbors) {
                    b.addNeighbor(Integer.parseInt(neighbor));
                }
                String[] polygons = fragments[3].split(",polygon");
                for (String polygon : polygons) {
                    Polygon p = new Polygon();
                    polygon = polygon.replaceAll("(polygon|[(),])", "");
                    String[] coords = polygon.split(" ");
                    for (int j = 0; j < coords.length; j++) {
                        p.addPoint(Integer.parseInt(coords[j]),Integer.parseInt(coords[++j]));
                    }
                    b.addArea(p);
                }
                b.setContinent(mapping.get(id));
                m.countries.put(id, b.create());
            }
            for (Integer[] country : countries) {
                Continent.Builder b = builder.remove(0);
                for (Integer id : country) {
                    b.addCountry(m.getCountry(id));
                }
                Continent c = b.create();
                m.continents.put(c.getId(), c);
            }
            for (; i < lines.length; i++) {
                String line = lines[i];
                if ("-line-".equals(line)) {
                    i++;
                    break;
                }
                String[] fragments = line.split(":");
                Point point = new Point(Integer.parseInt(fragments[0]),Integer.parseInt(fragments[1]));
                m.texts.add(new Text(fragments[2], point));
            }
            for (; i < lines.length; i++) {
                String line = lines[i];
                String[] fragments = line.split(":");
                Polygon p = new Polygon();
                p.addPoint(Integer.valueOf(fragments[0]),Integer.valueOf(fragments[1]));
                p.addPoint(Integer.valueOf(fragments[2]),Integer.valueOf(fragments[3]));
                m.lines.add(p);
            }
            return m;
        }
    }
}
