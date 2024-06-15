package net;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    public static final int PORT = 8888;

    private final List<Integer> moves = new ArrayList<>();

    public static String getHostIP() throws UnknownHostException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UncheckedIOException e) {
            return InetAddress.getLocalHost().getHostAddress();
        }
    }

    public static boolean alreadyHasOneRunning() {
        try (ServerSocket ignored = new ServerSocket(PORT)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

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
            while (!serverSocket.isClosed()) {
                new ServerThread(serverSocket.accept(), this).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }
}
