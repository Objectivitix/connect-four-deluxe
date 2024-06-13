package gui;

import core.*;
import net.Client;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinScreen extends Screen implements ActionListener {
    JTextField input;
    JPanel center;
    JLabel status;

    public JoinScreen() {
        super();
        setLayout(new BorderLayout());

        // take up space on all sides to constrain center
        add(Utils.spacer(250), BorderLayout.NORTH);
        add(Utils.spacer(250), BorderLayout.SOUTH);
        add(Utils.spacer(150), BorderLayout.WEST);
        add(Utils.spacer(150), BorderLayout.EAST);

        center = new JPanel(new GridLayout(3, 1, 0, -20));

        JLabel prompt = new JLabel("Enter the server's IP address:");
        prompt.setFont(new Font("Rasa", Font.BOLD, 36));
        prompt.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(prompt);

        input = new JTextField();
        input.setFont(new Font("", Font.PLAIN, 96));
        input.addActionListener(this);
        center.add(input);

        status = new JLabel("");
        status.setFont(new Font("", Font.PLAIN, 24));
        status.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(status);

        add(center);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != input) {
            return;
        }

        status.setText("Connecting . . .");
        status.setForeground(null);
        paint(getGraphics());

        String address = input.getText();

        if (address.isBlank()) {
            address = "localhost";
        }

        Game game = Game.newGame();

        Client client = new Client(address, game);
        int result = client.connect();

        if (result == 0) {
            replaceWith(new WaitingScreen(game, client));
            (new Thread(client)).start();
        } else if (result == 1) {
            replaceWith(new GameScreen(game, client, false));
            (new Thread(client)).start();
        } else {
            status.setText("Could not connect to this address");
            status.setForeground(Color.RED);
        }
    }
}
