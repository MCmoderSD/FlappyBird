package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Player {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final ImageIcon animation;
    private final Color color;
    private final Color hitboxColor;

    // Variables
    private final int x;
    private int y;

    // Constructor
    public Player(Config config) {
        image = config.getPlayerImage();
        animation = config.getRainbowAnimation();

        width = image.getWidth();
        height = image.getHeight();

        color = config.getPlayerColor();
        hitboxColor = config.getPlayerHitboxColor();

        x = config.getWidth() / 4 - width / 2;
        y = (config.getHeight() - height) / 2;
    }

    // Getter
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public ImageIcon getAnimation() {
        return animation;
    }

    public Color getColor() {
        return color;
    }

    public Color getHitboxColor() {
        return hitboxColor;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Setter
    public void setY(int y) {
        this.y = y;
    }
}