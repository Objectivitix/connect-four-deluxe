package gui;

import core.Game;
import net.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardPane extends JLayeredPane implements ActionListener {
    private Color color;

    public int recentI;
    public int recentJ;

    private double yPos = 0;
    private double yVel = 1;
    public boolean inAnimation = false;

    public BoardPane(Game game, Client client) {
        super();
        setSize(700, 700);
        setLayout(null);

        JPanel p = new BoardPanel(game, client);
        p.setLocation(0, 100);
        add(p, DEFAULT_LAYER);

        JPanel animation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2D = (Graphics2D) g;
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (inAnimation) {
                    g2D.setPaint(color);
                    g2D.fillOval(recentI * 100 + 10, (int) yPos, 80, 80);
                }
            }
        };

        animation.setBounds(0, 0, 700, 700);
        animation.setOpaque(false);
        add(animation, PALETTE_LAYER);

        new Timer(Screen.FRAME_PERIOD, this).start();
    }

    public void animate(Color color, int i, int j) {
        this.color = color;
        recentI = i;
        recentJ = 5 - j;
        yPos = 0;
        yVel = 1;
        inAnimation = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inAnimation) {
            yPos += yVel;
            yVel += 0.03;

            if (yPos + 10 > recentJ * 95 + 22) {
                inAnimation = false;
            }

            repaint();
        }
    }
}
