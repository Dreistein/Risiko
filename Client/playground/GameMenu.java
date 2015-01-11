import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by Jakob on 11.01.2015.
 */
public class GameMenu extends javax.swing.JFrame {

    public Toolkit t;
    public int x =0, y =0, width =800, height =600;
    public JButton NewGame;
    public JButton LoadGame;
    public JPanel listPane;

    public GameMenu(){

        //Set Layout Manager for GameMenu window
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        this.listPane.add(NewGame);
        this.listPane.add(LoadGame);



        //Set Size and Position of the window
        t = Toolkit.getDefaultToolkit();

        Dimension d = t.getScreenSize();
        x = (int) (d.getWidth() - width ) /2;
        y = (int) (d.getHeight() -height) /2;
        setBounds(x,y,width,height);
        setTitle("Risiko Multiplayer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        NewGame.addActionListener((ActionListener) this);
        LoadGame.addActionListener((ActionListener) this);

        NewGame.setActionCommand();
        LoadGame.setActionCommand();


        setVisible(true);
    }

    public static void main(String[] args) {

    }
}
