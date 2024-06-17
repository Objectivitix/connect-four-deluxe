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
        super(true);
        setLayout(new GridLayout());

        this.game = game;
        this.client = client;

        // make sure client disconnects if user exits to main menu
        addBackToMenuListener(evt -> {
            replaceWith(App.menuScreen);
            client.disconnect();
        });

        // indicate to user that we wait for a second player to start game
        JLabel waiting = new JLabel("Waiting for second player . . .");
        waiting.setFont(new Font("Rasa", Font.BOLD, 36));
        waiting.setHorizontalAlignment(SwingConstants.CENTER);
        add(waiting);

        // check for updates at regular intervals
        timer = new Timer(UPDATE_PERIOD, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // if server disconnects while we wait, direct to a DisconnectedScreen
        if (client.status == Client.SERVER_DISCONNECTED) {
            replaceWith(new DisconnectedScreen("Uh oh—the server disconnected!"));
            return;
        }

        // once second player joins, it's game on with first-client control
        if (client.status == Client.GOOD_TO_PLAY) {
            replaceWith(new GameScreen(game, client, true));
        }
    }
}
