package net;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    // port through which we establish client-server TCP connections
    public static final int PORT = 8888;

    // keep track of moves made during current game, used
    // to help late-joining spectators get up to speed
    private final List<Integer> moves = new ArrayList<>();

    // gets private IP of current device
    public static String getHostIP() throws UnknownHostException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UncheckedIOException e) {
            return InetAddress.getLocalHost().getHostAddress();
        }
    }

    // checks if a server already runs on this device
    public static boolean alreadyHasOneRunning() {
        // since only one ServerSocket can listen on a port at any given
        // moment, if exception thrown, we know there's already a server
        try (ServerSocket ignored = new ServerSocket(PORT)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    // accessor and mutators of `moves`
    public synchronized List<Integer> getMoves() {
        return moves;
    }

    public synchronized void addMove(int move) {
        moves.add(move);
    }

    public synchronized void clearMoves() {
        moves.clear();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // while we run, keep listening for client connection requests;
            // once accepted, delegate connected socket to a new ServerThread
            while (!serverSocket.isClosed()) {
                new ServerThread(serverSocket.accept(), this).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }
}
