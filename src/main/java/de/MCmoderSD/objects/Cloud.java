package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Cloud {

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

    // Modifiers
    private float speedModifier;

    // Constructors
    public Cloud(int x, int y) {
        BufferedImage[] cloudImages = Config.CLOUD_IMAGES;

        image = cloudImages[(int) (Math.random() * cloudImages.length)];

        width = image.getWidth();
        height = image.getHeight();

        color = Config.CLOUD_COLOR;
        hitboxColor = Config.CLOUD_HITBOX_COLOR;

        speed = Config.CLOUD_SPEED;

        this.x = x;
        this.y = y;

        speedModifier = 1;
    }

    public Cloud(Point location) {
        BufferedImage[] cloudImages = Config.CLOUD_IMAGES;

        image = cloudImages[(int) (Math.random() * cloudImages.length)];

        width = image.getWidth();
        height = image.getHeight();

        color = Config.CLOUD_COLOR;
        hitboxColor = Config.CLOUD_HITBOX_COLOR;

        speed = Config.CLOUD_SPEED;

        this.x = location.x;
        this.y = location.y;

        speedModifier = 1;
    }

    // Methods
    public void move() {
        x -= speed * speedModifier;
    }

    // Getter
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Dimension getSize() {
        return new Dimension(width, height);
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

    public Point getLocation() {
        return new Point(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)));
    }

    public Rectangle getHitbox() {
        return new Rectangle(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)), width, height);
    }

    public int getX() {
        return Math.toIntExact(Math.round(x));
    }

    public int getY() {
        return Math.toIntExact(Math.round(y));
    }

    public float getSpeed() {
        return speed;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    // Setter
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point location) {
        this.x = location.x;
        this.y = location.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }
}