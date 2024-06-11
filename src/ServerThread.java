import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    public static final List<ServerThread> threads = new ArrayList<>();
    public final int id;

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket) {
        super("ServerThread@" + threads.size());
        this.socket = socket;

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

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if (Protocol.getType(inputLine).equals("move")) {
                    sendToAll(inputLine);
                }
            }

            dispose();
        }

        catch (IOException e) {
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
