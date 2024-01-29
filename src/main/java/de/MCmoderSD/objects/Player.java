package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
    private final float gravity;
    private final float jumpHeight;

    // Variables
    private float x;
    private float y;
    private float fallSpeed;

    // Modifiers
    private float jumpHeightModifier;
    private float fallSpeedModifier;
    private float gravityModifier;

    // Constructor
    public Player() {
        image = Config.PLAYER_IMAGE;
        animation = Config.RAINBOW_ANIMATION;

        width = image.getWidth();
        height = image.getHeight();

        color = Config.PLAYER_COLOR;
        hitboxColor = Config.PLAYER_HITBOX_COLOR;

        jumpHeight = Config.JUMP_HEIGHT;
        gravity = Config.GRAVITY;


        x = (float) (Config.WIDTH / 4 - width / 2);
        y = (float) (Config.HEIGHT - height) / 2;

        jumpHeightModifier = 1;
        fallSpeedModifier = 1;
        gravityModifier = 1;
    }

    // Methods
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

    public ImageIcon getAnimation() {
        return animation;
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

    public float getFallSpeed() {
        return fallSpeed;
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