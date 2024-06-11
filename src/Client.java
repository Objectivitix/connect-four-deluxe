import java.io.*;
import java.net.*;

public class Client implements Runnable {
    public static final int RUNNING = 0;
    public static final int SERVER_DISCONNECTED = 1;
    public static final int OPPONENT_DISCONNECTED = 2;

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

            player = (Player) (id == 0 ? game.one : game.two);

            return 0;
        }

        catch (UnknownHostException e) {
            dispose();
            return 1;
        }

        catch (IOException e) {
            dispose();
            return 2;
        }
    }

    @Override
    public void run() {
        status = RUNNING;
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                switch (Protocol.getType(fromServer)) {
                    case "move" -> {
                        if (game.currAgent instanceof Player p) {
                            p.holdOn(Protocol.parse(fromServer));
                        }
                    }

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
