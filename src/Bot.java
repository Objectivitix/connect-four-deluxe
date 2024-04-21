import java.util.ArrayList;
import java.util.List;

public class Bot {
    private final Game game;

    public Bot(Game game) {
        this.game = game;
    }

    public int getRandomMove() {
        List<Integer> possibleMoves = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (game.isValidMove(i)) {
                possibleMoves.add(i);
            }
        }

        return possibleMoves.get((int) (Math.random() * possibleMoves.size()));
    }
}
