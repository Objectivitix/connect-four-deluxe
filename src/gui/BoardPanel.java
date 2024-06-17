package gui;

import core.Game;
import core.Player;
import core.Token;
import net.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel implements ActionListener {
    private Game game;
    private Client client;

    // each column of game board is a JButton,
    // stored here for easy iteration
    private JButton[] columnButtons;

    public BoardPanel(Game game, Client client) {
        super();

        // set size, layout, background
        setSize(700, 600);
        setLayout(new GridLayout(1, 7));
        setBackground(Color.BLUE);
        setOpaque(true);

        // initialize instance variables
        this.game = game;
        this.client = client;
        columnButtons = new JButton[7];

        // bind each column to a move and add to array of buttons
        for (int i = 0; i < 7; i++) {
            JButton column = createColumn(game, client, i);

            columnButtons[i] = column;
            add(column);
        }
    }

    private JButton createColumn(Game game, Client client, int i) {
        JButton column = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // get graphics object and make it antialiasing
                Graphics2D g2D = (Graphics2D) g;
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // paint tokens accordingly (empty = white,
                // X = red, O = yellow)
                for (int j = 0; j < 6; j++) {
                    Token[][] b = game.board.getRotatedBoard();
                    g2D.setPaint(b[j][i] == Token.X ? Color.RED
                        : b[j][i] == Token.O ? Color.YELLOW
                        : Color.WHITE);

                    g2D.fillOval(10, j * 95 + 22, 80, 80);

                    // encircle most recent token with a green indicator
                    // so users know what move was made by opponent
                    if (5 - j == game.board.recentJ && i == game.board.recentI) {
                        g2D.setStroke(new BasicStroke(10));
                        g2D.setPaint(new Color(0, 230, 0));
                        g2D.drawOval(12, j * 95 + 24, 76, 76);
                    }
                }
            }
        };

        // remove default styles and set bg to blue
        column.setContentAreaFilled(false);
        column.setBackground(Color.BLUE);
        column.setOpaque(true);
        column.setBorder(null);

        // bind to BoardPanel object's action listener
        column.addActionListener(this);

        // add hover and click effects, making sure they only
        // appear when column can be clicked to make a move
        column.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // mouse down: switch to light gray
                if (canCurrentlyPlay() && game.board.isValidMove(i))
                    column.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // mouse enters button bounds: switch to green
                if (canCurrentlyPlay() && game.board.isValidMove(i))
                    column.setBackground(new Color(0, 230, 0));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // mouse click in button bounds: switch to green (b/c still hovering)
                if (canCurrentlyPlay() && game.board.isValidMove(i))
                    column.setBackground(new Color(0, 230, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // mouse leaves button bounds: switch back to blue
                column.setBackground(Color.BLUE);
            }
        });

        return column;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        int i;

        // iterate over all column buttons until
        // we find the one that has been clicked
        for (i = 0; i < 7; i++) {
            if (source == columnButtons[i]) {
                // then, if this is a valid click, we make move accordingly
                if (canCurrentlyPlay() && game.board.isValidMove(i)) {
                    Player currPlayer = (Player) game.currAgent;

                    if (client == null) currPlayer.receiveMove(i);
                    else if (client.player == currPlayer) client.sendMove(i);
                }

                // we've found the button, so exit early for efficiency
                return;
            }
        }
    }

    // determines whether this BoardPanel can currently let user
    // make a move and display styles accordingly; accounts for all
    // cases, both offline and online - and when playing against AI
    private boolean canCurrentlyPlay() {
        return game.winner == null && !game.board.isTie()
            && (client == null || client.player != null && client.player == game.currAgent)
            && game.currAgent instanceof Player;
    }
}
