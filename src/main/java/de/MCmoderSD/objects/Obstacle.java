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
    private final float gravity;
    private final float speed;
    private final float jumpHeight;

    // Variables
    private float x;
    private float y;
    private float fallSpeed;

    // Modifiers
    private float jumpHeightModifier;
    private float speedModifier;
    private float fallSpeedModifier;
    private float gravityModifier;

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

        jumpHeightModifier = 1;
        speedModifier = 1;
        fallSpeedModifier = 1;
        gravityModifier = 1;
    }

    // Methods
    public void move() {
        x -= speed * speedModifier;
    }

    public void jump() {
        fallSpeed = -(jumpHeight * jumpHeightModifier);
    }

    public void fall() {
        fallSpeed += gravity * gravityModifier;
        y += fallSpeed * fallSpeedModifier;
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

    public float getJumpHeight() {
        return jumpHeight;
    }

    public float getGravity() {
        return gravity;
    }

    public float getSpeed() {
        return speed;
    }

    public float getFallSpeed() {
        return fallSpeed;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public float getJumpHeightModifier() {
        return jumpHeightModifier;
    }

    public float getFallSpeedModifier() {
        return fallSpeedModifier;
    }

    public float getGravityModifier() {
        return gravityModifier;
    }

    public boolean isTop() {
        return isTop;
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    public void setJumpHeightModifier(float jumpHeightModifier) {
        this.jumpHeightModifier = jumpHeightModifier;
    }

    public void setFallSpeed(float fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public void setFallSpeedModifier(float fallSpeedModifier) {
        this.fallSpeedModifier = fallSpeedModifier;
    }

    public void setGravityModifier(float gravityModifier) {
        this.gravityModifier = gravityModifier;
    }
}