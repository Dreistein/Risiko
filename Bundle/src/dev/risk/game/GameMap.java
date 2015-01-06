package dev.risk.game;

import com.sun.istack.internal.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * 24.09.2014
 *
 * @author Dreistein
 */
public class GameMap {

    protected static GameMap defaultMap;
    protected static Log log = LogFactory.getLog(GameMap.class);

    public static GameMap getDefault() throws IOException {
        if (defaultMap == null) {
            try {
                URL url = GameMap.class.getResource("/standard.map");
                defaultMap = loader.load(url);
            } catch (Exception e) {
                String msg = "Couldn't load map!";
                log.error(msg, e);
                throw new IOException(msg, e);
            }
        }
        return defaultMap;
    }

    public static class loader {

        protected static Log log = LogFactory.getLog(loader.class);

        public static GameMap load(URL mapRes) throws IOException {
            String s = "";
            try (Scanner sc = new Scanner(mapRes.openStream())) {
                sc.useDelimiter("\\A");
                s = sc.next();
            } catch (IOException e) {
                log.error("Could not load Map-File", e);
                throw e;
            }
            if (s.isEmpty())
                throw new IOException("The specified file is empty!");
            return parse(s);
        }

        public static GameMap parse(@NotNull String s) {
            if (s == null || s.isEmpty()) {
                throw new IllegalArgumentException("The String to parse mustn't be empty!");
            }
            return null;
        }
    }
}
