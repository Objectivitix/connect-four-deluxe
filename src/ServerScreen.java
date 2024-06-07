import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;

public class ServerScreen extends Screen {
    public ServerScreen() {
        super();
        setLayout(new GridLayout());

        JLabel title;

        try {
            title = new JLabel(Server.getHostIP());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        title.setFont(new Font("Rasa", Font.BOLD, 60));
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title);

        new Thread(new Server()).start();
    }
}
