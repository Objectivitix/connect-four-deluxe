package net;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    // list of all `ServerThread`s, used to broadcast messages to all their clients
    public static final List<ServerThread> threads = new ArrayList<>();

    // the server who sired us all, centralized keeper of move knowledge,
    // eternally grateful we are that she brings new spectators up to speed
    private final Server mother;

    // networking resources
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // ID that corresponds with the client this thread handles
    public final int id;

    public ServerThread(Socket socket, Server mother) {
        // give thread a name (good practice, good for debugging)
        super("ServerThread@" + threads.size());

        this.socket = socket;
        this.mother = mother;

        try {
            // open reader and writer on accepted socket's streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            // if we fail, gracefully dispose
            dispose();
        }

        // set ID and add to global list
        id = threads.size();
        threads.add(this);
    }

    @Override
    public void run() {
        try {
            // first communication: let own client know of its ID,
            // and let all other clients know another has joined
            sendToAll(Protocol.join(id));

            // when first one joins for a new game, clear previous
            // game's moves, if there were any
            if (id == 0) {
                mother.clearMoves();
            }

            // if we're spectating and we joined late, let client
            // know of all previous moves to get it up to speed
            if (id > 1 && !mother.getMoves().isEmpty()) {
                out.println(Protocol.moves(mother.getMoves()));
            }

            // continually read latest comms from client
            String fromClient;
            while ((fromClient = in.readLine()) != null) {
                switch (Protocol.getType(fromClient)) {
                    // if it's a restart signal, clear all moves
                    // and let everyone else know we're restarting
                    case "restart" -> {
                        mother.clearMoves();
                        sendToAll(fromClient);
                    }

                    // if it's a move signal, add it and echo to all
                    // (so their clients can copycat the latest move)
                    case "move" -> {
                        mother.addMove(Protocol.parse(fromClient));
                        sendToAll(fromClient);
                    }

                    // if it's an exit signal coming FROM OUR OWN
                    // CLIENT, terminate
                    case "exit" -> {
                        if (Protocol.parse(fromClient) == id) return;
                    }
                }
            }
        } catch (IOException ignored) {}

        // make sure thread is disposed no matter what
        finally {
            dispose();
        }
    }

    public static void sendToAll(String message) {
        for (ServerThread thread : threads) {
            // null check fixes strange rare bug
            if (thread != null) {
                thread.out.println(message);
            }
        }
    }

    public void dispose() {
        // remove from global list and let everyone know
        // that this thread-client pair is disconnecting
        threads.remove(this);
        sendToAll(Protocol.exit(id));

        // gracefully release all networking resources
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
