package dev.risk.gui;

import com.sun.corba.se.spi.activation.Server;
import dev.risk.game.ServerInfo;
import dev.risk.packet.UDPPacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 10.02.2015
 *
 * @author Dreistein
 */
public class ServerList extends JPanel {

    protected ArrayList<ServerInfo> server;
    protected JTable table;
    protected ServerListModel tableModel;
    protected JButton backButton;
    protected JButton connectButton;
    protected JLabel passLock;

    protected Lock lock = new ReentrantLock();
    protected Log log = LogFactory.getLog(ServerList.class);

    public ServerList() {
        this.setLayout(new BorderLayout());
        server = new ArrayList<>();
        tableModel = new ServerListModel();
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new CellRenderer());

        //setColumnWidths
        table.getColumnModel().getColumn(0).setMinWidth(16);
        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);

        passLock = new JLabel(new ImageIcon(ServerList.class.getResource("/lock.png")));

        this.add(new JScrollPane(table));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton("<- Back");
        controlPanel.add(backButton);

        JButton button = new JButton("Refresh");
        button.addActionListener(e -> refresh(20, ChronoUnit.SECONDS));
        controlPanel.add(button);

        connectButton = new JButton("Connect");
        controlPanel.add(connectButton);

        this.add(controlPanel, BorderLayout.SOUTH);
        refresh(20, ChronoUnit.SECONDS);
        addConnectActionListener(e -> {
            System.out.println(getSelectedServer());
        });
    }

    public void refresh(long time, TemporalUnit unit) {
        Instant end = Instant.now().plus(time, unit);
        clearServer();
        Runnable r = () -> {
            try {
                DatagramSocket socket = new DatagramSocket();

                UDPPacket udpPacket = new UDPPacket(UDPPacket.TYPE_STATUS, 0x15);
                byte[] buffer = udpPacket.serialize();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                packet.setAddress(InetAddress.getByAddress(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}));
                packet.setPort(3157);
                socket.send(packet);

                {
                    ServerInfo info = new ServerInfo("Server1 Test", 5, 8, true, "Standard", null);
                    addServer(info);
                    info = new ServerInfo("Some unprotected Server", 5, 8, false, "Standard", null);
                    addServer(info);
                    info = new ServerInfo("Low Player Server", 1, 3, false, "Standard", null);
                    addServer(info);
                    info = new ServerInfo("Almost Full Server", 7, 8, true, "Standard", null);
                    addServer(info);
                }

                while (!Duration.between(Instant.now(), end).isNegative()) {
                    try {
                        buffer = new byte[1024];
                        packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        udpPacket = new UDPPacket(packet.getData());

                        if (udpPacket.getType() != UDPPacket.TYPE_ACK) {
                            UDPPacket ackPacket = new UDPPacket(UDPPacket.TYPE_ACK);
                            ackPacket.setPacketID(udpPacket.getPacketID());
                            byte[] ackdata = ackPacket.serialize();
                            DatagramPacket ack = new DatagramPacket(ackdata, ackdata.length);
                            ack.setAddress(packet.getAddress());
                            ack.setPort(packet.getPort());
                            socket.send(ack);
                        }

                        if (udpPacket.getType() == UDPPacket.TYPE_SERVER_INFO) {
                            byte[] payload = udpPacket.getPayload();
                            int player = payload[0];
                            int maxplayer = payload[1];

                            byte gametype = (byte) (payload[2] >> 4);
                            boolean password = ((1 & payload[2]) > 0);


                            String name = UDPPacket.deserializeString(payload, 3);
                            String map = UDPPacket.deserializeString(payload, name.length()+3+1);

                            ServerInfo info = new ServerInfo(name, player, maxplayer, password, map, packet.getSocketAddress());
                            addServer(info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                log.error("Failed to refresh server list!", e);
            }
        };
        new Thread(r).start();
    }
    public ServerInfo getSelectedServer() {
        int sel = table.getSelectedRow();
        if (sel >=0) {
            return server.get(sel);
        }
        return null;
    }

    public void addBackActionListener(ActionListener l) {
        backButton.addActionListener(l);
    }
    public void removeBackActionListener(ActionListener l) {
        backButton.removeActionListener(l);
    }
    public void addConnectActionListener(ActionListener l) {
        connectButton.addActionListener(l);
    }
    public void removeConnectActionListener(ActionListener l) {
        connectButton.removeActionListener(l);
    }

    protected boolean addServer(ServerInfo info) {
        try {
            lock.tryLock(500L, TimeUnit.MILLISECONDS);
            server.add(info);
            tableModel.update();
            lock.unlock();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
    protected boolean removeServer(ServerInfo info) {
        try {
            lock.tryLock(500L, TimeUnit.MILLISECONDS);
            server.remove(info);
            tableModel.update();
            lock.unlock();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
    protected boolean clearServer() {
        try {
            lock.tryLock(500L, TimeUnit.MILLISECONDS);
            server.clear();
            tableModel.update();
            lock.unlock();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    protected class ServerListModel extends AbstractTableModel {

        public void update() {
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return server.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ServerInfo info = server.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return (info.isPassword()) ? passLock : new JLabel();
                case 1:
                    return new JLabel(info.getName());
                case 2:
                    return new JLabel(info.getMapName());
                case 3:
                    JProgressBar bar = new JProgressBar(0,info.getMaxPlayer());
                    bar.setValue(info.getPlayer());
                    bar.setString(info.getPlayer() + " / " + info.getMaxPlayer());
                    bar.setStringPainted(true);
                    return bar;
            }
            return "";
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "P";
                case 1:
                    return "Name";
                case 2:
                    return "Karte";
                case 3:
                    return "Spieler";
            }
            return "?";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
    protected class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JComponent) {
                JComponent c = (JComponent) value;
                if (isSelected) {
                    c.setBackground(new Color(0.95f,0.95f,0.95f));
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setOpaque(true);
                return c;
            }
            return new JLabel();
        }
    }
}
