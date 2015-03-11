package dev.risk.gui;

import dev.risk.game.Client;
import dev.risk.game.ServerInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class MainFrame implements ActionListener {

    private JFrame frame;
    private Image bgImage;

    private JPanel mainPanel;
    private ServerList joinPanel;
    private LobbyPane lobbyPane;
    private ChatPane chatPane;

    private Client client;

    public MainFrame() {
        frame = new JFrame("Risiko v1.0");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            bgImage = ImageIO.read(MainFrame.class.getResource("/bg800x600.png"));
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
        button.setActionCommand("gameList");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "newGame":
                break;
            case "gameList":
                frame.setContentPane(joinPanel);
                break;
            case "back":
                frame.setContentPane(mainPanel);
                break;
            case "exit":
                System.exit(0);
                break;
            case "connectGame":
                ServerInfo info = joinPanel.getSelectedServer();
                String pw = "";
                if (info.isPassword()) {
                    TextInputDialog tid = new TextInputDialog(new JFrame(), "Passworteingabe", "Geben Sie das Passwort ein:");
                    tid.setLocationRelativeTo(frame);
                    tid.setVisible(true);

                    pw = tid.getText();

                    if (pw == null)
                        return;
                }

                final String pass = pw;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client = new Client(info, pass);
                            chatPane = new ChatPane(client.getSelf(), client);
                            lobbyPane = new LobbyPane(info, client.getPlayer(), false, chatPane);
                            lobbyPane.addControlListener(MainFrame.this);
                            frame.setContentPane(lobbyPane);
                            frame.invalidate();
                            frame.validate();
                        } catch(Exception ex) {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Couldn't connect to Server!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }).start();
                return;
            case "cancelGame":
                client.stop();
                client = null;
                lobbyPane = null;
                chatPane = null;
                frame.setContentPane(mainPanel);
        }
        frame.invalidate();
        frame.validate();
    }
}
