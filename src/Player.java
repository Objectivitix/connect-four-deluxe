import java.util.Scanner;

public class Player extends Agent {
    // use Scanner to get a move from human player
    Board board;
    Scanner console;
    int move;

    public Player(Token token, Board board, Scanner s) {
        super(token);
        this.board = board;

        console = s;
    }

    public void holdOn(int move) {
        synchronized (this) {
            this.move = move;
            notify();
        }
    }

    @Override
    public int getMove() {
        try {
            synchronized (this) {
                wait();
                return move;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
