import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main extends JFrame {
    private Main(Game game) {
        // set JFrame's title
        super("Connect 4 Deluxe");

        // prevent resizing of window
        setResizable(false);

        // center top-level JPanels (*Screen.java)
        setLayout(new GridLayout());

        // close program if user closes window
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new GameScreen(game));

        pack();

        // center window with respect to viewport
        setLocationRelativeTo(null);

        // make everything visible
        setVisible(true);
    }

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        // initialize game and bot
        Board board = new Board();
        Agent one = new Player(Token.X, board, console);
        Agent two = new Player(Token.O, board, console);
//        Agent two = new Bot(Token.O, board, 7);
        Game game = new Game(one, two, board);

        new Main(game);

        System.out.println("Welcome to Connect 4!");
        System.out.println("X goes first.");
        System.out.println();

        Token winner = game.play();

        if (winner == null) {
            System.out.println("Wow, it's a tie!");
        } else {
            System.out.println(winner + " wins!");
        }

        console.close();
    }
}
