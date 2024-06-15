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
    ImageIcon redTurn, yellowTurn, redWin, yellowWin, tie, pressure;
    JLabel pressureLabel;

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
            spectating.setBounds(80, 50, 300, 100);
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
        boardPanel.setLocation(80, 120);
        add(boardPanel);

        redTurn = Utils.icon("red-turn.png", 320, 188);
        yellowTurn = Utils.icon("yellow-turn.png", 320, 188);
        redWin = Utils.icon("red-win.png", 320, 188);
        yellowWin = Utils.icon("yellow-win.png", 320, 188);
        tie = Utils.icon("tie.png", 320, 188);
        pressure = Utils.icon("pressure.png", 320, 50);

        status = new JLabel(redTurn);
        status.setBounds(810, 120, 320, 188);
        add(status);

        pressureLabel = new JLabel();
        pressureLabel.setBounds(755, 0, 320, 100);
        add(pressureLabel);

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
            if (client.player != null) {
                if (client.spectators >= 3) {
                    pressureLabel.setIcon(pressure);
                } else {
                    pressureLabel.setIcon(null);
                }
            }
        }

        boolean terminal = false;

        if (game.winner == Token.X) {
            status.setIcon(redWin);
            terminal = true;
        } else if (game.winner == Token.O) {
            status.setIcon(yellowWin);
            terminal = true;
        } else if (game.board.isTie()) {
            status.setIcon(tie);
            terminal = true;
        } else if (game.currAgent.token == Token.X) {
            status.setIcon(redTurn);
        } else {
            status.setIcon(yellowTurn);
        }

        if (terminal) {
            if (client == null) {
                playAgain = new JButton("Play Again");
                playAgain.setFont(new Font("", Font.PLAIN, 24));
                playAgain.setBounds(850, 350, 250, 100);
                playAgain.addActionListener(evt -> {
                    game.reset();
                    replaceWith(new GameScreen(game));
                });
                add(playAgain);
            } else {
                if (control) {
                    playAgain = new JButton("Play Again");
                    playAgain.setFont(new Font("", Font.PLAIN, 24));
                    playAgain.setBounds(850, 350, 250, 100);
                    playAgain.addActionListener(evt -> client.sendRestart());
                    add(playAgain);
                }
            }

            mainMenu = new JButton("Main Menu");
            mainMenu.setFont(new Font("", Font.PLAIN, 24));
            mainMenu.setBounds(850, 470, 250, 100);
            mainMenu.addActionListener(evt -> {
                replaceWith(new MenuScreen());
                if (client != null) client.disconnect();
            });
            add(mainMenu);
        }

        repaint();
    }
}
