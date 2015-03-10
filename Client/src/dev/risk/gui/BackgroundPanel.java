package dev.risk.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class BackgroundPanel extends JPanel {

    private Image image;

    public BackgroundPanel(Image im) {
        image = im;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
