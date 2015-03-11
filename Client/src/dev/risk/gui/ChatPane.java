package dev.risk.gui;

import dev.risk.game.ChatMessage;
import dev.risk.game.Player;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Observer;
import java.util.Stack;

/**
 * 10.03.2015
 *
 * @author Dreistein
 */
public class ChatPane extends JPanel {

    private static final String htmlWrapper = "<html><head><style>body {font-family:\"Lucida Console\"} .prefix {width:130px} .even {background-color:#e9e9e9;} .odd {background-color:#ffffff} .private {color: #0000ff}</style></head><body><table border=\"0\" style=\"width:100%\">$data</table></body></html>";
    private static final String templateRow = "<tr class=\"$rowID\"><td class=\"prefix\" style=\"background-color:$color;\">$name ($time)</td><td><div class=\"$type\">$message</div></td></tr>";

    private Stack<ChatMessage> messages;
    private DateTimeFormatter formatter;

    private JEditorPane editorPane;
    private JTextField sendField;
    private JButton sendButton;

    private Observer listener;
    private Player p;
    
    public ChatPane(Player p, Observer messageListener) {
        this.listener = messageListener;
        this.p = p;
        messages = new Stack<>();
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

        this.setLayout(new BorderLayout());

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        this.add(
                new JScrollPane(
                        editorPane,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                )
        );

        JPanel sendPanel = new JPanel(new BorderLayout());
        this.add(sendPanel, BorderLayout.SOUTH);
        sendField = new JTextField();
        sendPanel.add(sendField);
        sendButton = new JButton("Senden...");
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> sendMessage());
        sendField.addActionListener(e -> sendMessage());
    }

    protected void sendMessage() {
        if (sendField.getText().isEmpty())
            return;
        ChatMessage m = new ChatMessage(p, false, sendField.getText());
        sendField.setText("");
        listener.update(null, m);
    }

    public void addMessage(ChatMessage m) {
        messages.push(m);

        String data = "";
        int i = 0;
        for (ChatMessage message : messages) {
            String row = templateRow;
            Player p = message.getPlayer();

            row = row.replace("$rowID", ((i++ % 2) == 0) ? "even" : "odd");
            String color = "rgb($r,$g,$b)"
                    .replace("$r", String.valueOf(p.getColor().getRed()))
                    .replace("$g", String.valueOf(p.getColor().getGreen()))
                    .replace("$b", String.valueOf(p.getColor().getBlue()));
            row = row.replace("$color", color);
            row = row.replace("$time", formatter.format(message.getTime()));
            row = row.replace("$type", (message.isPrivate()) ? "private" : "");
            row = row.replace("$name", message.getPlayer().getName());
            data += row.replace("$message", message.getMessage());
        }
        editorPane.setText(htmlWrapper.replace("$data", data));
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        super.setLayout(mgr);
    }
}
