package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Obstacle {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final Color color;
    private final Color hitboxColor;
    private final boolean isTop;
    private final float jumpHeight;
    private final float gravity;

    // Variables
    private float x;
    private float y;
    private float speed;
    private float fallSpeed;

    // Constructor
    public Obstacle(Config config, boolean isTop) {
        this.isTop = isTop;

        image = isTop ? config.getObstacleTopImage() : config.getObstacleBottomImage();

        color = isTop ? config.getObstacleTopColor() : config.getObstacleBottomColor();
        hitboxColor = isTop ? config.getObstacleTopHitboxColor() : config.getObstacleBottomHitboxColor();

        width = image.getWidth();
        height = image.getHeight();


        jumpHeight = config.getJumpHeight();
        gravity = config.getGravity();
        speed = config.getObstacleSpeed();
    }

    // Methods
    public void move() {
        x -= speed;
    }

    public void jump() {
        fallSpeed = -jumpHeight;
    }

    public void fall() {
        fallSpeed += gravity;
        y += fallSpeed;
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

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public int getX() {
        return Math.toIntExact(Math.round(x));
    }

    public int getY() {
        return Math.toIntExact(Math.round(y));
    }

    public Point getLocation() {
        return new Point(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)));
    }

    public void setLocation(Point location) {
        this.x = location.x;
        this.y = location.y;
    }

    public Rectangle getHitbox() {
        return new Rectangle(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)), width, height);
    }

    public boolean isTop() {
        return isTop;
    }
}
