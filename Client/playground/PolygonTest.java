import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 11.09.2014
 *
 * @author Dreistein
 */
public class PolygonTest extends JPanel {

    public static void main(String[] args) throws  Exception {
        InputStream in = PolygonTest.class.getResourceAsStream("map.svg");
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        String svg = scanner.hasNext() ? scanner.next() : "";

        Document doc = Jsoup.parseBodyFragment(svg);

        Element path = doc.getElementsByTag("path").first();

        String data = path.attr("d");

        Pattern p = Pattern.compile("(-?\\d+(.\\d+)?),(-?\\d+(.\\d+)?)");
        Matcher m = p.matcher(data);

        Polygon polygon = new Polygon();
        while (m.find()) {
            double x = Double.valueOf(m.group(1)) * 10;
            double y = Double.valueOf(m.group(3)) * 10;
            polygon.addPoint((int) x, (int) y);
        }

        JFrame frame = new JFrame("Polygon Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(new PolygonTest(polygon));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(3);
    }


    protected Polygon polygon;

    public PolygonTest(Polygon p) {
        polygon = p;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.draw(polygon);
    }
}
