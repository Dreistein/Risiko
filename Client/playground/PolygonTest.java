import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;

/**
 * 11.09.2014
 *
 * @author Dreistein
 * edited 10.01.2015
 * @author Jakob
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


        PolygonTest ptest = new PolygonTest(p);
        frame.add(ptest);

        frame.getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    ptest.setClickPoint(e.getPoint());
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(3);
    }


    protected ArrayList<Polygon> polygons;
    protected Polygon highlighted;

    public PolygonTest(Collection<Polygon> p) {
        polygons = new ArrayList<>(p);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        //draw polygons
        g2.setColor(Color.CYAN);
        polygons.forEach(g2::fillPolygon);
        g2.setColor(Color.BLACK);
        polygons.forEach(g2::draw);
        //draw highlighted polygon
        if (highlighted != null) {
            g2.setColor(Color.RED);
            g2.draw(highlighted);
        }
    }

    public void setClickPoint(Point p) {
        highlighted = null;
        for (Polygon polygon : polygons) {
            if (polygon.contains(p)) {
                highlighted = polygon;
            }
        }
        repaint();
    }
}
