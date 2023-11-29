package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Player {

    // Attributes
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final ImageIcon animation;
    private final Color color;
    private final Color hitboxColor;
    private final float jumpHeight;
    private final float gravity;

    // Variables
    private float x;
    private float y;
    private float fallSpeed;

    // Constructor
    public Player(Config config) {
        image = config.getPlayerImage();
        animation = config.getRainbowAnimation();

        width = image.getWidth();
        height = image.getHeight();

        jumpHeight = config.getJumpHeight();

        color = config.getPlayerColor();
        hitboxColor = config.getPlayerHitboxColor();

        gravity = config.getGravity();


        x = (float) config.getWidth() / 4 - (float) width / 2;
        y = (float) (config.getHeight() - height) / 2;
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

    public BufferedImage getImage() {
        return image;
    }

    public ImageIcon getAnimation() {
        return animation;
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

    public Point getLocation() {
        return new Point((int) x, (int) y);
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    // Setter
    public void setY(int y) {
        this.y = y;
    }
}