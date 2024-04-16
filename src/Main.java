import java.util.Scanner;

public class Main {
    public static int getMove(Scanner console, Game game, Player player) {
        int move;
        System.out.print(player + ", make your move (enter 1-7): ");

        while (true) {
            if (!console.hasNextInt() || (move = console.nextInt()) < 1 || move > 7) {
                console.nextLine();
                System.out.print("That's not a valid move. Enter an integer from 1 to 7: ");
                continue;
            }

            if (!game.isValidMove(move - 1)) {
                console.nextLine();
                System.out.printf("Column %d is full. Enter another (1-7): ", move);
                continue;
            }

            break;
        }

        return move - 1;
    }

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        Game game = new Game();

        System.out.println("Welcome to Connect 4!");
        System.out.println("X goes first.");
        System.out.println();

        game.printBoard();
        System.out.println();

        Player currPlayer = Player.X;
        Player winner;

        while ((winner = game.checkWin()) == null && !game.isTie()) {
            game.makeMove(currPlayer, getMove(console, game, currPlayer));

            System.out.print("\033[H\033[2J");
            System.out.flush();
            game.printBoard();
            System.out.println();

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
