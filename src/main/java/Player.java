import javax.swing.*;
import java.awt.*;

public class Player {

    // Attributes
    private final int width, height;
    private final Image image;
    private final ImageIcon rainbow;

    // Variables
    private Rectangle hitbox;
    private int x, y;

    // Constructor
    public Player(Config config) {

        image = config.getUtils().reader(config.getPlayer());
        rainbow = config.getUtils().createImageIcon(config.getRainbow());

        width = image.getWidth(null);
        height = image.getHeight(null);

        updateLocation();
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public ImageIcon getRainbow() {
        return rainbow;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void updateLocation() {
        hitbox = new Rectangle(x, y, width, height);
    }
}
