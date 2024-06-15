package net;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    public static final List<ServerThread> threads = new ArrayList<>();
    public final int id;

    private final Server mother;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket, Server mother) {
        super("ServerThread@" + threads.size());
        this.socket = socket;
        this.mother = mother;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            dispose();
        }

        id = threads.size();
        threads.add(this);
    }

    @Override
    public void run() {
        try {
            sendToAll(Protocol.join(id));

            if (id == 0) {
                mother.clearMoves();
            }

            if (id > 1 && !mother.getMoves().isEmpty()) {
                out.println(Protocol.moves(mother.getMoves()));
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                switch (Protocol.getType(inputLine)) {
                    case "restart" -> {
                        mother.clearMoves();
                        sendToAll(inputLine);
                    }

                    case "move" -> {
                        mother.addMove(Protocol.parse(inputLine));
                        sendToAll(inputLine);
                    }

                    case "exit" -> {
                        if (Protocol.parse(inputLine) == id) dispose();
                    }
                }
            }
        }

        catch (IOException ignored) {}
        finally {
            dispose();
        }
    }

    public static void sendToAll(String message) {
        for (ServerThread handler : threads) {
            handler.out.println(message);
        }
    }

    public void dispose() {
        threads.remove(this);
        sendToAll(Protocol.exit(id));

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
