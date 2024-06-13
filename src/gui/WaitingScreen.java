package gui;

import core.Game;
import net.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitingScreen extends Screen implements ActionListener {
    Game game;
    Client client;

    public WaitingScreen(Game game, Client client) {
        super();
        setLayout(new GridLayout());

        this.game = game;
        this.client = client;

        JLabel waiting = new JLabel("Waiting for second player . . .");
        waiting.setFont(new Font("Rasa", Font.BOLD, 36));
        waiting.setHorizontalAlignment(SwingConstants.CENTER);
        add(waiting);

        timer = new Timer(UPDATE_PERIOD, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client.status == Client.SERVER_DISCONNECTED) {
            replaceWith(new DisconnectedScreen(true));
            return;
        }

        if (client.status == Client.GOOD_TO_PLAY) {
            replaceWith(new GameScreen(game, client, true));
        }
    }
}
