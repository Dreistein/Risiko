import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jakob on 11.01.2015.
 */
public class GameMenu extends javax.swing.JFrame {

    public Toolkit t;
    public int x =0, y =0, width =800, height =600;
    public JButton newGame;
    public JButton loadGame;
    public JButton options;
    public JButton exit;
    public JPanel listPane;

    public GameMenu(){

        //Set Layout Manager for GameMenu window
        listPane = new JPanel();
        newGame = new JButton("Neues Spiel");
        loadGame = new JButton("Spiel suchen");
        options = new JButton("Optionen");
        exit = new JButton("Beenden");
        GridLayout layout = new GridLayout(4,1);
        layout.setVgap(10);
        listPane.setBorder(new EmptyBorder(150, 200, 150, 200));
        listPane.setLayout(layout);
        this.listPane.add(newGame);
        this.listPane.add(loadGame);
        this.listPane.add(options);
        this.listPane.add(exit);
        this.add(listPane);



        //Set Size and Position of the window
        t = Toolkit.getDefaultToolkit();

        Dimension d = t.getScreenSize();
        x = (int) (d.getWidth() - width ) /2;
        y = (int) (d.getHeight() -height) /2;
        setBounds(x, y, width, height);
        setTitle("Risiko Multiplayer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //newGame.addActionListener((ActionListener) this);
        //loadGame.addActionListener((ActionListener) this);

        exit.addActionListener(e -> System.exit(0));


        newGame.setActionCommand("");
        loadGame.setActionCommand("");


        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        GameMenu menu = new GameMenu();
    }
}