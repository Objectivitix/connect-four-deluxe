import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main {
    public static final Scanner console = new Scanner(System.in);

    public static Game game;
    public static Bot bot;

    // use Scanner to get a move from human player
    public static int getMove(Player player) {
        int move;
        System.out.print(player + ", make your move (enter 1-7): ");

        // repeat as long as input is invalid
        while (true) {
            // not an integer, or integer is out of bounds
            if (!console.hasNextInt() || (move = console.nextInt()) < 1 || move > 7) {
                console.nextLine();
                System.out.print("That's not a valid move. Enter an integer from 1 to 7: ");
                continue;
            }

            // column is already full
            if (!game.isValidMove(move - 1)) {
                console.nextLine();
                System.out.printf("Column %d is full. Enter another (1-7): ", move);
                continue;
            }

            // if we make it here, input is valid :)
            break;
        }

        // minus 1 because indices start at 0
        return move - 1;
    }

    public static void playTurn(Player player) {
        // if there's a bot, and it's its turn, let it play
        int move = (bot != null && player == bot.player) ?
            bot.getOptimalMove() : getMove(player);

        game.makeMove(player, move);

        // clear the screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // display updated board
        game.printBoard();
        System.out.println();
    }

    public static void main(String[] args) {
        // initialize game and bot
        game = new Game();
        bot = new Bot(game, Player.O, 6);

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

        // display initial empty board
        game.printBoard();
        System.out.println();

        Player currPlayer = Player.X;
        Player winner;

        // if loop exits due to tie, winner will still be null
        while ((winner = game.checkWin()) == null && !game.isTie()) {
            playTurn(currPlayer);

            // alternate turns
            if (currPlayer == Player.X) currPlayer = Player.O;
            else currPlayer = Player.X;
        }

        if (winner == null) {
            System.out.println("Wow, it's a tie!");
        } else {
            System.out.println(winner + " wins!");
        }

        console.close();
    }
}
