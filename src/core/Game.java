package core;

public class Game implements Runnable {
    // the two agents playing this game
    public final Agent one;
    public final Agent two;

    // currently active agent & winner's token
    public Agent currAgent;
    public Token winner;

    // the game board
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

    // create a new game, botLevel == -1 means two human players
    public static Game newGame(int botLevel) {
        Board board = new Board();
        Agent one = new Player(Token.X);
        Agent two = (botLevel == -1) ?
            new Player(Token.O) : new Bot(Token.O, board, botLevel);

        return new Game(one, two, board);
    }

    // convenience method for play again
    public void reset() {
        currAgent = one;
        winner = null;
        board.reset();
    }

    public void playTurn() {
        // here lies the magic: all agents implement getMove, so
        // this Game object accepts all forms of games (PvP, PvC)
        board.makeMove(currAgent.token, currAgent.getMove());

        // logging: clear the screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // logging: display updated board
        board.printBoard();
        System.out.println();
    }

    @Override
    public void run() {
        // logging: display initial empty board
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
