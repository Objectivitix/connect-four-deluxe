public class Game {
    private final Agent one;
    private final Agent two;
    public Agent currAgent;

    public final Board board;

    public Game(Agent one, Agent two, Board board) {
        this.one = one;
        this.two = two;
        currAgent = one;

        this.board = board;
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

    public Token play() {
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

        return winner;
    }
}
