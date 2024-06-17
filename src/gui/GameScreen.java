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
    private final Game game;
    private final Client client;

    // whether this GameScreen's Client has first-player control
    private final boolean control;

    // ensure images are only loaded once for efficiency
    private final ImageIcon redTurn, yellowTurn, redWin, yellowWin, tie, pressure;

    // various Swing components used throughout class
    private JLabel status, spectators;
    private JButton playAgain, mainMenu;
    private JLabel pressureLabel;

    public GameScreen(Game game) {
        this(game, null, false);
    }

    public GameScreen(Game game, Client client, boolean control) {
        super(true);
        setLayout(null);

        this.game = game;
        this.client = client;
        this.control = control;

        // get game logic running in a separate thread
        (new Thread(game)).start();

        // make sure if there's a client associated with this
        // screen, it disconnects when top-left menu button clicked
        addBackToMenuListener(evt -> {
            replaceWith(App.menuScreen);
            if (client != null) client.disconnect();
        });

        // create and style spectating indicator
        if (client != null && client.player == null) {
            JLabel spectating = new JLabel("SPECTATING");
            spectating.setFont(new Font("Rasa", Font.BOLD, 48));
            spectating.setForeground(Color.BLUE);
            spectating.setBounds(80, 50, 300, 100);
            add(spectating);
        }

        // create and style number of spectators indicator
        if (client != null) {
            spectators = new JLabel("0");
            spectators.setIcon(Utils.icon("eye.png", 30, 20));
            spectators.setIconTextGap(10);
            spectators.setFont(new Font("", Font.PLAIN, 30));
            spectators.setBounds(1090, 0, 150, 100);
            add(spectators);
        }

        // make game board GUI to the left
        BoardPanel boardPanel = new BoardPanel(game, client);
        boardPanel.setLocation(80, 120);
        add(boardPanel);

        // load game status images
        redTurn = Utils.icon("red-turn.png", 320, 188);
        yellowTurn = Utils.icon("yellow-turn.png", 320, 188);
        redWin = Utils.icon("red-win.png", 320, 188);
        yellowWin = Utils.icon("yellow-win.png", 320, 188);
        tie = Utils.icon("tie.png", 320, 188);
        pressure = Utils.icon("pressure.png", 320, 50);

        // create status indicator
        status = new JLabel(redTurn);
        status.setBounds(810, 120, 320, 188);
        add(status);

        // initialize "pressure's on" indicator
        pressureLabel = new JLabel();
        pressureLabel.setBounds(755, 0, 320, 100);
        add(pressureLabel);

        // start timer so screen updates automatically based
        // on what happens with Game and Client objects
        timer = new Timer(UPDATE_PERIOD, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // if a player disconnects, switch to DisconnectedScreen
        // with message accordingly (spectator or actual player)
        if (client != null && client.status == Client.PLAYER_DISCONNECTED) {
            String text = (client.player == null) ?
                "Uh oh—a player disconnected!" : "Uh oh—your opponent disconnected!";
            replaceWith(new DisconnectedScreen(text));
            return;
        }

        // if server disconnects, display that in client GUI
        if (client != null && client.status == Client.SERVER_DISCONNECTED) {
            replaceWith(new DisconnectedScreen("Uh oh—the server disconnected!"));
            return;
        }

        // if first client restarts the game, make sure we do that
        if (client != null && client.status == Client.RESTART) {
            game.reset();
            client.setStatusToRunning();
            replaceWith(new GameScreen(game, client, control));
        }

        // update components related to spectators
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

        // update status according to how game is running
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

        // if a terminal state is reached, display end game buttons
        if (terminal) {
            playAgain = new JButton("Play Again");
            playAgain.setFont(new Font("", Font.PLAIN, 24));
            playAgain.setBounds(850, 350, 250, 100);

            // play again only displayed for non-network
            // players or players having first-client control
            if (client == null) {
                playAgain.addActionListener(evt -> {
                    game.reset();
                    replaceWith(new GameScreen(game));
                });
                add(playAgain);
            } else {
                if (control) {
                    playAgain.addActionListener(evt -> client.sendRestart());
                    add(playAgain);
                }
            }

            // add a "back to main menu" button, too
            // (remember to disconnect client if we have one)
            mainMenu = new JButton("Main Menu");
            mainMenu.setFont(new Font("", Font.PLAIN, 24));
            mainMenu.setBounds(850, 470, 250, 100);
            mainMenu.addActionListener(evt -> {
                replaceWith(App.menuScreen);
                if (client != null) client.disconnect();
            });
            add(mainMenu);
        }

        // make sure updates are actually reflected in GUI
        repaint();
    }
}
