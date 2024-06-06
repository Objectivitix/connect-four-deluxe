import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuScreen extends Screen implements ActionListener {
    JButton local, withBot;

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

        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());
        add(body, BorderLayout.CENTER);

        // take up space for vertical gap between title and buttons
        JPanel gap = new JPanel();
        gap.setPreferredSize(new Dimension(50, 50));
        body.add(gap, BorderLayout.NORTH);

        // take up space to the left to constrain center
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(200, 200));
        body.add(left, BorderLayout.WEST);

        // take up space to the right to constrain center
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(200, 200));
        body.add(right, BorderLayout.EAST);

        // take up space at the bottom to constrain center
        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(200, 200));
        body.add(bottom, BorderLayout.SOUTH);

        // create grid layout where we will place menu
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1, 4, 35, 0));
        body.add(center, BorderLayout.CENTER);

        local = new JButton("Play locally");
        local.addActionListener(this);
        center.add(local);

        withBot = new JButton("Play with AI");
        withBot.addActionListener(this);
        center.add(withBot);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == local) {
            // initialize game and bot
            Board board = new Board();
            Agent one = new Player(Token.X, board);
            Agent two = new Player(Token.O, board);
            Game game = new Game(one, two, board);

            replaceWith(new GameScreen(game));
        } else if (e.getSource() == withBot) {
            // initialize game and bot
            Board board = new Board();
            Agent one = new Player(Token.X, board);
            Agent two = new Bot(Token.O, board, 7);
            Game game = new Game(one, two, board);

            replaceWith(new GameScreen(game));
        }
    }
}
