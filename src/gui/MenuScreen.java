package gui;

import core.*;
import net.ServerThread;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuScreen extends Screen implements ActionListener {
    JPanel body, center;
    JButton local, withBot, startServer, joinServer;
    BotMenu menu;
    JLabel title;

    public MenuScreen() {
        // initialize with border layout
        super();
        setLayout(new BorderLayout());

        // create top half
        JPanel top = Utils.spacer(430);
        top.setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);

        // this is so we can place title in bottom half of top half
        JPanel header = new JPanel();
        top.add(header, BorderLayout.SOUTH);

        // create centered title with cool font
        title = new JLabel(Utils.icon("welcome.png", 900, 375));
        title.setHorizontalAlignment(JLabel.CENTER);
        header.add(title);

        body = new JPanel();
        body.setLayout(new BorderLayout());
        add(body, BorderLayout.CENTER);

        // take up space for vertical gap between title and buttons
        body.add(Utils.spacer(20), BorderLayout.NORTH);

        // take up space on other 3 sides to constrain center
        body.add(Utils.spacer(150), BorderLayout.WEST);
        body.add(Utils.spacer(150), BorderLayout.EAST);
        body.add(Utils.spacer(100), BorderLayout.SOUTH);

        // create grid layout where we will place menu
        center = new JPanel(new GridLayout(1, 4, 35, 0));
        body.add(center, BorderLayout.CENTER);

        JButton[] buttons = new JButton[4];

        buttons[0] = local = new JButton("Play Locally");
        local.setIcon(Utils.icon("local.png", 100, 100));

        buttons[1] = withBot = new JButton("Play with AI");
        withBot.setIcon(Utils.icon("bot.png", 100, 100));

        buttons[2] = startServer = new JButton("Start Server");
        startServer.setIcon(Utils.icon("server.png", 100, 100));

        buttons[3] = joinServer = new JButton("Join Server");
        joinServer.setIcon(Utils.icon("join.png", 100, 100));

        for (JButton button : buttons) {
            button.setText(Utils.center(button.getText()));
            button.setFont(new Font("", Font.PLAIN, 24));
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.TOP);
            button.setIconTextGap(30);
            button.addActionListener(this);
            center.add(button);
        }

        menu = new BotMenu();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == local) {
            replaceWith(new GameScreen(Game.newGame()));
        } else if (e.getSource() == withBot) {
            title.setIcon(Utils.icon("bot-selection.png", 900, 375));

            body.remove(center);
            body.add(menu, BorderLayout.CENTER);
            for (int i = 0; i < menu.levelBtns.length; i++) {
                int closureI = i;
                menu.levelBtns[i].addActionListener(evt -> {
                    replaceWith(new GameScreen(Game.newGame(closureI)));
                });
            }

            revalidate();
            repaint();
        } else if (e.getSource() == startServer) {
            ((JFrame) getRootPane().getParent()).addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ServerThread.sendToAll("exit -1");
                }
            });

            replaceWith(new ServerScreen());
        } else if (e.getSource() == joinServer) {
            replaceWith(new JoinScreen());
        }
    }
}
