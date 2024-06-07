import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen extends Screen implements ActionListener {
    Game game;
    JLabel status;
    JButton playAgain, mainMenu;

    public GameScreen(Game game) {
        this(game, null);
    }

    public GameScreen(Game game, Client client) {
        super();
        setLayout(null);

        this.game = game;

        (new Thread(game)).start();
        (new Thread(client)).start();

        BoardPane boardPane = new BoardPane(game, client);
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
        boolean terminal = false;

        if (game.winner == Token.X) {
            status.setText("Red wins!");
            terminal = true;
        } else if (game.winner == Token.O) {
            status.setText("Yellow wins!");
            terminal = true;
        } else if (game.board.isTie()) {
            status.setText("Wow, it's a tie!");
            terminal = true;
        } else if (game.currAgent.token == Token.X) {
            status.setText("Red, your turn");
        } else {
            status.setText("Yellow, your turn");
        }

        if (terminal) {
            playAgain = new JButton("Play Again");
            playAgain.setBounds(850, 300, 250, 100);
            playAgain.addActionListener(evt -> {
                game.reset();
                replaceWith(new GameScreen(game));
            });
            add(playAgain);

            mainMenu = new JButton("Main Menu");
            mainMenu.setBounds(850, 420, 250, 100);
            mainMenu.addActionListener(evt -> replaceWith(new MenuScreen()));
            add(mainMenu);
        }

        repaint();
    }
}
