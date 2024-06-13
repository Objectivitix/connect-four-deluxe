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

    public MenuScreen() {
        // initialize with border layout
        super();
        setLayout(new BorderLayout());

        // create top half
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setPreferredSize(new Dimension(250, 250));
        add(top, BorderLayout.NORTH);

        // this is so we can place title in bottom half of top half
        JPanel header = new JPanel();
        top.add(header, BorderLayout.SOUTH);

        // create centered title with cool font
        JLabel title = new JLabel("Welcome to Connect 4 Deluxe!");
        title.setFont(new Font("Rasa", Font.BOLD, 36));
        title.setHorizontalAlignment(JLabel.CENTER);
        header.add(title);

        body = new JPanel();
        body.setLayout(new BorderLayout());
        add(body, BorderLayout.CENTER);

        // take up space for vertical gap between title and buttons
        JPanel gap = new JPanel();
        gap.setPreferredSize(new Dimension(50, 50));
        body.add(gap, BorderLayout.NORTH);

        // take up space to the left to constrain center
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(150, 150));
        body.add(left, BorderLayout.WEST);

        // take up space to the right to constrain center
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(150, 150));
        body.add(right, BorderLayout.EAST);

        // take up space at the bottom to constrain center
        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(200, 200));
        body.add(bottom, BorderLayout.SOUTH);

        // create grid layout where we will place menu
        center = new JPanel();
        center.setLayout(new GridLayout(1, 4, 35, 0));
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
            button.setText("<html><div style='text-align: center;'>"
                + button.getText() + "</div></html>");
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
            new App(false);
        } else if (e.getSource() == joinServer) {
            replaceWith(new JoinScreen());
        }
    }
}
