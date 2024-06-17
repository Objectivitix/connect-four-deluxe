package gui;

import core.*;
import net.Protocol;
import net.Server;
import net.ServerThread;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends Screen implements ActionListener {
    // preload welcome image
    ImageIcon welcome;

    // big containers, the title, and set of menu buttons
    JPanel body, center;
    JLabel title;
    JButton local, withBot, startServer, joinServer;

    // replaces center with a new set of menu buttons, this time
    // for adjusting AI's level, when `withBot` button clicked
    BotMenu menu;

    public MenuScreen() {
        super();
        setLayout(new BorderLayout());

        // create top half
        JPanel top = Utils.spacer(430);
        top.setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);

        // this is so we can place title in bottom half of top half
        JPanel header = new JPanel();
        top.add(header, BorderLayout.SOUTH);

        // create centered title with welcome image
        welcome = Utils.icon("welcome.png", 900, 375);
        title = new JLabel(welcome);
        title.setHorizontalAlignment(JLabel.CENTER);
        header.add(title);

        // carve out space for actual menu
        body = new JPanel();
        body.setLayout(new BorderLayout());
        add(body, BorderLayout.CENTER);

        // take up space for vertical gap between title and menu
        body.add(Utils.spacer(20), BorderLayout.NORTH);

        // take up space on other 3 sides to constrain center
        body.add(Utils.spacer(150), BorderLayout.WEST);
        body.add(Utils.spacer(150), BorderLayout.EAST);
        body.add(Utils.spacer(100), BorderLayout.SOUTH);

        // use grid layout for menu buttons
        center = new JPanel(new GridLayout(1, 4, 35, 0));
        body.add(center, BorderLayout.CENTER);

        // create those buttons and set their icons
        JButton[] buttons = new JButton[4];

        buttons[0] = local = new JButton("Play Locally");
        local.setIcon(Utils.icon("local.png", 100, 100));

        buttons[1] = withBot = new JButton("Play with AI");
        withBot.setIcon(Utils.icon("bot.png", 100, 100));

        buttons[2] = startServer = new JButton("Start Server");
        startServer.setIcon(Utils.icon("server.png", 100, 100));

        buttons[3] = joinServer = new JButton("Join Server");
        joinServer.setIcon(Utils.icon("join.png", 100, 100));

        // style, bind, and add each button
        for (JButton button : buttons) {
            button.setText(Utils.center(button.getText()));
            button.setFont(new Font("", Font.PLAIN, 24));
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.TOP);
            button.setIconTextGap(30);
            button.addActionListener(this);
            center.add(button);
        }

        // preload BotMenu in separate thread to avoid slowing down startup
        new Thread(() -> {
            // must synchronize as we're mutating this object within thread
            synchronized (this) {
                menu = new BotMenu();
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == local) {
            // play locally: simply direct to a GameScreen with new game
            replaceWith(new GameScreen(Game.newGame()));
        } else if (e.getSource() == withBot) {
            // play with AI: switch to bot selection image and BotMenu
            title.setIcon(Utils.icon("bot-selection.png", 900, 375));
            body.remove(center);
            body.add(menu, BorderLayout.CENTER);

            // bind each bot level button with starting a new game
            for (int i = 0; i < menu.levelBtns.length; i++) {
                int closureI = i;
                menu.levelBtns[i].addActionListener(evt -> {
                    replaceWith(new GameScreen(Game.newGame(closureI)));

                    // change title and menu back, so when user goes
                    // back to main menu, it'll be the main menu again
                    title.setIcon(welcome);
                    body.remove(menu);
                    body.add(center, BorderLayout.CENTER);
                });
            }

            // adding 'n removing stuff invalidates layout, so
            // we revalidate before making GUI reflect everything
            revalidate();
            repaint();
        } else if (e.getSource() == startServer) {
            // start server: if we have one running already, disallow
            if (Server.alreadyHasOneRunning()) {
                Utils.alert(App.frame, "There's already a server running on this machine!");
                return;
            }

            // otherwise, all's good - let's make sure that if user closes
            // window, all clients are notified of server's disconnection
            App.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ServerThread.sendToAll(Protocol.exitServer());
                }
            });

            // then, switch to ServerScreen
            replaceWith(new ServerScreen());
        } else if (e.getSource() == joinServer) {
            // join server: simply direct to a JoinScreen
            replaceWith(new JoinScreen());
        }
    }
}
