import java.io.*;
import java.net.*;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String address;
    private Game game;

    public Player player;

    public Client(String address, Game game) {
        this.address = address;
        this.game = game;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, Server.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            player = (Player) ((Integer.parseInt(in.readLine()) < 2) ? game.one : game.two);

            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                if (game.currAgent instanceof Player p) {
                    p.holdOn(Integer.parseInt(fromServer));
                }
            }

            dispose();
        }

        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + address);
            dispose();
        }

        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + address);
            dispose();
        }
    }

    public void sendToServer(int move) {
        out.println(move);
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
