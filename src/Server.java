import java.io.*;
import java.net.*;

public class Server implements Runnable {
    public static final int PORT = 8888;

    public static String getHostIP() throws UnknownHostException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UncheckedIOException e) {
            return InetAddress.getLocalHost().getHostAddress();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!serverSocket.isClosed()) {
                if (ServerThread.threads.size() < 2) {
                    new ServerThread(serverSocket.accept()).start();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }
}
