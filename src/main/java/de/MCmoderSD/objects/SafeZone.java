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

        color = /* ToDo config.getSafeZoneColor(); */ Color.GREEN;
        hitboxColor = /* ToDo config.getSafeZoneColor(); */ Color.GREEN;

        width = top.getWidth();
        height = bottom.getY() - top.getY() - top.getHeight();

        x = top.getX();
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

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public Point getLocation() {
        return new Point((int) x, (int) y);
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
