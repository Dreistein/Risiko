package dev.risk.gui;

import dev.risk.game.Player;
import dev.risk.game.ServerInfo;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class LobbyPane extends JPanel {

    private JButton startButton;
    private JButton cancelButton;
    private ChatPane chatPane;
    private JPanel mainPanel;

    private ServerInfo info;

    public LobbyPane(ServerInfo info, Collection<Player> playerList, boolean host, ChatPane chatPane) {
        this.chatPane = chatPane;
        this.info = info;
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());

        //control panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (host) {
            cancelButton = new JButton("Abbrechen");
            panel.add(cancelButton);
            startButton = new JButton("Spiel starten");
            startButton.setActionCommand("start game");
            panel.add(startButton);
        } else {
            cancelButton = new JButton("Server verlassen");
            panel.add(cancelButton);
            startButton = new JButton();
        }
        cancelButton.setActionCommand("cancel game");
        mainPanel.add(panel, BorderLayout.SOUTH);


        //info pane
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        GridBagConstraints cs = new GridBagConstraints();

        cs.ipady = 10;
        cs.insets = new Insets(0,15,0,10);

        cs.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel("Server Info");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD).deriveFont(17f));
        panel.add(lbl, cs);

        cs.anchor = GridBagConstraints.EAST;
        cs.gridy = 1;
        panel.add(new JLabel("Servername:"), cs);
        cs.gridy = 2;
        panel.add(new JLabel("Karte:"), cs);
        cs.gridy = 3;
        panel.add(new JLabel("Spieler:"), cs);
        cs.gridy = 4;
        panel.add(new JLabel("Max. Spieler:"), cs);
        cs.gridy = 5;
        panel.add(new JLabel("Passwort:"), cs);

        cs.insets = new Insets(0,0,0,15);
        cs.anchor = GridBagConstraints.WEST;
        cs.gridx = 1;
        cs.gridy = 1;
        panel.add(new JLabel(info.getName()), cs);
        cs.gridy = 2;
        panel.add(new JLabel(info.getMapName()), cs);
        cs.gridy = 3;
        panel.add(new JLabel(String.valueOf(info.getPlayer())), cs);
        cs.gridy = 4;
        panel.add(new JLabel(String.valueOf(info.getMaxPlayer())), cs);
        cs.gridy = 5;
        panel.add(new JLabel((info.isPassword()) ? "Ja" : "Nein"), cs);

        mainPanel.add(panel, BorderLayout.EAST);

        updatePlayerList(playerList);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, chatPane);
        add(splitPane);
        splitPane.setDividerLocation(0.95d);
    }

    public void updatePlayerList(Collection<Player> players) {
        //playerlist
        String[] columnNames = {"Id", "Name"};
        String[][] playerData = new String[info.getMaxPlayer()][2];

        int i = 0;
        for (Player player : players) {
            playerData[i][0] = String.valueOf(player.getId());
            playerData[i++][1] = player.getName();
        }

        mainPanel.add(
                new JScrollPane(
                        new JTable(playerData, columnNames),
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                ),
                BorderLayout.CENTER
        );
    }

    public void addControlListener(ActionListener listener) {
        startButton.addActionListener(listener);
        cancelButton.addActionListener(listener);
    }

    public void removeControlListener(ActionListener listener) {
        startButton.removeActionListener(listener);
        cancelButton.removeActionListener(listener);
    }

    public ChatPane getChatPane() {
        return chatPane;
    }

    public void setChatPane(ChatPane chatPane) {
        this.chatPane = chatPane;
    }
}
