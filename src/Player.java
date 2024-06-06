public class Player extends Agent {
    // use Scanner to get a move from human player
    Board board;
    int move;

    public Player(Token token, Board board) {
        super(token);
        this.board = board;
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
