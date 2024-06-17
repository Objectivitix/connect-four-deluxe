package core;

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

    // when minimaxing, instead of iterating through moves 0-6,
    // we iterate through this prioritized array instead, so
    // center-moves (good outcome likelier) are evaluated first
    private static final int[] PSEUDOMOVES = {3, 2, 4, 1, 5, 0, 6};

    // board features, such as an agent having 3-in-a-row w/ adjacent spaces
    private static final Pattern THREE_DOUBLE = Pattern.compile("_AAA_");
    private static final Pattern THREE = Pattern.compile("_AAA(B|$)|(B|^)AAA_");
    private static final Pattern THREE_SPACED = Pattern.compile("A_AA|AA_A");
    private static final Pattern TWO_DOUBLE = Pattern.compile("_AA_");
    private static final Pattern TWO_GOOD = Pattern.compile("___AA(B|$)|(B|^)AA___");
    private static final Pattern TWO_MID = Pattern.compile("(B|^)__AA(B|$)|(B|^)AA__(B|$)");

    // map of board feature regexes to their weights, these
    // are the ones searched for during heuristic calculation
    private final Map<Pattern, Integer> featureWeights = new HashMap<>(Map.of(
        THREE_DOUBLE, 1_000_000,
        THREE, 300_000,
        THREE_SPACED, 300_000,
        TWO_DOUBLE, 50_000,
        TWO_GOOD, 20_000,
        TWO_MID, 10_000
    ));

    // the actual game, contrasting with simulations the bot runs
    private final Board realBoard;
    private Board simBoard;

    // bot's opponent token (used in simulation)
    private final Token opp;

    // depth at which we cut off game tree search
    private int searchDepth;

    // bot nerfs: chance of making a random move, and
    // that of making only a 2-ply move (shallower)
    private double randomMovePossibility = 0;
    private double shallowMovePossibility = 0;

    public Bot(Token token, Board board, int level) {
        super(token);
        realBoard = board;

        if (token == Token.X) opp = Token.O;
        else opp = Token.X;

        // adjust bot's depth, features used,
        // and bad move chances based on level
        switch (level) {
            case 0 -> {
                searchDepth = 4;
                randomMovePossibility = 0.2;
                shallowMovePossibility = 0.5;
                featureWeights.remove(THREE_SPACED);
                featureWeights.remove(TWO_DOUBLE);
                featureWeights.remove(TWO_GOOD);
                featureWeights.remove(TWO_MID);
            }

            case 1 -> {
                searchDepth = 4;
                randomMovePossibility = 0.1;
                shallowMovePossibility = 0.3;
                featureWeights.remove(THREE_SPACED);
                featureWeights.remove(TWO_GOOD);
                featureWeights.remove(TWO_MID);
            }

            case 2 -> {
                searchDepth = 4;
                shallowMovePossibility = 0.3;
                featureWeights.remove(TWO_GOOD);
                featureWeights.remove(TWO_MID);
            }

            // highest level: looks ahead 7 moves, no nerfs
            default -> searchDepth = 7;
        }
    }

    // driver method that invokes minimax (or random move)
    @Override
    public int getMove() {
        // activate possibility of making random moves after 5 turns
        if (realBoard.moves > 5 && Math.random() < randomMovePossibility) {
            return getRandomMove();
        }

        // so if searchDepth is changed for a
        // shallow move, we can change it back later
        int originalDepth = -1;

        // activate possibility of making shallow moves after 2 turns
        if (realBoard.moves > 2 && Math.random() < shallowMovePossibility) {
            originalDepth = searchDepth;
            searchDepth = 2;
        }

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

        if (originalDepth != -1) {
            searchDepth = originalDepth;
        }

        return move;
    }

    private int getRandomMove() {
        List<Integer> possibleMoves = new ArrayList<>();

        // get a list of available moves to make
        for (int i = 0; i < 7; i++) {
            if (realBoard.isValidMove(i)) {
                possibleMoves.add(i);
            }
        }

        // return a random one using random index
        return possibleMoves.get((int) (Math.random() * possibleMoves.size()));
    }

    private long minimax(Token currPlayer, int depth, long alpha, long beta) {
        Token winner = simBoard.checkWin();

        // terminal nodes: win +∞, loss -∞, tie 0
        if (winner == token) return MY_INF;
        if (winner == opp) return -MY_INF;
        if (simBoard.isTie()) return 0;

        // nodes reaching cutoff need not be explored
        // further; just calculate board heuristic
        if (depth == searchDepth) {
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
            for (Pattern feature : featureWeights.keySet()) {
                int weight = featureWeights.get(feature);

                score += weight * feature.matcher(myLine).results().count();
                score -= weight * feature.matcher(oppLine).results().count();
            }
        }

        return score;
    }
}
