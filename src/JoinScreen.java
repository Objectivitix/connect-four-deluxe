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

        Board board = new Board();
        Agent one = new Player(Token.X, board);
        Agent two = new Player(Token.O, board);
        Game game = new Game(one, two, board);

        replaceWith(new GameScreen(game, new Client(address, game)));
    }
}
