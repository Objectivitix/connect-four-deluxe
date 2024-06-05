import java.util.Scanner;

public class Player extends Agent {
    // use Scanner to get a move from human player
    Board board;
    Scanner console;

    public Player(Token token, Board board, Scanner s) {
        super(token);
        this.board = board;

        console = s;
    }

    @Override
    public int getMove() {
        int move;
        System.out.print(token + ", make your move (enter 1-7): ");

        // repeat as long as input is invalid
        while (true) {
            // not an integer, or integer is out of bounds
            if (!console.hasNextInt() || (move = console.nextInt()) < 1 || move > 7) {
                console.nextLine();
                System.out.print("That's not a valid move. Enter an integer from 1 to 7: ");
                continue;
            }

            // column is already full
            if (!board.isValidMove(move - 1)) {
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
}
