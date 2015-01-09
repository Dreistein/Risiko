import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * 11.09.2014
 *
 * @author Dreistein
 */
public class PolygonTest extends JPanel {

    public static void main(String[] args) throws  Exception {
        InputStream in = PolygonTest.class.getResourceAsStream("polygons.txt");

        Scanner sc = new Scanner(in);

        ArrayList<Polygon> p = new ArrayList<>();

        while (sc.hasNextLine()) {
            Polygon polygon = new Polygon();
            String line = sc.nextLine();
            String[] coordinates = line
                    .substring(line.indexOf('(')+1, line.indexOf(')'))
                    .replace(",","")
                    .split(" ");

            for (int i = 0; i < coordinates.length; i++) {
                try {
                    polygon.addPoint(Integer.valueOf(coordinates[i]),Integer.valueOf(coordinates[++i]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            p.add(polygon);
        }

        JFrame frame = new JFrame("Polygon Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(new PolygonTest(p));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(3);
    }


    protected ArrayList<Polygon> polygons;

    public PolygonTest(Collection<Polygon> p) {
        polygons = new ArrayList<>(p);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        for (Polygon polygon : polygons) {
            g2.draw(polygon);
        }
    }
}
