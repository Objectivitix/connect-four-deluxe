import java.util.*;
import java.util.regex.Pattern;

// Minimax powers this Connect 4 bot. The algorithm consists of:
//    1) a recursive game tree searcher that maximizes "gains"
//       for bot, assuming opponent plays perfectly (constantly
//       minimizing bot's gains)
//    2) a heuristic to decide how "good" a non-terminal game
//       board is - it's a weighted sum of board "features"

public class Bot {
    // an int big enough to mean infinity, small
    // enough to be free of overflow concerns
    private static final int MY_INF = 999_999_999;

    // map of board feature regexes to their weights
    // (so three-in-a-row w/ adjacent spaces has weight 1_000_000)
    private static final Map<Pattern, Integer> FEATURE_WEIGHTS = Map.of(
        Pattern.compile("_AAA_"), 1_000_000,
        Pattern.compile("_AAA(B|$)|(B|^)AAA_"), 300_000,
        Pattern.compile("A_AA|AA_A"), 300_000,
        Pattern.compile("_AA_"), 50_000,
        Pattern.compile("___AA(B|$)|(B|^)AA___"), 20_000,
        Pattern.compile("(B|^)__AA(B|$)|(B|^)AA__(B|$)"), 10_000
    );

    // the actual game, contrasting with simulations the bot runs
    private final Game realGame;

    // who the bot represents and its opponent
    public final Player player;
    private final Player opp;

    // depth at which we cut off game tree search
    private final int maxDepth;

    public Bot(Game game, Player player, int lookaheadDepth) {
        realGame = game;
        this.player = player;

        if (player == Player.X) opp = Player.O;
        else opp = Player.X;

        maxDepth = lookaheadDepth;
    }

    // driver method that invokes minimax
    public int getOptimalMove() {
        long max = -MY_INF;
        int move = -1;

        for (int i = 0; i < 7; i++) {
            if (realGame.isValidMove(i)) {
                // if it's a valid move, we make a successor
                Game succ = realGame.deepCopy();
                succ.makeMove(player, i);

                // search this successor state recursively
                long result = minimax(succ, opp, 1, -MY_INF, MY_INF);

                // record move associated with maximum gains
                if (result >= max) {
                    max = result;
                    move = i;
                }
            }
        }

        return move;
    }

    private long minimax(Game state, Player currPlayer, int depth, long alpha, long beta) {
        Player winner = state.checkWin();

        // terminal nodes: win +∞, loss -∞, tie 0
        if (winner == player) return MY_INF;
        if (winner == opp) return -MY_INF;
        if (state.isTie()) return 0;

        // nodes reaching cutoff need not be explored
        // further; just calculate board heuristic
        if (depth == maxDepth) {
            return calculateHeuristic(state);
        }

        // start simulating! if BOT's playing, maximize
        if (currPlayer == player) {
            long max = -MY_INF;

            // make all possible moves, and for each resulting game state ...
            for (Game succ : getSuccessors(state, player)) {
                // ... yield turn, add depth, use updated alpha beta, and simulate
                max = Math.max(max, minimax(succ, opp, depth + 1, alpha, beta));

                // alpha-beta exploits assumptions of perfect play and
                // prunes entire subtrees that we know cannot affect
                // final output, providing a huge speed-up
                if (max >= beta) return max;

                // update alpha for next successor
                alpha = Math.max(alpha, max);
            }

            return max;
        }

        // if OPP's playing, minimize
        long min = MY_INF;

        for (Game succ : getSuccessors(state, opp)) {
            min = Math.min(min, minimax(succ, player, depth + 1, alpha, beta));
            if (min <= alpha) return min;
            beta = Math.min(beta, min);
        }

        return min;
    }

    // get successor game states (children of a game tree node)
    private List<Game> getSuccessors(Game state, Player currPlayer) {
        List<Game> succs = new ArrayList<>();

        // check each column
        for (int i = 0; i < 7; i++) {
            if (state.isValidMove(i)) {
                // make copy of game state
                Game succ = state.deepCopy();

                // make move, add to successors
                succ.makeMove(currPlayer, i);
                succs.add(succ);
            }
        }

        return succs;
    }

    private long calculateHeuristic(Game state) {
        long score = 0;

        // for each game line, we find features that bot's
        // pieces have and features that opp's pieces have
        for (String line : state.getLines()) {
            String myLine = line
                .replaceAll(player.name(), "A")
                .replaceAll(opp.name(), "B");

            String oppLine = line
                .replaceAll(opp.name(), "A")
                .replaceAll(player.name(), "B");

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
