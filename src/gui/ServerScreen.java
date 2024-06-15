package gui;

import net.Server;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;

public class ServerScreen extends Screen {
    public ServerScreen() {
        super();
        setLayout(null);

        JLabel intro = new JLabel(Utils.icon("now-a-server.png", 900, 375));
        intro.setBounds(150, 50, 900, 375);
        add(intro);

        JLabel address;

        try {
            address = new JLabel(Server.getHostIP());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        address.setFont(new Font("Rasa", Font.BOLD, 144));
        address.setHorizontalAlignment(SwingConstants.CENTER);
        address.setBounds(0, 415, 1200, 120);
        add(address);

        JLabel outro = new JLabel(Utils.center("Closing this window kills the server.\n" +
            "Run another instance of this program to join."));
        outro.setBounds(0, 650, 1200, 100);
        outro.setFont(new Font("Rasa", Font.PLAIN, 30));
        outro.setHorizontalAlignment(SwingConstants.CENTER);
        add(outro, BorderLayout.SOUTH);

        new Thread(new Server()).start();
    }
}
