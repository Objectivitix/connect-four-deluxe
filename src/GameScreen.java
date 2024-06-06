import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen extends JPanel implements ActionListener {
    Game game;
    JLabel status;

    public GameScreen(Game game) {
        super();
        setPreferredSize(new Dimension(1200, 800));
        setLayout(null);

        this.game = game;

        BoardPane boardPane = new BoardPane(game);
        boardPane.setLocation(100, 20);
        add(boardPane);

        status = new JLabel("Red, your turn");
        status.setFont(new Font("Rasa", Font.BOLD, 30));
        status.setBounds(850, 50, 300, 200);
        add(status);

        new Timer(100, this).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (game.winner == Token.X) {
            status.setText("Red wins!");
        } else if (game.winner == Token.O) {
            status.setText("Yellow wins!");
        } else if (game.board.isTie()) {
            status.setText("Wow, it's a tie!");
        } else if (game.currAgent.token == Token.X) {
            status.setText("Red, your turn");
        } else {
            status.setText("Yellow, your turn");
        }

        repaint();
    }
}
