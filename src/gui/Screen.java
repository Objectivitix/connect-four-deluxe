package gui;

import javax.swing.*;
import java.awt.*;

// a top-level screen to facilitate switching and positioning
// (by setting size at the panel level, null layout positions
// are much more accurate, as the window header is excluded)
public class Screen extends JPanel {
    private static final int UPDATE_RATE = 30;
    public static final int UPDATE_PERIOD = 1_000 / UPDATE_RATE;

    private static final int FRAME_RATE = 144;
    public static final int FRAME_PERIOD = 1_000 / FRAME_RATE;

    protected Timer timer;

    public Screen() {
        super();
        setPreferredSize(new Dimension(1200, 800));
    }

    public void replaceWith(Screen other) {
        Container root = getParent();
        root.remove(this);
        root.add(other);

        // stop timer if there is one
        if (timer != null) {
            timer.stop();
        }

        // update display
        root.revalidate();
        root.repaint();
    }
}
