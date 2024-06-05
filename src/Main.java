import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        // initialize game and bot
        Board board = new Board();
        Agent one = new Player(Token.X, board, console);
        Agent two = new Bot(Token.O, board, 7);
        Game game = new Game(one, two, board);

        JFrame frame = new JFrame("Connect 4");
        ImageIcon icon = new ImageIcon("connect4.jpg");

        // resize the ImageIcon
        Image image = icon.getImage().getScaledInstance(400, 400,  Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);

        // add icon, resize frame, make visible
        frame.add(new JLabel(icon));
        frame.pack();
        frame.setVisible(true);

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
