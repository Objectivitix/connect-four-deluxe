import javax.swing.*;
import java.awt.*;

public class BoardPane extends JLayeredPane {
    public BoardPane(Game game) {
        super();
        setSize(700, 700);
        setLayout(new BorderLayout());

        add(new BoardPanel(game), BorderLayout.SOUTH);
    }
}
