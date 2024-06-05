import java.util.*;
import java.util.regex.Pattern;

// Minimax powers this Connect 4 bot. The algorithm consists of:
//    1) a recursive game tree searcher that maximizes "gains"
//       for bot, assuming opponent plays perfectly (constantly
//       minimizing bot's gains)
//    2) a heuristic to decide how "good" a non-terminal game
//       board is - it's a weighted sum of board "features"

public class Bot extends Agent {
    // an int big enough to mean infinity, small
    // enough to be free of overflow concerns
    private static final int MY_INF = 999_999_999;

    private static final int[] PSEUDOMOVES = {3, 2, 4, 1, 5, 0, 6};

    // map of board feature regexes to their weights
    // (ex. 3-in-a-row w/ adjacent spaces has weight 1_000_000)
    private static final Map<Pattern, Integer> FEATURE_WEIGHTS = Map.of(
        Pattern.compile("_AAA_"), 1_000_000,
        Pattern.compile("_AAA(B|$)|(B|^)AAA_"), 300_000,
        Pattern.compile("A_AA|AA_A"), 300_000,
        Pattern.compile("_AA_"), 50_000,
        Pattern.compile("___AA(B|$)|(B|^)AA___"), 20_000,
        Pattern.compile("(B|^)__AA(B|$)|(B|^)AA__(B|$)"), 10_000
    );

    // the actual game, contrasting with simulations the bot runs
    private final Board realBoard;
    private Board simBoard;

    private final Token opp;

    // depth at which we cut off game tree search
    private final int maxDepth;

    public Bot(Token token, Board board, int lookaheadDepth) {
        super(token);
        realBoard = board;

        if (token == Token.X) opp = Token.O;
        else opp = Token.X;

        maxDepth = lookaheadDepth;
    }

    // driver method that invokes minimax
    @Override
    public int getMove() {
        long max = -MY_INF;
        int move = -1;

        // reset simBoard with a copy of actual current board
        simBoard = realBoard.deepCopy();

        for (int i : PSEUDOMOVES) {
            if (simBoard.isValidMove(i)) {
                simBoard.makeMove(token, i);

                // search this successor state recursively
                long result = minimax(opp, 1, -MY_INF, MY_INF);

                simBoard.unmakeMove(i);

                // record move associated with maximum gains
                if (result >= max) {
                    max = result;
                    move = i;
                }
            }
        }

        return move;
    }

    private long minimax(Token currPlayer, int depth, long alpha, long beta) {
        Token winner = simBoard.checkWin();

        // terminal nodes: win +∞, loss -∞, tie 0
        if (winner == token) return MY_INF;
        if (winner == opp) return -MY_INF;
        if (simBoard.isTie()) return 0;

        // nodes reaching cutoff need not be explored
        // further; just calculate board heuristic
        if (depth == maxDepth) {
            return calculateHeuristic();
        }

        // start simulating! if BOT's playing, maximize
        if (currPlayer == token) {
            long max = -MY_INF;

            for (int i : PSEUDOMOVES) {
                // make all possible moves, and for each resulting game state ...
                if (simBoard.isValidMove(i)) {
                    simBoard.makeMove(token, i);

                    // ... yield turn, add depth, use updated alpha beta, and simulate
                    max = Math.max(max, minimax(opp, depth + 1, alpha, beta));

                    simBoard.unmakeMove(i);

                    // alpha-beta exploits assumptions of perfect play and
                    // prunes entire subtrees that we know cannot affect
                    // final output, providing a huge speed-up
                    if (max >= beta) return max;

                    // update alpha for next successor
                    alpha = Math.max(alpha, max);
                }
            }

            return max;
        }

        // if OPP's playing, minimize
        long min = MY_INF;

        for (int i : PSEUDOMOVES) {
            if (simBoard.isValidMove(i)) {
                simBoard.makeMove(opp, i);
                min = Math.min(min, minimax(token, depth + 1, alpha, beta));
                simBoard.unmakeMove(i);

                if (min <= alpha) return min;
                beta = Math.min(beta, min);
            }
        }

        return min;
    }

    private long calculateHeuristic() {
        long score = 0;

        // for each game line, we find features that bot's
        // pieces have and features that opp's pieces have
        for (String line : simBoard.getLines()) {
            String myLine = line
                .replaceAll(token.name(), "A")
                .replaceAll(opp.name(), "B");

            String oppLine = line
                .replaceAll(opp.name(), "A")
                .replaceAll(token.name(), "B");

            // get weighted sum of bot's features, get same
            // for opp, subtract latter from former (to
            // simulate adversarial nature of Connect 4)
            for (Pattern feature : FEATURE_WEIGHTS.keySet()) {
                int weight = FEATURE_WEIGHTS.get(feature);

                score += weight * feature.matcher(myLine).results().count();
                score -= weight * feature.matcher(oppLine).results().count();
            }
        }

        return score;
    }
}
