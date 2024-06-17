package utils;

import javax.swing.*;
import java.awt.*;

public class Utils {
    // creates an ImageIcon of specified dimensions, using
    // getResource so program works in all forms (like JAR file)
    public static ImageIcon icon(String fileName, int width, int height) {
        return new ImageIcon(
            new ImageIcon(Utils.class.getResource("/resources/"  + fileName))
                .getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH)
        );
    }

    // creates and "sizes" an empty "square" JPanel
    // for convenient placement in border layouts
    public static JPanel spacer(int space) {
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(space, space));
        return spacer;
    }

    // make any given text center-aligned, for use in a JLabel,
    // by wrapping it in styled html tags
    public static String center(String text) {
        return "<html><div style='text-align: center'>"
            + text.replaceAll("\n", "<br>") + "</div></html>";
    }

    // pop up an alert JOptionPane
    public static void alert(Container parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Nope", JOptionPane.WARNING_MESSAGE);
    }
}
