package net;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String exit(int id) {
        return "exit " + id;
    }

    public static boolean isValid(String line) {
        return line.matches("join \\d+|move [0-6]|exit -1|exit \\d+");
    }

    public static String getType(String line) {
        return line.split(" ")[0];
    }

    public static int parse(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }

    public static List<Integer> parseMoves(String line) {
        List<Integer> moves = new ArrayList<>();
        for (String s : line.replaceFirst("^moves ", "").split(" ")) {
            moves.add(Integer.parseInt(s));
        }
        return moves;
    }
}
