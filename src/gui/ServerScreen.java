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

        // add intro image
        JLabel intro = new JLabel(Utils.icon("now-a-server.png", 900, 375));
        intro.setBounds(150, 50, 900, 375);
        add(intro);

        // get this device's IP, on which a ServerSocket listens quietly
        String ip;
        try {
            ip = Server.getHostIP();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // display this IP in big bold numerals
        JLabel address = new JLabel(ip);
        address.setFont(new Font("Rasa", Font.BOLD, 144));
        address.setHorizontalAlignment(SwingConstants.CENTER);
        address.setBounds(0, 415, 1200, 120);
        add(address);

        // add some concise instructions
        JLabel outro = new JLabel(Utils.center("Closing this window kills the server.\n" +
            "Run another instance of this program to join."));
        outro.setBounds(0, 650, 1200, 100);
        outro.setFont(new Font("Rasa", Font.PLAIN, 30));
        outro.setHorizontalAlignment(SwingConstants.CENTER);
        add(outro, BorderLayout.SOUTH);

        // make server listen in a separate thread
        new Thread(new Server()).start();
    }
}
