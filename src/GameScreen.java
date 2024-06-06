import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    public GameScreen(Game game) {
        super();
        setPreferredSize(new Dimension(1200, 800));
        setLayout(null);

        BoardPane boardPane = new BoardPane(game);
        boardPane.setLocation(100, 20);
        add(boardPane);
    }
}
