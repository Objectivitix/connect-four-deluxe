package utils;

import javax.swing.*;
import java.awt.*;

public class Utils {
    // creates an ImageIcon of specified dimensions
    public static ImageIcon icon(String fileName, int width, int height) {
        return new ImageIcon(
            new ImageIcon("src/resources/" + fileName)
                .getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH)
        );
    }
}
