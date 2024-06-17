package gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    public static JFrame frame;

    // store the one and only MenuScreen for efficiency
    public static MenuScreen menuScreen;

    private App() {
        // set JFrame's title
        super("Connect 4 Deluxe");

        // prevent resizing of window
        setResizable(false);

        // center top-level JPanels (*Screen.java)
        setLayout(new GridLayout());

        // close program if user closes window
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // start with a MenuScreen
        menuScreen = new MenuScreen();
        add(menuScreen);

        // automatically resize JFrame to contents
        pack();

        // center window with respect to viewport
        setLocationRelativeTo(null);

        // make everything visible
        setVisible(true);
    }

    public static void start() {
        frame = new App();
    }
}
