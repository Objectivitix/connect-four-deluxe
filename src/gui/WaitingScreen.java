package gui;

import core.Game;
import net.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitingScreen extends Screen implements ActionListener {
    Game game;
    Client client;

    public WaitingScreen(Game game, Client client) {
        this.game = game;
        this.client = client;

        timer = new Timer(UPDATE_PERIOD, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client.status == Client.SERVER_DISCONNECTED) {
            replaceWith(new MenuScreen());
            return;
        }

        if (client.status == Client.GOOD_TO_PLAY) {
            replaceWith(new GameScreen(game, client, true));
        }
    }
}
