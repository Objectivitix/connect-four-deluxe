package gui;

import utils.Utils;

import javax.swing.*;
import java.awt.*;

public class DisconnectedScreen extends Screen {
    public DisconnectedScreen(boolean serverDisconnected) {
        super();
        setLayout(new BorderLayout());

        // take up space on all sides to constrain center
        add(Utils.spacer(250), BorderLayout.NORTH);
        add(Utils.spacer(250), BorderLayout.SOUTH);
        add(Utils.spacer(250), BorderLayout.WEST);
        add(Utils.spacer(250), BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, -20));

        JLabel title = new JLabel(serverDisconnected ? "Uh oh—the server disconnected!" : "Uh oh—your opponent disconnected!");
        title.setFont(new Font("Rasa", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(title);

        JButton mainMenu = new JButton("Go back to Main Menu");
        mainMenu.setFont(new Font("", Font.PLAIN, 24));
        mainMenu.addActionListener(evt -> replaceWith(new MenuScreen()));
        center.add(mainMenu);

        add(center);
    }
}
