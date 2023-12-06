package de.MCmoderSD.objects;

import de.MCmoderSD.main.Config;

import java.awt.*;

public class SafeZone {
    private final int width;
    private final int height;
    private final Color color;
    private final Color hitboxColor;

    // Variables
    private float x;
    private float y;
    private float speed;

    // Constructors
    public SafeZone(Config config, Obstacle top, Obstacle bottom) {

        color = config.getSafeZoneColor();
        hitboxColor = config.getSafeZoneHitboxColor();

        width = top.getWidth() - top.getWidth() / 10;
        height = bottom.getY() - top.getY() - top.getHeight();

        x = top.getX() + (float) top.getWidth() / 20;
        y = top.getY() + top.getHeight();

        speed = config.getObstacleSpeed();
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

    public Color getColor() {
        return color;
    }

    public Color getHitboxColor() {
        return hitboxColor;
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

    public Rectangle getHitbox() {
        return new Rectangle(Math.toIntExact(Math.round(x)), Math.toIntExact(Math.round(y)), width, height);
    }
}
