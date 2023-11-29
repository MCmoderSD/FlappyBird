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

    // Variables
    private float x;
    private float y;
    private float speed;

    // Constructors
    public Background(Config config, int x, int y) {
        image = config.getBackgroundImage();
        color = config.getBackgroundColor();

        speed = config.getBackgroundSpeed();

        width = image.getWidth();
        height = image.getHeight();

        this.x = x;
        this.y = y;
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
        return new Point((int) x, 0);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
