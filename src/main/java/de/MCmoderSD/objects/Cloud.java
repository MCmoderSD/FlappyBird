package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Cloud {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final Color color;
    private final Color hitboxColor;

    // Variables
    private float x;
    private float y;
    private float speed;

    // Constructors
    public Cloud(Config config, int x, int y) {
        BufferedImage[] cloudImages = config.getCloudImages();

        image = cloudImages[(int) (Math.random() * cloudImages.length)];

        width = image.getWidth();
        height = image.getHeight();

        color = config.getCloudColor();
        hitboxColor = config.getCloudHitboxColor();

        speed = config.getCloudSpeed();

        this.x = x;
        this.y = y;
    }

    public Cloud(Config config, Point location) {
        BufferedImage[] cloudImages = config.getCloudImages();

        image = cloudImages[(int) (Math.random() * cloudImages.length)];

        width = image.getWidth();
        height = image.getHeight();

        color = config.getCloudColor();
        hitboxColor = config.getCloudHitboxColor();

        speed = config.getCloudSpeed();

        this.x = location.x;
        this.y = location.y;
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

    public Color getHitboxColor() {
        return hitboxColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getLocation() {
        return new Point(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)));
    }

    public int getX() {
        return Math.toIntExact(Math.round(x));
    }

    public int getY() {
        return Math.toIntExact(Math.round(y));
    }

    public Rectangle getHitbox() {
        return new Rectangle(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)), width, height);
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }
}
