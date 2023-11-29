package de.MCmoderSD.main;

import de.MCmoderSD.utilities.Calculate;
import de.MCmoderSD.utilities.image.ImageReader;
import de.MCmoderSD.utilities.image.ImageStreamer;
import de.MCmoderSD.utilities.json.JsonNode;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.sound.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class Config {

    // Associations
    private final AudioPlayer audioPlayer;

    // Constants
    private final String[] args;
    private final int width;
    private final int height;
    private final boolean resizable;
    private final Dimension size;

    // Game logic constants
    private final int percentage;
    private final int gap;
    private final float jumpHeight;
    private final float gravity;
    private final float backgroundSpeed;
    private final float obstacleSpeed;
    private final float cloudSpeed;
    private final int maxFPS;

    // Assets
    private final BufferedImage icon;
    private final BufferedImage backgroundImage;
    private final BufferedImage playerImage;
    private final BufferedImage obstacleTopImage;
    private final BufferedImage obstacleBottomImage;
    private final BufferedImage gameOverImage;
    private final BufferedImage pauseImage;
    private final BufferedImage[] cloudImages;

    // Colors
    private final Color playerColor;
    private final Color playerHitboxColor;
    private final Color cloudColor;
    private final Color cloudHitboxColor;
    private final Color obstacleTopColor;
    private final Color obstacleTopHitboxColor;
    private final Color obstacleBottomColor;
    private final Color obstacleBottomHitboxColor;
    private final Color obstacleHitboxColor;
    private final Color backgroundColor;
    private final Color fontColor;
    private final Color scoreColor;

    // Animations
    private final ImageIcon rainbowAnimation;

    // Sounds
    private final String dieSound;
    private final String flapSound;
    private final String hitSound;
    private final String pointSound;
    private final String rainbowSound;
    private final String backgroundMusic;

    // Messages
    private final String language;
    private final String title;
    private final String scorePrefix;

    // Constructor
    public Config(String[] args) {
        this.args = args;

        JsonUtility jsonUtility = new JsonUtility();

        // Language
        if (args.length == 0) language = "en";
        else language = args[0];

        JsonNode config = jsonUtility.load("/config/lena.json");

        width = config.get("width").asInt();
        height = config.get("height").asInt();
        resizable = config.get("resizable").asBoolean();
        size = new Dimension(width, height);

        percentage = config.get("percentage").asInt();
        gap = config.get("gap").asInt();
        jumpHeight = config.get("jumpHeight").asFloat();
        gravity = config.get("gravity").asFloat();
        backgroundSpeed = config.get("backgroundSpeed").asFloat();
        obstacleSpeed = config.get("obstacleSpeed").asFloat();
        cloudSpeed = config.get("cloudSpeed").asFloat();
        maxFPS = config.get("maxFPS").asInt();


        ImageReader imageReader = new ImageReader();

        // Assets
        icon = imageReader.read(config.get("icon").asText());
        backgroundImage = imageReader.read(config.get("backgroundImage").asText());
        playerImage = imageReader.read(config.get("playerImage").asText());
        obstacleTopImage = imageReader.read(config.get("obstacleTopImage").asText());
        obstacleBottomImage = imageReader.read(config.get("obstacleBottomImage").asText());
        gameOverImage = imageReader.read(config.get("gameOverImage").asText());
        pauseImage = imageReader.read(config.get("pauseImage").asText());

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        cloudImages = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            cloudImages[i] = imageReader.read(clouds.get("variant" + i).asText());

        // Animations
        rainbowAnimation = imageReader.readGif(config.get("rainbowAnimation").asText());

        // Colors
        playerColor = Calculate.hexToColor(config.get("playerColor").asText());
        playerHitboxColor = Calculate.hexToColor(config.get("playerHitboxColor").asText());
        cloudColor = Calculate.hexToColor(config.get("cloudColor").asText());
        cloudHitboxColor = Calculate.hexToColor(config.get("cloudHitboxColor").asText());
        obstacleTopColor = Calculate.hexToColor(config.get("obstacleTopColor").asText());
        obstacleTopHitboxColor = Calculate.hexToColor(config.get("obstacleTopHitboxColor").asText());
        obstacleBottomColor = Calculate.hexToColor(config.get("obstacleBottomColor").asText());
        obstacleBottomHitboxColor = Calculate.hexToColor(config.get("obstacleBottomHitboxColor").asText());
        obstacleHitboxColor = Calculate.hexToColor(config.get("obstacleHitboxColor").asText());
        backgroundColor = Calculate.hexToColor(config.get("backgroundColor").asText());
        fontColor = Calculate.hexToColor(config.get("fontColor").asText());
        scoreColor = Calculate.hexToColor(config.get("scoreColor").asText());


        audioPlayer = new AudioPlayer();

        // Sounds
        audioPlayer.loadAudio(dieSound = config.get("dieSound").asText());
        audioPlayer.loadAudio(flapSound = config.get("flapSound").asText());
        audioPlayer.loadAudio(hitSound = config.get("hitSound").asText());
        audioPlayer.loadAudio(pointSound = config.get("pointSound").asText());
        audioPlayer.loadAudio(rainbowSound = config.get("rainbowSound").asText());
        audioPlayer.loadAudio(backgroundMusic = config.get("backgroundMusic").asText());


        JsonNode messages = jsonUtility.load("/languages/" + language + ".json");

        // Messages
        title = messages.get("title").asText();
        scorePrefix = messages.get("scorePrefix").asText();
    }

    // Constructor with URL
    public Config(String[] args, String url) {
        this.args = args;

        JsonUtility jsonUtility = new JsonUtility(url);

        // Language
        if (args.length == 0) language = "en";
        else language = args[0];

        JsonNode config = jsonUtility.load("/config/lena");

        width = config.get("width").asInt();
        height = config.get("height").asInt();
        resizable = config.get("resizable").asBoolean();
        size = new Dimension(width, height);

        percentage = config.get("percentage").asInt();
        gap = config.get("gap").asInt();
        jumpHeight = config.get("jumpHeight").asFloat();
        gravity = config.get("gravity").asFloat();
        backgroundSpeed = config.get("backgroundSpeed").asFloat();
        obstacleSpeed = config.get("obstacleSpeed").asFloat();
        cloudSpeed = config.get("cloudSpeed").asFloat();
        maxFPS = config.get("maxFPS").asInt();


        ImageStreamer imageReader = new ImageStreamer(url);

        // Assets
        icon = imageReader.read(config.get("icon").asText());
        backgroundImage = imageReader.read(config.get("backgroundImage").asText());
        playerImage = imageReader.read(config.get("playerImage").asText());
        obstacleTopImage = imageReader.read(config.get("obstacleTop").asText());
        obstacleBottomImage = imageReader.read(config.get("obstacleBottom").asText());
        gameOverImage = imageReader.read(config.get("gameOverImage").asText());
        pauseImage = imageReader.read(config.get("pauseImage").asText());

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        cloudImages = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            cloudImages[i] = imageReader.read(clouds.get("variant" + i).asText());

        // Animations
        rainbowAnimation = imageReader.readGif(config.get("rainbowAnimation").asText());

        // Colors
        playerColor = Calculate.hexToColor(config.get("playerColor").asText());
        playerHitboxColor = Calculate.hexToColor(config.get("playerHitboxColor").asText());
        cloudColor = Calculate.hexToColor(config.get("cloudColor").asText());
        cloudHitboxColor = Calculate.hexToColor(config.get("cloudHitboxColor").asText());
        obstacleTopColor = Calculate.hexToColor(config.get("obstacleTopColor").asText());
        obstacleTopHitboxColor = Calculate.hexToColor(config.get("obstacleTopHitboxColor").asText());
        obstacleBottomColor = Calculate.hexToColor(config.get("obstacleBottomColor").asText());
        obstacleBottomHitboxColor = Calculate.hexToColor(config.get("obstacleBottomHitboxColor").asText());
        obstacleHitboxColor = Calculate.hexToColor(config.get("obstacleHitboxColor").asText());
        backgroundColor = Calculate.hexToColor(config.get("backgroundColor").asText());
        fontColor = Calculate.hexToColor(config.get("fontColor").asText());
        scoreColor = Calculate.hexToColor(config.get("scoreColor").asText());


        audioPlayer = new AudioPlayer();

        // Sounds
        audioPlayer.loadAudio(dieSound = config.get("dieSound").asText());
        audioPlayer.loadAudio(flapSound = config.get("flapSound").asText());
        audioPlayer.loadAudio(hitSound = config.get("hitSound").asText());
        audioPlayer.loadAudio(pointSound = config.get("pointSound").asText());
        audioPlayer.loadAudio(rainbowSound = config.get("rainbowSound").asText());
        audioPlayer.loadAudio(backgroundMusic = config.get("backgroundMusic").asText());


        JsonNode messages = jsonUtility.load("languages" + language + ".json");

        // Messages
        title = messages.get("title").asText();
        scorePrefix = messages.get("scorePrefix").asText();
    }

    // Association getter
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    // Constants getter
    public String[] getArgs() {
        return args;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResizable() {
        return resizable;
    }

    public Dimension getSize() {
        return size;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getGap() {
        return gap;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public float getGravity() {
        return gravity;
    }

    public float getBackgroundSpeed() {
        return backgroundSpeed;
    }

    public float getObstacleSpeed() {
        return obstacleSpeed;
    }

    public float getCloudSpeed() {
        return cloudSpeed;
    }

    public int getMaxFPS() {
        return maxFPS;
    }

    // Assets getter
    public BufferedImage getIcon() {
        return icon;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public BufferedImage getPlayerImage() {
        return playerImage;
    }

    public BufferedImage getObstacleTopImage() {
        return obstacleTopImage;
    }

    public BufferedImage getObstacleBottomImage() {
        return obstacleBottomImage;
    }

    public BufferedImage getGameOverImage() {
        return gameOverImage;
    }

    public BufferedImage getPauseImage() {
        return pauseImage;
    }

    public BufferedImage[] getCloudImages() {
        return cloudImages;
    }

    // Animations getter
    public ImageIcon getRainbowAnimation() {
        return rainbowAnimation;
    }

    // Colors getter
    public Color getPlayerColor() {
        return playerColor;
    }

    public Color getPlayerHitboxColor() {
        return playerHitboxColor;
    }

    public Color getCloudColor() {
        return cloudColor;
    }

    public Color getCloudHitboxColor() {
        return cloudHitboxColor;
    }

    public Color getObstacleTopColor() {
        return obstacleTopColor;
    }

    public Color getObstacleTopHitboxColor() {
        return obstacleTopHitboxColor;
    }

    public Color getObstacleBottomColor() {
        return obstacleBottomColor;
    }

    public Color getObstacleBottomHitboxColor() {
        return obstacleBottomHitboxColor;
    }

    public Color getObstacleHitboxColor() {
        return obstacleHitboxColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Color getScoreColor() {
        return scoreColor;
    }

    // Sounds getter
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

    public String getBackgroundMusic() {
        return backgroundMusic;
    }

    // Messages getter
    public String getLanguage() {
        return language;
    }

    public String getTitle() {
        return title;
    }

    public String getScorePrefix() {
        return scorePrefix;
    }
}