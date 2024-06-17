package net;

import core.Game;
import core.Player;

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    // client statuses
    public static final int STANDBY = -1;
    public static final int RUNNING = 0;
    public static final int GOOD_TO_PLAY = 1;
    public static final int SERVER_DISCONNECTED = 10;
    public static final int PLAYER_DISCONNECTED = 11;
    public static final int RESTART = 20;

    public int status = STANDBY;

    // networking resources
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // ID that corresponds with that of the
    // ServerThread handling this client
    private int id;

    // server IP address to attempt to connect to
    private final String address;

    // underlying game and player of this client
    private final Game game;
    public Player player;

    // number of spectators on this game
    public int spectators = 0;

    public Client(String address, Game game) {
        this.address = address;
        this.game = game;
        socket = new Socket();
    }

    public int connect() {
        try {
            // attempt connection with a 10-second timeout
            socket.connect(new InetSocketAddress(address, Server.PORT), 10_000);

            // if successful, open writer and reader on the socket's streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            // get ID from first line of communications (sent by ServerThread)
            id = Protocol.parse(in.readLine());

            // determine underlying player based on ID
            if (id == 0) {
                player = (Player) game.one;
            } else if (id == 1) {
                player = (Player) game.two;
            } else {
                // ID is 2 or above: you're a spectator
                player = null;
                spectators = id - 1;
            }

            return id;
        }

        // if connection crashes in any way (ex. timeout expires),
        // dispose this socket and return unsuccessful
        catch (IOException e) {
            dispose();
            return -1;
        }
    }

    public void setStatusToRunning() {
        status = RUNNING;
    }

    @Override
    public void run() {
        status = RUNNING;

        try {
            // continually read latest comms from server(thread)
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                switch (Protocol.getType(fromServer)) {
                    // if it's a restart signal,
                    // set status for GUI to respond
                    case "restart" -> status = RESTART;

                    // if it's a join signal, set status
                    // for WaitingScreen and update number
                    // of spectators if needed
                    case "join" -> {
                        status = GOOD_TO_PLAY;
                        int otherId = Protocol.parse(fromServer);
                        if (otherId > 1) spectators = otherId - 1;
                    }

                    // if it's a move signal, ping current player with the move
                    // (who will return it to a patiently waiting Game object)
                    case "move" -> ((Player) game.currAgent).receiveMove(Protocol.parse(fromServer));

                    // if it's many moves, parse 'em and send them all off,
                    // waiting 20 ms between each ping to avoid overloading/racing
                    // the threads (also makes for sick fill-up effect)
                    case "moves" -> {
                        for (int i : Protocol.parseMoves(fromServer)) {
                            ((Player) game.currAgent).receiveMove(i);
                            Thread.sleep(20);
                        }
                    }

                    // if it's an exit signal, check if it's critical, and if so,
                    // update status for DisconnectedScreen; otherwise, just
                    // decrement number of spectators
                    case "exit" -> {
                        int exitedId = Protocol.parse(fromServer);

                        if (exitedId == Protocol.SERVER_ID) {
                            status = SERVER_DISCONNECTED;
                            return;
                        } else if (exitedId < 2 && exitedId != id) {
                            status = PLAYER_DISCONNECTED;
                            return;
                        } else {
                            spectators--;
                        }
                    }
                }
            }
        }

        // catch exceptions to satisfy compiler
        catch (IOException e) {
            System.err.println("Error reading from " + address);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // make sure client is disposed upon exit
        finally {
            dispose();
        }
    }

    public void sendMove(int move) {
        out.println(Protocol.move(move));
    }

    public void sendRestart() {
        out.println(Protocol.restart());
    }

    public void disconnect() {
        // tell corresponding ServerThread to dispose itself,
        // which will ripple to dispose this client too (since
        // in.readLine() in main `run` loop will become null)
        out.println(Protocol.exit(id));
    }

    public void dispose() {
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
