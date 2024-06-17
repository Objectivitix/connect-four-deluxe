package gui;

import core.*;
import net.Client;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JoinScreen extends Screen implements ActionListener {
    private final JTextField input;
    private final JLabel status;

    public JoinScreen() {
        super(true);
        setLayout(new BorderLayout());

        addBackToMenuListener(evt -> replaceWith(App.menuScreen));

        // take up space on all sides to constrain center
        add(Utils.spacer(250), BorderLayout.NORTH);
        add(Utils.spacer(250), BorderLayout.SOUTH);
        add(Utils.spacer(150), BorderLayout.WEST);
        add(Utils.spacer(150), BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, -20));

        // create big bold prompt for user input
        JLabel prompt = new JLabel("Enter the server's IP address:");
        prompt.setFont(new Font("Rasa", Font.BOLD, 36));
        prompt.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(prompt);

        // create input field for IP address
        input = new JTextField();
        input.setFont(new Font("", Font.PLAIN, 96));
        input.addActionListener(this);
        center.add(input);

        // create connection status indicator
        status = new JLabel("");
        status.setFont(new Font("", Font.PLAIN, 24));
        status.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(status);

        add(center);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // only execute what comes next if action comes from input enter
        if (e.getSource() != input) {
            return;
        }

        // reset status indicator's font color
        status.setForeground(null);

        // get this screen's graphics object
        Graphics g = getGraphics();

        // this thread takes care of updating the connection status
        // indicator while client socket attempts to connect to server,
        // which blocks the entire AWT thread this component runs on
        Thread connecting = new Thread(() -> {
            int dots = 0;

            while (true) {
                // update loading status before painting immediately
                status.setText("Connecting" + " .".repeat(dots));
                paint(g);

                // cycle number of dots: 0,1,2,3,...
                dots = (dots + 1) % 4;

                // sleep for 700 milliseconds
                try {
                    Thread.sleep(700);
                } catch (InterruptedException ex) {
                    // once thread interrupted by resolved connection
                    // attempt (either good or bad), terminate thread
                    return;
                }
            }
        });

        connecting.start();

        String address = input.getText();

        // if user entered blank, default to localhost
        // (useful when server runs on same machine)
        if (address.isBlank()) {
            address = "localhost";
        }

        Game game = Game.newGame();

        // connect to the server, interrupt loading thread when done
        Client client = new Client(address, game);
        int id = client.connect();
        connecting.interrupt();

        if (id == 0) {
            // first client: direct to waiting screen
            replaceWith(new WaitingScreen(game, client));
            (new Thread(client)).start();
        } else if (id > 0) {
            // second client or onwards: direct to game screen right away
            replaceWith(new GameScreen(game, client, false));
            (new Thread(client)).start();
        } else {
            // if timed out, show failed status with red font
            status.setText("Could not connect to this address");
            status.setForeground(Color.RED);
        }
    }
}
