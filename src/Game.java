import java.util.*;

public class Game {
    // board size for Connect 4
    private static final int LENGTH = 7;
    private static final int WIDTH = 6;

    // for easy storage & move-making, `board` is actually
    // the true game board turned 90 degrees clockwise
    private final Player[][] board;

    // stores "where we're at" for each column
    private final int[] firstEmptyIndices;

    public Game() {
        board = new Player[LENGTH][WIDTH];
        firstEmptyIndices = new int[LENGTH];

        // initialize to 0 because no pieces yet
        for (int i = 0; i < LENGTH; i++) {
            firstEmptyIndices[i] = 0;
        }
    }

    // private parameterized constructor used for deep copy
    private Game(Player[][] board, int[] firstEmptyIndices) {
        this.board = new Player[LENGTH][WIDTH];

        for (int i = 0; i < LENGTH; i++) {
            this.board[i] = Arrays.copyOf(board[i], WIDTH);
        }

        this.firstEmptyIndices = Arrays.copyOf(firstEmptyIndices, LENGTH);
    }

    public Game deepCopy() {
        return new Game(board, firstEmptyIndices);
    }

    public boolean isValidMove(int i) {
        // the column is full when this index > WIDTH
        return firstEmptyIndices[i] < WIDTH;
    }

    public void makeMove(Player player, int i) {
        // use the first empty index to "drop" a new piece
        board[i][firstEmptyIndices[i]] = player;
        firstEmptyIndices[i]++;
    }

    // used by bot minimax to generate new successor state
    public void unmakeMove(int i) {
        firstEmptyIndices[i]--;
        board[i][firstEmptyIndices[i]] = null;
    }

    public boolean isTie() {
        for (int k : firstEmptyIndices) {
            if (k < WIDTH) {
                return false;
            }
        }

        // if we make it here, all columns must be full
        return true;
    }

    public Player checkWin() {
        // game line with four-in-a-row means there's a win
        for (String line : getLines()) {
            if (line.contains("XXXX")) return Player.X;
            if (line.contains("OOOO")) return Player.O;
        }

        // making it here means no wins so far (or tied)
        return null;
    }

    public void printBoard() {
        // print out column numbers for UX
        for (int i = 1; i < LENGTH; i++) {
            System.out.print(i + "  ");
        }

        System.out.println(LENGTH);

        // get the actual board and print row by row
        for (Player[] row : getRotatedBoard()) {
            for (Player piece : row) {
                if (piece == null) System.out.print("_  ");
                else System.out.print(piece + "  ");
            }

            System.out.println();
        }
    }

    // gets "game lines" of the board as strings
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

        // turn each game line array into a string
        for (Player[] lineArr : _getLines()) {
            StringBuilder lineStr = new StringBuilder();

            for (Player piece : lineArr) {
                if (piece == null) lineStr.append("_");
                else lineStr.append(piece);
            }

            lines.add(lineStr.toString());
        }

        return lines;
    }

    private List<Player[]> _getLines() {
        // Consider the game board as rows, columns, and diagonals. These
        // are lines on which war is waged. Designing the program this way
        // facilitates win and AI logic because we can leverage pre-built
        // string manipulation methods.

        List<Player[]> lines = new ArrayList<>();

        // 1. rows
        Collections.addAll(lines, board);

        // 2. columns
        Collections.addAll(lines, getRotatedBoard());

        // 3. diagonals like /
        for (int j = 3; j < WIDTH; j++)
            lines.add(getDiagonal(0, j, false));
        for (int i = 1; i < 4; i++)
            lines.add(getDiagonal(i, WIDTH - 1, false));

        // 4. diagonals like \
        for (int j = 0; j < 3; j++)
            lines.add(getDiagonal(0, j, true));
        for (int i = 1; i < 4; i++)
            lines.add(getDiagonal(i, 0, true));

        return lines;
    }

    // rotate the board 90 degrees cc to get true game board
    private Player[][] getRotatedBoard() {
        Player[][] rotatedBoard = new Player[WIDTH][LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                rotatedBoard[j][i] = board[i][WIDTH - j - 1];
            }
        }

        return rotatedBoard;
    }

    // get a diagonal line of the board from starting coords
    private Player[] getDiagonal(int iStart, int jStart, boolean otherWay) {
        List<Player> diag = new ArrayList<>();

        int i = iStart;
        int j = jStart;

        while (i < LENGTH && j >= 0 && j < WIDTH) {
            diag.add(board[i][j]);

            i++;

            // so we have diagonals shaped both like / and \
            if (otherWay) j++;
            else j--;
        }

        return diag.toArray(new Player[0]);
    }
}
