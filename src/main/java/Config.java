import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Objects;

public class Config {
    // All variables and assets for the game configuration
    private final String title; // Game title
    private final String background; // File path for the background
    private final String player; // File path for the player image
    private final String rainbow; // File path for the rainbow image
    private final String obstacleTop; // File path for the top obstacle
    private final String obstacleBottom; // File path for the bottom obstacle
    private final String icon; // File path for the game icon
    private final String gameOver; // File path for the Game Over image
    private final String pause; // File path for the pause image
    private final String cloud; // File path for the cloud image
    private final String dieSound; // File path for the death sound
    private final String flapSound; // File path for the flap sound
    private final String hitSound; // File path for the hit sound
    private final String pointSound; // File path for the points sound
    private final String rainbowSound; // File path for the rainbow sound
    private final String music; // File path for the background music
    private final String[] args; // Arguments passed when starting the game
    private final int width; // Window width
    private final int height; // Window height
    private final boolean resizable; // Indicates if the window can be resized
    private final boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux"); // Indicates if the OS is Linux
    private final int percentage; // Percentage representing the obstacle size relative to the window height
    private final int gap; // Vertical gap between the obstacles
    private final int jumpHeight; // The player's jump height

    // Class Objects
    private final Utils utils;
    private final UI ui;

    // All variables for the game logic
    private double FPS; // Frames per second (updated frames per second) Maximum: 100
    private int points = -10; // Points
    private boolean sound = true; // Indicates whether sounds should be played

    // Constructor to initialize the variables
    public Config(Utils utils, String defaultConfig, int jumpHeight, int percentage, int gap, int FPS, String[] args) {

        this.utils = utils;
        this.gap = gap;
        this.percentage = percentage;
        this.jumpHeight = jumpHeight;
        this.FPS = FPS;
        this.args = args;

        JsonNode config;

        if (args.length < 1) config = utils.checkDate(defaultConfig);
        else if (args[0].toLowerCase().endsWith(".json")) config = utils.readJson(args[0].toLowerCase());
        else config = utils.readJson(args[0].toLowerCase());

        title = config.get("Title").asText();

        HashMap<Integer, String> nullCheck = new HashMap<>();
        nullCheck.put(0, config.get("Background").asText());
        nullCheck.put(nullCheck.size(), config.get("Player").asText());
        nullCheck.put(nullCheck.size(), config.get("Rainbow").asText());
        nullCheck.put(nullCheck.size(), config.get("ObstacleTop").asText());
        nullCheck.put(nullCheck.size(), config.get("ObstacleBottom").asText());
        nullCheck.put(nullCheck.size(), config.get("Icon").asText());
        nullCheck.put(nullCheck.size(), config.get("GameOver").asText());
        nullCheck.put(nullCheck.size(), config.get("Pause").asText());
        nullCheck.put(nullCheck.size(), config.get("Cloud").asText());
        nullCheck.put(nullCheck.size(), config.get("dieSound").asText());
        nullCheck.put(nullCheck.size(), config.get("flapSound").asText());
        nullCheck.put(nullCheck.size(), config.get("hitSound").asText());
        nullCheck.put(nullCheck.size(), config.get("pointSound").asText());
        nullCheck.put(nullCheck.size(), config.get("rainbowSound").asText());
        nullCheck.put(nullCheck.size(), config.get("backgroundMusic").asText());

        for (int i = 0; i < nullCheck.size(); i++) {
            assert nullCheck.get(i) != null;
            if (Objects.equals(nullCheck.get(i), "") && i < 8) nullCheck.put(i, "error/empty.png");
            else if (Objects.equals(nullCheck.get(i), "") && i > 7) nullCheck.put(i, "error/empty.wav");
        }

        background = nullCheck.get(0);
        player = nullCheck.get(1);
        rainbow = nullCheck.get(2);
        obstacleTop = nullCheck.get(3);
        obstacleBottom = nullCheck.get(4);
        icon = nullCheck.get(5);
        gameOver = nullCheck.get(6);
        pause = nullCheck.get(7);
        cloud = nullCheck.get(8);
        dieSound = nullCheck.get(9);
        flapSound = nullCheck.get(10);
        hitSound = nullCheck.get(11);
        pointSound = nullCheck.get(12);
        rainbowSound = nullCheck.get(13);
        music = nullCheck.get(14);

        int[] dimension = utils.maxDimension(config.get("WindowSizeX").asInt(), config.get("WindowSizeY").asInt());
        width = dimension[0];
        height = dimension[1];

        resizable = config.get("Resizable").asBoolean();

        // Start the game logic with the specified parameters
        ui = new UI(this, utils);
    }

    // Getters and setters
    public int getJumpHeight() {
        return jumpHeight;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getGap() {
        return gap;
    }

    public double getFPS() {
        return FPS;
    }

    public void setFPS(double FPS) {
        this.FPS = FPS;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public int getWindowSizeX() {
        return width;
    }

    public int getWindowSizeY() {
        return height;
    }

    public boolean isResizable() {
        return resizable;
    }

    public String getBackground() {
        return background;
    }

    public String getPlayer() {
        return player;
    }

    public String getRainbow() {
        return rainbow;
    }

    public String getObstacleTop() {
        return obstacleTop;
    }

    public String getObstacleBottom() {
        return obstacleBottom;
    }

    public String getIcon() {
        return icon;
    }

    public String getGameOver() {
        return gameOver;
    }

    public String getPause() {
        return pause;
    }

    public String getCloud() {
        return cloud;
    }

    public String getDieSound() {
        return dieSound;
    }

    public String getFlapSound() {
        return flapSound;
    }

    public String getHitSound() {
        return hitSound;
    }

    public String getPointSound() {
        return pointSound;
    }

    public String getRainbowSound() {
        return rainbowSound;
    }

    public String getMusic() {
        return music;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public Utils getUtils() {
        return utils;
    }

    public UI getUi() {
        return ui;
    }

    public boolean isLinux() {
        return isLinux;
    }
}