package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.*;

@SuppressWarnings("unused")
public class SafeZone {
    private final int width;
    private final int height;
    private final Color color;
    private final Color hitboxColor;
    private final float gravity;
    private final float speed;
    private final float jumpHeight;

    // Variables
    private float x;
    private float y;
    private float fallSpeed;

    // Modifiers
    private float speedModifier;
    private float jumpHeightModifier;
    private float fallSpeedModifier;
    private float gravityModifier;

    // Constructors
    public SafeZone(Config config, Obstacle top, Obstacle bottom) {

        color = config.getSafeZoneColor();
        hitboxColor = config.getSafeZoneHitboxColor();

        width = top.getWidth() - top.getWidth() / 10;
        height = bottom.getY() - top.getY() - top.getHeight();

        x = (float) (top.getX() + top.getWidth() / 20);
        y = top.getY() + top.getHeight();

        jumpHeight = config.getJumpHeight();
        gravity = config.getGravity();
        speed = config.getObstacleSpeed();

        speedModifier = 1;
        jumpHeightModifier = 1;
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

    public float getJumpHeight() {
        return jumpHeight;
    }

    public float getGravity() {
        return gravity;
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

    public void setFallSpeed(float fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    public void setJumpHeightModifier(float jumpHeightModifier) {
        this.jumpHeightModifier = jumpHeightModifier;
    }

    public void setFallSpeedModifier(float fallSpeedModifier) {
        this.fallSpeedModifier = fallSpeedModifier;
    }

    public void setGravityModifier(float gravityModifier) {
        this.gravityModifier = gravityModifier;
    }
}