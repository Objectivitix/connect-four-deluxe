import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Bot {
    private final int MY_INF = 999_999_999;

    private final Map<Pattern, Integer> FEATURE_WEIGHTS = Map.of(
        Pattern.compile("_AAA_"), 3_000_000,
        Pattern.compile("_AAA(B|$)|(B|^)AAA_"), 900_000,
        Pattern.compile("A_AA|AA_A"), 900_000,
        Pattern.compile("_AA_"), 50_000,
        Pattern.compile("___AA(B|$)|(B|^)AA___"), 20_000,
        Pattern.compile("(B|^)__AA(B|$)|(B|^)AA__(B|$)"), 10_000
    );

    private final Game realGame;

    public final Player player;
    private final Player opp;

    private final int maxDepth;

    public Bot(Game game, Player player, int lookaheadDepth) {
        realGame = game;
        this.player = player;

        if (player == Player.X) opp = Player.O;
        else opp = Player.X;

        maxDepth = lookaheadDepth;
    }

    public int getRandomMove() {
        List<Integer> possibleMoves = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (realGame.isValidMove(i)) {
                possibleMoves.add(i);
            }
        }

        return possibleMoves.get((int) (Math.random() * possibleMoves.size()));
    }

    public int getOptimalMove() {
        long max = -MY_INF;
        int move = -1;

        for (int i = 0; i < 7; i++) {
            if (realGame.isValidMove(i)) {
                Game succ = realGame.deepCopy();
                succ.makeMove(player, i);
                long result = minimax(succ, opp, 1);
                if (result >= max) {
                    max = result;
                    move = i;
                }
            }
        }

        return move;
    }

    public long minimax(Game state, Player currPlayer, int depth) {
        Player winner = state.checkWin();
        if (winner == player) return MY_INF;
        if (winner == opp) return -MY_INF;
        if (state.isTie()) return 0;

        if (depth > maxDepth) {
            return calculateHeuristic(state);
        }

        if (currPlayer == player) {
            long max = -MY_INF;

            for (Game succ : getSuccessors(state, player)) {
                max = Math.max(max, minimax(succ, opp, depth + 1));
            }

            return max;
        }

        long min = MY_INF;

        for (Game succ : getSuccessors(state, opp)) {
            min = Math.min(min, minimax(succ, player, depth + 1));
        }

        return min;
    }

    private List<Game> getSuccessors(Game state, Player currPlayer) {
        List<Game> succs = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (state.isValidMove(i)) {
                Game succ = state.deepCopy();
                succ.makeMove(currPlayer, i);
                succs.add(succ);
            }
        }

        return succs;
    }

    public long calculateHeuristic(Game state) {
        long score = 0;

        for (String line : state.getLines()) {
            String myLine = line
                .replaceAll(player.name(), "A")
                .replaceAll(opp.name(), "B");

            String oppLine = line
                .replaceAll(opp.name(), "A")
                .replaceAll(player.name(), "B");

            for (Pattern feature : FEATURE_WEIGHTS.keySet()) {
                int weight = FEATURE_WEIGHTS.get(feature);

                score += weight * feature.matcher(myLine).results().count();
                score -= weight * feature.matcher(oppLine).results().count();
            }
        }

        return score;
    }
}
