import com.fasterxml.jackson.databind.JsonNode;

import java.awt.*;

public class Cloud {

    // Attributes
    private final Image image;
    private final int width, height;

    // Variables
    private int x, y;

    // Constructor
    public Cloud(Config config) {

        JsonNode cloud = config.getUtils().readJson(config.getCloud());
        assert cloud != null;

        image = config.getUtils().readImage(cloud.get("variant" + (int) (cloud.size() * Math.random())).asText());

        width = image.getWidth(null);
        height = image.getHeight(null);
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

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}