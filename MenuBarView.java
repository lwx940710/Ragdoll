
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Object;
import java.util.*;
import javax.swing.event.*;

public class MenuBarView extends JFrame implements Observer {

    private Model model;
    private JMenuBar menubar;
    private JMenuItem save;
    /**
     * Create a new View.
     */
    public MenuBarView(Model model) {
        this.model = model;

        menubar = new JMenuBar();
        JMenu file = new JMenu("File");

        // Reset
        JMenuItem reset = new JMenuItem("Reset (Ctrl-R)");
        reset.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.clearAll();
                model.update();
            }
        });

        // Quit
        JMenuItem quit = new JMenuItem("Quit (Ctrl-Q)");
        quit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        file.add(reset);
        file.addSeparator();
        file.add(quit);

        menubar.add(file);
        model.addObserver(this);
    }

    public JMenuBar getResult() {
        return menubar;
    }

    /**
     * Update with data from the model.
     */
    public void update(Observable o, Object obj) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.

        System.out.println("Model changed!");
    }
}
