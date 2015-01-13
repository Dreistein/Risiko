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
    Country second = null;

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
                Point p = e.getPoint();
                p.x /= 0.75;
                p.y /= 0.75;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    onClick(p, true);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    onClick(p, false);
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
        BufferedImage im2 = new BufferedImage(1240,640, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) im.getGraphics();
        Graphics2D g3 = (Graphics2D) im2.getGraphics();

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
            if (second != null) {
                int x1 = selected.getPointsPoint().x;
                int y1 = selected.getPointsPoint().y;
                int dx = second.getPointsPoint().x - x1;
                int dy = second.getPointsPoint().y - y1;

                double phi = Math.atan2(dy,dx); //angle between points
                double len = selected.getPointsPoint().distance(second.getPointsPoint()); //distance between points
                double ux = dx/len; //unit vector x
                double uy = dy/len; //unit vector y

                double urx = 10*Math.cos(phi + Math.PI / 2); //right-hand orthogonal unit vector x
                double ury = 10*Math.sin(phi + Math.PI / 2); //right-hand orthogonal unit vector y
                double ulx = 10*Math.cos(phi - Math.PI / 2); //left-hand orthogonal unit vector x
                double uly = 10*Math.sin(phi - Math.PI / 2); //left-hand orthogonal unit vector y

                Polygon arrow = new Polygon();
                arrow.addPoint((int)(x1+ux*15+urx),(int)(y1+uy*15+ury));
                arrow.addPoint((int)(x1+ux*15+ulx),(int)(y1+uy*15+uly));
                arrow.addPoint((int)(x1+ux*(len-45)+ulx),(int)(y1+uy*(len-45)+uly));
                arrow.addPoint((int)(x1+ux*(len-45)+3*ulx),(int)(y1+uy*(len-45)+3*uly));
                arrow.addPoint((int)(x1+ux*(len-15)),(int)(y1+uy*(len-15)));
                arrow.addPoint((int)(x1+ux*(len-45)+3*urx),(int)(y1+uy*(len-45)+3*ury));
                arrow.addPoint((int)(x1+ux*(len-45)+urx),(int)(y1+uy*(len-45)+ury));

                g3.setColor(Color.RED);
                g3.fillPolygon(arrow);
                g3.setColor(Color.DARK_GRAY);
                g3.draw(arrow);
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
        rendered = scaleOp.filter(im2, rendered);
        g.drawImage(rendered, 0,0,width,height,0,0,930,480, (img, infoflags, x, y, width, height) -> true);

    }

    public void onClick(Point p, boolean left) {
        Country c = table.get(getInBounds(p));
        if(left) {
            if (c != selected) {
                selected = c;
                repaint();
            }
        } else {
            if (c != second) {
                second = c;
                repaint();
            }
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
