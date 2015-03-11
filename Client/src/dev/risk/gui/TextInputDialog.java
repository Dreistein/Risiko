package dev.risk.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 11.03.2015
 *
 * @author Dreistein
 */
public class TextInputDialog extends JDialog implements ActionListener {


    JTextField inputField;

    String text;

    public TextInputDialog(Frame parentFrame, String title, String inputText) {
        super(parentFrame, title, true);

        setLayout(new BorderLayout());

        JLabel text = new JLabel(inputText);
        text.setBorder(new EmptyBorder(10,10,10,10));
        this.add(text, BorderLayout.NORTH);
        inputField = new JTextField();
        inputField.setActionCommand("enter");
        inputField.addActionListener(this);
        this.add(inputField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(cancelButton);

        JButton okButton = new JButton("Ok");
        okButton.setActionCommand("ok");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);

        this.add(bottomPanel, BorderLayout.SOUTH);

        this.pack();
        this.setSize(new Dimension(300,getHeight()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("ok".equals(e.getActionCommand()) || "enter".equals(e.getActionCommand())) {
            text = inputField.getText();
            if (text.isEmpty()) {
                inputField.setBorder(new LineBorder(Color.RED));
                return;
            }
        }
        if ("cancel".equals(e.getActionCommand())) {
            text = null;
        }
        setVisible(false);
        dispose();
    }

    public String getText() {
        return text;
    }
}
