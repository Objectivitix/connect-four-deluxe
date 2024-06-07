import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    public static final List<ClientHandler> handlers = new ArrayList<>();

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        super("ClientHandler@" + handlers.size());
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            dispose();
        }

        handlers.add(this);
    }

    @Override
    public void run() {
        try {
            out.println(handlers.size());

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                for (ClientHandler handler : handlers) {
                    handler.out.println(inputLine);
                }
            }

            dispose();
        }

        catch (IOException e) {
            dispose();
        }
    }

    public void dispose() {
        handlers.remove(this);

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
