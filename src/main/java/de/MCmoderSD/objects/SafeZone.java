package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class SafeZone {

    private final int width;
    private final int height;
    private final float gravity;
    private final float speed;
    private final float jumpHeight;

    // Variables
    private float x;
    private float y;

    // Constructors
    public SafeZone(Obstacle top, Obstacle bottom) {

        width = top.getWidth() - top.getWidth() / 10;
        height = bottom.getY() - top.getY() - top.getHeight();

        x = (float) (top.getX() + top.getWidth() / 20);
        y = top.getY() + top.getHeight();

        jumpHeight = Config.JUMP_HEIGHT;
        gravity = Config.GRAVITY;
        speed = Config.OBSTACLE_SPEED;
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

    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), width, height);
    }

    public int getX() {
        return Math.round(x);
    }

    public int getY() {
        return Math.round(y);
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public float getGravity() {
        return gravity;
    }
}