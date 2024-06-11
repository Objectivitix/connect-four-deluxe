import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitingScreen extends Screen implements ActionListener {
    Game game;
    Client client;

    public WaitingScreen(Game game, Client client) {
        this.game = game;
        this.client = client;

        timer = new Timer(50, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client.status == Client.GOOD_TO_PLAY) {
            replaceWith(new GameScreen(game, client, true));
        }
    }
}
