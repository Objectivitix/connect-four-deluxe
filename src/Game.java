import java.util.ArrayList;
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
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                for (Player[] four : getPossibleConnectFours(i, j)) {
                    Player winner = checkConnectFour(four);

                    if (winner != null) {
                        return winner;
                    }
                }
            }
        }

        return null;
    }

    public void printBoard() {
        for (int i = 1; i < LENGTH; i++) {
            System.out.print(i + "  ");
        }

        System.out.println(LENGTH);

        for (int j = WIDTH - 1; j >= 0; j--) {
            for (Player[] column : board) {
                if (column[j] == null) System.out.print("_  ");
                else System.out.print(column[j] + "  ");
            }

            System.out.println();
        }
    }

    private List<Player[]> getPossibleConnectFours(int i, int j) {
        List<Player[]> fours = new ArrayList<>();

        // direction 1: right
        if (j + 3 < WIDTH) {
            fours.add(new Player[]{
                board[i][j], board[i][j + 1], board[i][j + 2], board[i][j + 3]
            });
        }

        // direction 2: down-right
        if (i + 3 < LENGTH && j + 3 < WIDTH) {
            fours.add(new Player[]{
                board[i][j], board[i + 1][j + 1], board[i + 2][j + 2], board[i + 3][j + 3]
            });
        }

        // direction 3: down
        if (i + 3 < LENGTH) {
            fours.add(new Player[]{
                board[i][j], board[i + 1][j], board[i + 2][j], board[i + 3][j]
            });
        }

        // direction 4: down-left
        if (i + 3 < LENGTH && j - 3 >= 0) {
            fours.add(new Player[]{
                board[i][j], board[i + 1][j - 1], board[i + 2][j - 2], board[i + 3][j - 3]
            });
        }

        return fours;
    }

    private Player checkConnectFour(Player[] four) {
        boolean allXs = true;
        boolean allOs = true;

        for (Player piece : four) {
            if (piece != Player.X) allXs = false;
            if (piece != Player.O) allOs = false;
        }

        if (allXs) return Player.X;
        else if (allOs) return Player.O;
        else return null;
    }
}
