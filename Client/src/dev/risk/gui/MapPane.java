package dev.risk.gui;

import dev.risk.game.Country;
import dev.risk.game.Map;
import dev.risk.game.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * 12.01.2015
 *
 * @author Dreistein
 */
public class MapPane extends JPanel {
    int width = 930;
    int height = 480;
    BufferedImage staticMap;
    Map map;

    HashMap<Polygon, Country> table;
    Country hover = null;
    Country selected = null;

    public MapPane(Map m) {
        map = m;
        BufferedImage im = new BufferedImage(1240, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) im.getGraphics();
        g2.setColor(Color.DARK_GRAY);
        for (Polygon p : map.getLines()) {
            g2.draw(p);
        }
        for (Country c : map.getCountries().values()) {
            for (Polygon p : c.getArea()) {
                g2.draw(p);
            }
        }
        for (Text t : map.getTexts()) {
            g2.drawString(t.getText(), t.getCoordinates().x, t.getCoordinates().y);
        }
        AffineTransform at = new AffineTransform();
        at.scale(0.75,0.75);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        staticMap = scaleOp.filter(im, staticMap);

        table = new HashMap<>();
        for (Country c : m.getCountries().values()) {
            for (Polygon p : c.getArea()) {
                table.put(p, c);
            }
        }

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Point p = e.getPoint();
                    p.x /= 0.75;
                    p.y /= 0.75;
                    onClick(p);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                p.x /= 0.75;
                p.y /= 0.75;
                onMove(p);
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        BufferedImage im = new BufferedImage(1240,640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) im.getGraphics();

        for (Country c : map.getCountries().values()) {
            if (c == hover || c == selected) {
                continue;
            }
            g2.setColor(map.getContinent(c.getContinent()).getColor());
            for (Polygon p : c.getArea()) {
                g2.fillPolygon(p);
            }
        }

        if (hover != null) {
            g2.setColor(map.getContinent(hover.getContinent()).getHover());
            for (Polygon p : hover.getArea()) {
                g2.fillPolygon(p);
            }
        }
        if (selected != null) {
            g2.setColor(map.getContinent(selected.getContinent()).getSelected());
            for (Polygon p : selected.getArea()) {
                g2.fillPolygon(p);
            }
        }

        for (Country c : map.getCountries().values()) {
            g2.setColor(Color.WHITE);
            int x = c.getPointsPoint().x-10;
            int y = c.getPointsPoint().y-10;
            g2.fillArc(x,y,20,20,0,360);
            g2.setColor(Color.DARK_GRAY);
            g2.drawArc(x, y, 20, 20, 0, 360);
            g2.drawString("30",x+5,y+15);
        }

        AffineTransform at = new AffineTransform();
        at.scale(0.75,0.75);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rendered = new BufferedImage(1240,640,BufferedImage.TYPE_INT_ARGB);
        rendered = scaleOp.filter(im, rendered);
        g.drawImage(rendered, 0,0,width,height,0,0,930,480, (img, infoflags, x, y, width, height) -> true);
        g.drawImage(staticMap, 0,0,width,height,0,0,staticMap.getWidth(),staticMap.getHeight(), (img, infoflags, x, y, width, height) -> true);
    }

    public void onClick(Point p) {
        Country c = table.get(getInBounds(p));
        if (c != selected) {
            selected = c;
            repaint();
            System.out.println(c.getName());
        }
    }

    public void onMove(Point p) {
        Country c = table.get(getInBounds(p));
        if (c != hover) {
            hover = c;
            repaint();
        }
    }

    protected Polygon getInBounds(Point p) {
        for (Polygon polygon : table.keySet()) {
            if (polygon.contains(p)) {
                return polygon;
            }
        }
        return null;
    }
}
