package gui;

import javax.swing.*;
import java.awt.*;

public class BotMenu extends JPanel {
    public JButton[] levelBtns;

    public BotMenu() {
        super();
        setLayout(new GridLayout(1, 4, 25, 0));

        levelBtns = new JButton[]{
            new JButton("Bob"),
            new JButton("Clarke"),
            new JButton("Athena"),
            new JButton("The High One")
        };

        for (JButton btn : levelBtns) {
            add(btn);
        }
    }
}
