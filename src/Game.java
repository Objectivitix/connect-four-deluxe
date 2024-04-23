import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Game {
    private static final int LENGTH = 7;
    private static final int WIDTH = 6;

    private final Player[][] board;
    private final int[] firstEmptyIndices;

    public Game() {
        board = new Player[LENGTH][WIDTH];
        firstEmptyIndices = new int[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            firstEmptyIndices[i] = 0;
        }
    }

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
        return firstEmptyIndices[i] < WIDTH;
    }

    public void makeMove(Player player, int i) {
        board[i][firstEmptyIndices[i]] = player;
        firstEmptyIndices[i]++;
    }

    public boolean isTie() {
        for (int k : firstEmptyIndices) {
            if (k < WIDTH) {
                return false;
            }
        }

        return true;
    }

    public Player checkWin() {
        for (String line : getLines()) {
            if (line.contains("XXXX")) return Player.X;
            if (line.contains("OOOO")) return Player.O;
        }

        return null;
    }

    public void printBoard() {
        for (int i = 1; i < LENGTH; i++) {
            System.out.print(i + "  ");
        }

        System.out.println(LENGTH);

        for (Player[] row : getRotatedBoard()) {
            for (Player piece : row) {
                if (piece == null) System.out.print("_  ");
                else System.out.print(piece + "  ");
            }

            System.out.println();
        }
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

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
        List<Player[]> lines = new ArrayList<>();

        // 1. rows
        Collections.addAll(lines, board);

        // 2. columns
        Collections.addAll(lines, getRotatedBoard());

        // 3. / diagonals
        for (int j = 3; j < WIDTH; j++)
            lines.add(getDiagonal(0, j, false));
        for (int i = 1; i < 4; i++)
            lines.add(getDiagonal(i, WIDTH - 1, false));

        // 4. \ diagonals
        for (int j = 0; j < 3; j++)
            lines.add(getDiagonal(0, j, true));
        for (int i = 1; i < 4; i++)
            lines.add(getDiagonal(i, 0, true));

        return lines;
    }

    private Player[][] getRotatedBoard() {
        Player[][] rotatedBoard = new Player[WIDTH][LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                rotatedBoard[j][i] = board[i][WIDTH - j - 1];
            }
        }

        return rotatedBoard;
    }

    private Player[] getDiagonal(int iStart, int jStart, boolean otherWay) {
        List<Player> diag = new ArrayList<>();

        int i = iStart;
        int j = jStart;

        while (i < LENGTH && j >= 0 && j < WIDTH) {
            diag.add(board[i][j]);

            i++;
            if (otherWay) j++;
            else j--;
        }

        return diag.toArray(new Player[0]);
    }
}
