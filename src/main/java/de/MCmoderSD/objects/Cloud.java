package de.MCmoderSD.objects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static de.MCmoderSD.main.Config.*;

public class Cloud {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final float speed;

    // Variables
    private float x;
    private float y;

    // Constructors
    public Cloud(int x, int y) {

        image = CLOUD_IMAGES[(int) (Math.random() * CLOUD_IMAGES.length)];

        width = image.getWidth();
        height = image.getHeight();

        speed = CLOUD_SPEED;

        this.x = x;
        this.y = y;
    }

    // Methods
    public void move() {
        x -= speed;
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

    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), width, height);
    }

    public int getX() {
        return Math.round(x);
    }

    public int getY() {
        return Math.round(y);
    }
}