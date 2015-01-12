package dev.risk.game;

import java.awt.*;

/**
 * 11.01.2015
 *
 * @author Dreistein
 */
public class Text {

    protected String text;
    protected Point coordinates;
    protected Color color = Color.DARK_GRAY;

    public Text(String text, Point coordinates) {
        this.text = text;
        this.coordinates = coordinates;
    }

    public String getText() {
        return text;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
