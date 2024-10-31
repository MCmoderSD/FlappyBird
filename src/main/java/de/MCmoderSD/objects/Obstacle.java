package de.MCmoderSD.objects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static de.MCmoderSD.main.Config.*;

public class Obstacle {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final Color color;
    private final Color hitboxColor;
    private final float speed;

    // Variables
    private float x;
    private float y;

    // Constructor
    public Obstacle(boolean isTop) {

        image = isTop ? OBSTACLE_TOP_IMAGE : OBSTACLE_BOTTOM_IMAGE;

        color = isTop ? OBSTACLE_TOP_COLOR : OBSTACLE_BOTTOM_COLOR;
        hitboxColor = isTop ? OBSTACLE_TOP_HITBOX_COLOR : OBSTACLE_BOTTOM_HITBOX_COLOR;

        width = image.getWidth();
        height = image.getHeight();

        speed = OBSTACLE_SPEED;
    }

    // Methods
    public void move() {
        x -= speed;
    }

    // Setter
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
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

    public Color getColor() {
        return color;
    }

    public Color getHitboxColor() {
        return hitboxColor;
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