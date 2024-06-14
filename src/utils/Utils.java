package utils;

import javax.swing.*;
import java.awt.*;

public class Utils {
    // creates an ImageIcon of specified dimensions
    public static ImageIcon icon(String fileName, int width, int height) {
        return new ImageIcon(
            new ImageIcon(Utils.class.getResource("/resources/"  + fileName))
                .getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH)
        );
    }

    public static JPanel spacer(int space) {
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(space, space));
        return spacer;
    }

    public static String html(String text) {
        return "<html>" + text.replaceAll("\n", "<br>") + "</html>";
    }

    public static String center(String text) {
        return "<html><div style='text-align: center'>"
            + text.replaceAll("\n", "<br>") + "</html>";
    }
}
