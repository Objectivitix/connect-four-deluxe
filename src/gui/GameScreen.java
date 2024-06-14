package gui;

import core.Game;
import core.Token;
import net.Client;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen extends Screen implements ActionListener {
    Game game;
    Client client;
    boolean control;
    JLabel status, spectators;
    JButton playAgain, mainMenu;

    public GameScreen(Game game) {
        this(game, null, false);
    }

    public GameScreen(Game game, Client client, boolean control) {
        super();
        setLayout(null);

        this.game = game;
        this.client = client;
        this.control = control;

        (new Thread(game)).start();

//        BoardPane boardPane = new BoardPane(game, client);
//        boardPane.setLocation(100, 20);
//        add(boardPane);

        if (client != null && client.player == null) {
            JLabel spectating = new JLabel("SPECTATING");
            spectating.setFont(new Font("Rasa", Font.BOLD, 48));
            spectating.setForeground(Color.BLUE);
            spectating.setBounds(100, 50, 300, 100);
            add(spectating);
        }

        if (client != null) {
            spectators = new JLabel("0");
            spectators.setIcon(Utils.icon("eye.png", 30, 20));
            spectators.setIconTextGap(10);
            spectators.setFont(new Font("", Font.PLAIN, 30));
            spectators.setBounds(1090, 0, 150, 100);
            add(spectators);
        }

        BoardPanel boardPanel = new BoardPanel(game, client);
        boardPanel.setLocation(100, 120);
        add(boardPanel);

        status = new JLabel("Red, your turn");
        status.setFont(new Font("Rasa", Font.BOLD, 30));
        status.setBounds(850, 50, 300, 200);
        add(status);

        timer = new Timer(UPDATE_PERIOD, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client != null && client.status == Client.PLAYER_DISCONNECTED) {
            String text = (client.player == null) ?
                "Uh oh—a player disconnected!" : "Uh oh—your opponent disconnected!";
            replaceWith(new DisconnectedScreen(text));
            return;
        }

        if (client != null && client.status == Client.SERVER_DISCONNECTED) {
            replaceWith(new DisconnectedScreen("Uh oh—the server disconnected!"));
            return;
        }

        if (client != null && client.status == Client.RESTART) {
            game.reset();
            client.setStatusToRunning();
            replaceWith(new GameScreen(game, client, control));
        }

        if (client != null) {
            spectators.setText(String.valueOf(client.spectators));
        }

        boolean terminal = false;

        if (game.winner == Token.X) {
            status.setText("Red wins!");
            terminal = true;
        } else if (game.winner == Token.O) {
            status.setText("Yellow wins!");
            terminal = true;
        } else if (game.board.isTie()) {
            status.setText("Wow, it's a tie!");
            terminal = true;
        } else if (game.currAgent.token == Token.X) {
            status.setText("Red, your turn");
        } else {
            status.setText("Yellow, your turn");
        }

        if (terminal) {
            if (client == null) {
                playAgain = new JButton("Play Again");
                playAgain.setFont(new Font("", Font.PLAIN, 24));
                playAgain.setBounds(850, 300, 250, 100);
                playAgain.addActionListener(evt -> {
                    game.reset();
                    replaceWith(new GameScreen(game));
                });
                add(playAgain);
            } else {
                if (control) {
                    playAgain = new JButton("Play Again");
                    playAgain.setFont(new Font("", Font.PLAIN, 24));
                    playAgain.setBounds(850, 300, 250, 100);
                    playAgain.addActionListener(evt -> client.sendRestart());
                    add(playAgain);
                }
            }

            mainMenu = new JButton("Main Menu");
            mainMenu.setFont(new Font("", Font.PLAIN, 24));
            mainMenu.setBounds(850, 420, 250, 100);
            mainMenu.addActionListener(evt -> {
                replaceWith(new MenuScreen());
                if (client != null) client.disconnect();
            });
            add(mainMenu);
        }

        repaint();
    }
}
