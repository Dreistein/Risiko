package dev.risk;

import dev.risk.gui.BackgroundPanel;
import dev.risk.gui.ServerList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 11.02.2015
 *
 * @author Dreistein
 */
public class Risk implements ActionListener {

    JFrame frame;
    Image bgImage;

    JPanel mainPanel;
    ServerList joinPanel;

    public Risk() {
        frame = new JFrame("Risiko v1.0");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            bgImage = ImageIO.read(Risk.class.getResource("/bg800x600.png"));
        } catch (Exception e) {
            e.printStackTrace(); //@Todo replace with log
        }

        mainPanel = (bgImage != null) ? new BackgroundPanel(bgImage) : new JPanel();
        mainPanel.setLayout(new GridLayout(4,1));
        ((GridLayout)mainPanel.getLayout()).setVgap(10);
        mainPanel.setBorder(new EmptyBorder(150, 200, 150, 200));

        JButton button = new JButton("Neues Spiel");
        button.setActionCommand("newGame");
        button.addActionListener(this);
        mainPanel.add(button);

        button = new JButton("Spiel beitreten");
        button.setActionCommand("joinGame");
        button.addActionListener(this);
        mainPanel.add(button);

        button = new JButton("Optionen");
        button.setActionCommand("options");
        button.addActionListener(this);
        mainPanel.add(button);

        button = new JButton("Beenden");
        button.setActionCommand("exit");
        button.addActionListener(this);
        mainPanel.add(button);

        frame.add(mainPanel);
        frame.invalidate();
        frame.validate();

        frame.setSize(new Dimension(806, 628));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            frame.createBufferStrategy(3);
        } catch (Exception e) {
            e.printStackTrace(); //@Todo replace with log
        }


        joinPanel = new ServerList();
        joinPanel.addConnectActionListener(this);
        joinPanel.addBackActionListener(this);
    }


    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        new Risk();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "newGame":
                break;
            case "joinGame":
                frame.setContentPane(joinPanel);
                break;
            case "back":
                frame.setContentPane(mainPanel);
                break;
            case "exit":
                System.exit(0);
        }
        frame.invalidate();
        frame.validate();
    }
}
