package core;

public class Game implements Runnable {
    public final Agent one;
    public final Agent two;

    public Agent currAgent;
    public Token winner;

    public final Board board;

    private Game(Agent one, Agent two, Board board) {
        this.one = one;
        this.two = two;
        currAgent = one;

        this.board = board;
    }

    public static Game newGame() {
        return newGame(-1);
    }

    public static Game newGame(int botLevel) {
        Board board = new Board();
        Agent one = new Player(Token.X, board);
        Agent two = (botLevel == -1) ?
            new Player(Token.O, board) : new Bot(Token.O, board, botLevel);

        return new Game(one, two, board);
    }

    public void reset() {
        currAgent = one;
        winner = null;
        board.reset();
    }

    public void playTurn() {
        board.makeMove(currAgent.token, currAgent.getMove());

        // clear the screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // display updated board
        board.printBoard();
        System.out.println();
    }

    @Override
    public void run() {
        // display initial empty board
        board.printBoard();
        System.out.println();

        Token winner;

        // if loop exits due to tie, winner will still be null
        while ((winner = board.checkWin()) == null && !board.isTie()) {
            playTurn();

            // alternate turns
            if (currAgent == one) currAgent = two;
            else currAgent = one;
        }

        this.winner = winner;
    }
}
