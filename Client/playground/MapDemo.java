import dev.risk.game.Map;
import dev.risk.gui.MapPane;

import javax.swing.*;
import java.awt.*;

/**
 * 11.01.2015
 *
 * @author Dreistein
 */
public class MapDemo extends JPanel {

    public static void main(String[] args) {
        Map m = Map.getDefault();

        JFrame frame = new JFrame("Map Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(new MapPane(m));

        frame.setSize(new Dimension(930, 530));

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(3);
    }
}
