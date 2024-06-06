import javax.swing.*;
import java.awt.*;

// a top-level screen to facilitate switching and positioning
// (by setting size at the panel level, null layout positions
// are much more accurate, as the window header is excluded)
public class Screen extends JPanel {
    public Screen() {
        super();
        setPreferredSize(new Dimension(1200, 800));
    }

    public void replaceWith(Screen other) {
        Container root = getParent();
        root.remove(this);
        root.add(other);

        // update display
        root.revalidate();
        root.repaint();
    }
}
