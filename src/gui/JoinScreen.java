package gui;

import core.*;
import net.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinScreen extends Screen implements ActionListener {
    JTextField input;

    public JoinScreen() {
        super();
        setLayout(new GridLayout());

        input = new JTextField();
        input.setFont(new Font("", Font.PLAIN, 96));
        input.addActionListener(this);

        add(input);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != input) {
            return;
        }

        String address = input.getText();

        if (address.isBlank()) {
            address = "localhost";
        }

        Game game = Game.newGame();

        Client client = new Client(address, game);
        int result = client.connect();

        if (result == 0) {
            replaceWith(new WaitingScreen(game, client));
        } else if (result == 1) {
            replaceWith(new GameScreen(game, client, false));
        }

        (new Thread(client)).start();
    }
}
