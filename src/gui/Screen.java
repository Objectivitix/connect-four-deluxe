package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// a top-level screen to facilitate switching and positioning
// (by setting size at the panel level, null layout positions
// are much more accurate, as the window header is excluded)
public class Screen extends JLayeredPane {
    // constant shared across GUI package, dictates how many
    // updates/repaints/calculations are carried out per second
    private static final int UPDATE_RATE = 30;
    public static final int UPDATE_PERIOD = 1_000 / UPDATE_RATE;

    // all screens have two children: an optional main menu
    // button, and a panel containing all the usual content
    private JButton backToMenu;
    private final JPanel content;

    // a screen may choose to activate a timer to carry out
    // updates every UPDATE_PERIOD
    protected Timer timer;

    public Screen() {
        this(false);
    }

    public Screen(boolean back) {
        super();
        setPreferredSize(new Dimension(1200, 800));

        // content panel spans entire screen, add to lowest layer
        content = new JPanel();
        content.setBounds(0, 0, 1200, 800);
        super.add(content, DEFAULT_LAYER);

        // if we want a main menu button, do that top-left
        if (back) {
            backToMenu = new JButton("Main Menu");
            backToMenu.setFont(new Font("", Font.BOLD, 14));
            backToMenu.setBounds(25, 25, 120, 40);

            // add to second-lowest layer so it always
            // appears above rest of standard content
            super.add(backToMenu, PALETTE_LAYER);
        }
    }

    // we let subclasses add their own listeners because some might
    // have more to do than others (for example, networked GUI components
    // must safely disconnect/dispose their network objects/resources
    // before returning to main menu)
    protected void addBackToMenuListener(ActionListener l) {
        backToMenu.addActionListener(l);
    }

    // override common methods so that they manipulate content panel
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
        // remove this child and add the other screen
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
