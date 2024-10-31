package de.MCmoderSD.objects;

import java.awt.Rectangle;

import static de.MCmoderSD.main.Config.*;

public class Player {

    // Attributes
    private final int width;
    private final int height;

    private final float gravity;
    private final float jumpHeight;

    // Variables
    private float x;
    private float y;
    private float fallSpeed;

    // Constructor
    public Player() {

        jumpHeight = JUMP_HEIGHT;
        gravity = GRAVITY;

        width = PLAYER_IMAGE.getWidth();
        height = PLAYER_IMAGE.getHeight();

        x = (WIDTH / 4f - width / 2f);
        y = (HEIGHT - height) / 2f;
    }

    // Methods
    public void jump() {
        fallSpeed = -jumpHeight;
    }

    public void fall() {
        fallSpeed += gravity;
        y += fallSpeed;
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