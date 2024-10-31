package de.MCmoderSD.objects;

import java.awt.Rectangle;

import static de.MCmoderSD.main.Config.*;

public class Background {

    // Attributes
    private final int width;
    private final int height;

    // Variables
    private float x;
    private float y;

    // Constructors
    public Background(int x, int y) {

        // Initialize Attributes
        width = BACKGROUND_IMAGE.getWidth();
        height = BACKGROUND_IMAGE.getHeight();

        // Set Variables
        this.x = x;
        this.y = y;
    }

    // Methods
    public void move() {
        x -= BACKGROUND_SPEED;
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return Math.round(x);
    }

    public int getY() {
        return Math.round(y);
    }

    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), width, height);
    }
}