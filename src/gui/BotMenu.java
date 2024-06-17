package gui;

import utils.Utils;

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

        // add icons to buttons
        levelBtns[0].setIcon(Utils.icon("bob.png", 160, 120));
        levelBtns[1].setIcon(Utils.icon("clarke.png", 160, 120));
        levelBtns[2].setIcon(Utils.icon("athena.png", 160, 120));
        levelBtns[3].setIcon(Utils.icon("high-one.png", 180, 135));

        // adjust spacing and layout of icon and button text
        for (JButton button : levelBtns) {
            button.setFont(new Font("", Font.PLAIN, 24));
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.TOP);
            button.setIconTextGap(button.getText().contains("High") ? 15 : 30);

            add(button);
        }
    }
}
