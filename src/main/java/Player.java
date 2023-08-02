import java.awt.*;

public class Player {
    private final int width, height;
    private Image image;
    private Point position;
    private Rectangle hitbox;
    private int x, y;
    public Player(Config config) {

        image = config.getUtils().reader(config.getPlayer());

        width = image.getWidth(null);
        height = image.getHeight(null);

        updateLocation();
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;

        position = new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getLocation() {
        return position;
    }

    public void setLocation(Point position) {
        setLocation(position.x, position.y);
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

    public void setImage(Image image) {
        this.image = image;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void updateLocation () {
        hitbox = new Rectangle(x, y, width, height);
    }
}
