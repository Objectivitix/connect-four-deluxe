package core;

public class Player extends Agent {
    // most recent move, used for inter-thread manipulation so
    // GUI can signal Player obj to make a move for Game obj
    private int move;

    public Player(Token token) {
        super(token);
    }

    @Override
    public int getMove() {
        // simply wait for a ping that updates move, before
        // returning it as it must be the most recent
        try {
            // synchronize thread with object itself as lock
            synchronized (this) {
                wait();
                return move;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveMove(int move) {
        // wake up thread with newest move
        synchronized (this) {
            this.move = move;
            notify();
        }
    }
}
