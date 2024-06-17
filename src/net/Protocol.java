package net;

import java.util.ArrayList;
import java.util.List;

// utility class that aids implementation of the Connect 4 Protocol™
// (C4P), used to comm between clients and server. I define C4P as such:
//
//     join N    ServerThread@N has started; client N has connected (N >= 0)
//     move N    a move for column N was made (0 <= N < 7)
//     moves A B C ...    many moves; used to sync up late-joining spectators
//     exit -1   server has disconnected
//     exit N    client N has disconnected; ServerThread@N disposed (N >= 0)
//     restart   party leader pressed "play again"
public class Protocol {
    public static final int SERVER_ID = -1;

    public static String join(int id) {
        return "join " + id;
    }

    public static String move(int i) {
        return "move " + i;
    }

    public static String moves(List<Integer> moves) {
        StringBuilder sb = new StringBuilder("moves ");

        for (int move : moves) {
            sb.append(move).append(" ");
        }

        // remove trailing space
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String exit(int id) {
        return "exit " + id;
    }

    public static String exitServer() {
        return exit(SERVER_ID);
    }

    public static String restart() {
        return "restart";
    }

    public static String getType(String line) {
        return line.split(" ")[0];
    }

    public static int parse(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }

    public static List<Integer> parseMoves(String line) {
        List<Integer> moves = new ArrayList<>();

        // get only the numbers that indicate moves
        for (String s : line.replaceFirst("^moves ", "").split(" ")) {
            moves.add(Integer.parseInt(s));
        }

        return moves;
    }
}
