package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// a top-level screen to facilitate switching and positioning
// (by setting size at the panel level, null layout positions
// are much more accurate, as the window header is excluded)
public class Screen extends JLayeredPane {
    private static final int UPDATE_RATE = 30;
    public static final int UPDATE_PERIOD = 1_000 / UPDATE_RATE;

    private JButton backToMenu;
    private final JPanel content;

    protected Timer timer;

    public Screen() {
        this(false);
    }

    public Screen(boolean back) {
        super();
        setPreferredSize(new Dimension(1200, 800));

        content = new JPanel();
        content.setBounds(0, 0, 1200, 800);
        super.add(content, DEFAULT_LAYER);

        if (back) {
            backToMenu = new JButton("Main Menu");
            backToMenu.setFont(new Font("", Font.BOLD, 14));
            backToMenu.setBounds(25, 25, 120, 40);
            super.add(backToMenu, PALETTE_LAYER);
        }
    }

    protected void addBackToMenuListener(ActionListener l) {
        backToMenu.addActionListener(l);
    }

    @Override
    public Component add(Component comp) {
        content.add(comp);
        return comp;
    }

    @Override
    public void add(Component comp, Object constraints) {
        content.add(comp, constraints);
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        if (content == null) {
            super.setLayout(mgr);
        } else {
            content.setLayout(mgr);
        }
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
