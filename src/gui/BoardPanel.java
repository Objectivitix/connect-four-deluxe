package gui;

import core.Game;
import core.Player;
import core.Token;
import net.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel implements ActionListener {
    private Game game;
    private JButton[] columnButtons;
    private Client client;

    public BoardPanel(Game game, Client client) {
        super();
//        setPreferredSize(new Dimension(700, 600));
        setSize(700, 600);
        setLayout(new GridLayout(1, 7));
        setBackground(Color.BLUE);
        setOpaque(true);

        this.game = game;
        this.client = client;

        columnButtons = new JButton[7];

        for (int i = 0; i < 7; i++) {
            int closureI = i;
            JButton column = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    if (game.board.isValidMove(closureI)) {
                        super.paintComponent(g);
                    }

                    Graphics2D g2D = (Graphics2D) g;
                    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    for (int j = 0; j < 6; j++) {
                        Token[][] b = game.board.getRotatedBoard();
                        g2D.setPaint(b[j][closureI] == Token.X ? Color.RED
                            : b[j][closureI] == Token.O ? Color.YELLOW
                            : Color.WHITE);

                        g2D.fillOval(10, j * 95 + 22, 80, 80);

                        if (5 - j == game.board.recentJ && closureI == game.board.recentI) {
                            g2D.setStroke(new BasicStroke(10));
                            g2D.setPaint(new Color(0, 230, 0));
                            g2D.drawOval(12, j * 95 + 24, 76, 76);
                        }
                    }
                }
            };

            column.setBackground(Color.BLUE);
            column.setOpaque(true);
            column.setBorder(null);

            column.addActionListener(this);

            column.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    column.setBackground(new Color(0, 230, 0));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    column.setBackground(Color.BLUE);
                }
            });

            columnButtons[i] = column;
            add(column);
        }

        new Timer(Screen.UPDATE_PERIOD, this).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        int i;

        for (i = 0; i < 7; i++) {
            if (source == columnButtons[i]) {
                if (game.board.isValidMove(i) && game.currAgent instanceof Player p) {
                    if (client == null) p.holdOn(i);
                    else if (client.player == p) client.sendToServer(i);
                }

                break;
            }
        }

//        repaint();
    }
}
