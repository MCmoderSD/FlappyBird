import java.awt.*;

public class Obstacle {

    // Attributes
    private final Image image;
    private final int width, height;

    // Variables
    private int x, y;
    private Rectangle hitbox;

    // Constructor
    public Obstacle(Config config, boolean isTop) {
        if (isTop) image = config.getUtils().reader(config.getObstacleTop());
        else image = config.getUtils().reader(config.getObstacleBottom());

        width = image.getWidth(null);
        height = image.getHeight(null);

        updateLocation();
    }

    // Getters and Setters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;

        hitbox = new Rectangle(x, y, width, height);
    }

    public void updateLocation() {
        hitbox = new Rectangle(x, y, width, height);
    }
}