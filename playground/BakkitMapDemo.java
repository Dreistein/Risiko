import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;

/**
 * 10.09.2014
 *
 * @author Dreistein
 */
public class BakkitMapDemo {

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("SVG Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JSVGCanvas svgCanvas = new JSVGCanvas();
        svgCanvas.setURI("https://raw.githubusercontent.com/jonase/mlx/master/resources/worldmap.svg");
        frame.add(svgCanvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(3);
    }
}
