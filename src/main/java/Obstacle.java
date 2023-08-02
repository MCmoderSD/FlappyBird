import java.awt.*;

public class Obstacle {
    private final Image image;
    private final int width, height;
    private int x, y;
    private Rectangle hitbox;

    public Obstacle(Config config, boolean isTop) {
        if (isTop) image = config.getUtils().reader(config.getObstacleTop());
        else image = config.getUtils().reader(config.getObstacleBottom());

        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;

        hitbox = new Rectangle(x, y, width, height);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void updateLocation() {
        hitbox = new Rectangle(x, y, width, height);
    }
}
