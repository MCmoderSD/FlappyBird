package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Background {

    // Attributes
    private final BufferedImage image;
    private final Color color;
    private final int width;
    private final int height;
    private final int speed;

    // Variables
    private int x;
    private int y;

    // Constructors
    public Background(Config config, int x, int y) {
        image = config.getBackgroundImage();
        color = config.getBackgroundColor();

        speed = config.getBackgroundSpeed();

        width = image.getWidth();
        height = image.getHeight();
    }

    public Background(Config config, Point location) {
        image = config.getBackgroundImage();
        color = config.getBackgroundColor();

        speed = config.getBackgroundSpeed();

        width = image.getWidth();
        height = image.getHeight();

        x = location.x;
        y = location.y;
    }

    // Methods
    public void move() {
        x -= speed;
    }

    // Getter
    public BufferedImage getImage() {
        return image;
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getLocation() {
        return new Point(x, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }
}
