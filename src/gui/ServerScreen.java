package gui;

import net.Server;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;

public class ServerScreen extends Screen {
    public ServerScreen() {
        super();
        setLayout(new GridLayout(3, 1, 0, -50));

        JLabel intro = new JLabel("Your server’s IP address is");
        intro.setFont(new Font("Rasa", Font.PLAIN, 36));
        intro.setHorizontalAlignment(SwingConstants.CENTER);
        intro.setVerticalAlignment(SwingConstants.BOTTOM);
        add(intro);

        JLabel address;

        try {
            address = new JLabel(Server.getHostIP());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        address.setFont(new Font("Rasa", Font.BOLD, 120));
        address.setHorizontalAlignment(SwingConstants.CENTER);
        add(address);

        JLabel outro = new JLabel(Utils.center("Closing this window kills the server.\n" +
            "Run another instance of this program to join."));
        outro.setFont(new Font("Rasa", Font.PLAIN, 30));
        outro.setHorizontalAlignment(SwingConstants.CENTER);
        outro.setVerticalAlignment(SwingConstants.TOP);
        add(outro);

        new Thread(new Server()).start();
    }
}
