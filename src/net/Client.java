package net;

import core.Game;
import core.Player;

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    public static final int RUNNING = 0;
    public static final int GOOD_TO_PLAY = 1;
    public static final int SERVER_DISCONNECTED = 10;
    public static final int OPPONENT_DISCONNECTED = 11;
    public static final int RESTART = 20;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int id;

    private String address;
    private Game game;

    public Player player;
    public int status = -1;

    public Client(String address, Game game) {
        this.address = address;
        this.game = game;
        socket = new Socket();
    }

    public int connect() {
        try {
            socket.connect(new InetSocketAddress(address, Server.PORT), 10_000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            id = Protocol.parse(in.readLine());

            if (id == 0) {
                player = (Player) game.one;
            } else if (id == 1) {
                player = (Player) game.two;
            } else {
                player = null;
            }

            return id;
        }

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
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                switch (Protocol.getType(fromServer)) {
                    case "restart" -> status = RESTART;
                    case "join" -> status = GOOD_TO_PLAY;
                    case "move" -> ((Player) game.currAgent).holdOn(Protocol.parse(fromServer));

                    case "exit" -> {
                        int exitedId = Protocol.parse(fromServer);
                        if (exitedId == -1) {
                            status = SERVER_DISCONNECTED;
                            dispose();
                            return;
                        }

                        if (Protocol.parse(fromServer) < 2 && exitedId != id) {
                            status = OPPONENT_DISCONNECTED;
                            dispose();
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from " + address);
        } finally {
            dispose();
        }
    }

    public void sendToServer(int move) {
        out.println(Protocol.move(move));
    }

    public void sendRestart() {
        out.println("restart");
    }

    public void disconnect() {
        out.println(Protocol.exit(id));
    }

    public void dispose() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
