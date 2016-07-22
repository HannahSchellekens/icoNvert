package nl.rubenschellekens.iconvert;

import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ruben Schellekens
 */
public class Util {

    public static FileChooser.ExtensionFilter getImageExtensionFilter() {
        return new FileChooser.ExtensionFilter("Images (PNG/JPG/BMP)", "*.png", "*.jpg",
                "*.jpeg", "*.bmp");
    }

    public static FileChooser.ExtensionFilter getIconExtensionFilter() {
        return new FileChooser.ExtensionFilter("Icons (ICO)", "*.ico");
    }

    public static BufferedImage scale(BufferedImage original, int newSize) {
        Image temp = original.getScaledInstance(newSize, newSize, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return image;
    }

    public static BufferedImage crop(BufferedImage original, int newSize) {
        BufferedImage image = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return image;
    }

}
