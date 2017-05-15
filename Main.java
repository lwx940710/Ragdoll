import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("DOODLE");
        Model model = new Model(frame);


        // Set up the window.
        frame.setTitle("Paper Doll");
        frame.setSize(new Dimension(800, 750));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MenuBarView menuView = new MenuBarView(model);
        CenterView centerView = new CenterView(model);
        BotView botview = new BotView(model);
        model.notifyObservers();

        frame.setJMenuBar(menuView.getResult());
        frame.add(centerView, BorderLayout.CENTER);
        frame.add(botview, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
