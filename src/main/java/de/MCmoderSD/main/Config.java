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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class Config {

    // Associations
    private final AudioPlayer audioPlayer;

    // Constants
    private final String[] args;
    private final String configuration;
    private final boolean validConfig;
    private final int width;
    private final int height;
    private final boolean smallScreenMode;
    private final boolean resizable;
    private final Dimension size;
    private final JsonNode database;
    private final ArrayList<String> blockedTerms;

    // Game logic constants
    private final boolean isReverse;
    private final int percentage;
    private final int gap;
    private final float jumpHeight;
    private final float gravity;
    private final float backgroundSpeed;
    private final float obstacleSpeed;
    private final float cloudSpeed;
    private final float rainbowSpawnChance;
    private final int rainbowDuration;
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
    private final Color safeZoneColor;
    private final Color safeZoneHitboxColor;
    private final Color backgroundColor;
    private final Color fontColor;
    private final Color scoreColor;
    private final Color fpsColor;

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
    private final String score;
    private final String username;
    private final String usernameToolTip;
    private final String rank;
    private final String scorePrefix;
    private final String fpsPrefix;
    private final String start;
    private final String startToolTip;
    private final String sound;
    private final String soundToolTip;
    private final String fpsToolTip;
    private final String cheatsDetected;
    private final String cheatsDetectedTitle;
    private final String instruction;
    private final String confirm;
    private final String confirmToolTip;
    private final String invalidUsername;
    private final String invalidUsernameTitle;

    // Constructor
    public Config(String[] args) {
        this.args = args;

        JsonUtility jsonUtility = new JsonUtility();

        // Language
        if (args.length == 0) language = "en";
        else language = args[0];

        if (Calculate.checkDate(11, 9) || Calculate.checkDate(9, 11)) configuration = "911";
        else if (args.length > 1) configuration = args[1];
        else configuration = "lena";

        if (args.length > 2) {
            String arg = args[2].toLowerCase();
            while (arg.startsWith(" ") || arg.startsWith("-") || arg.startsWith("/")) arg = arg.substring(1);
            isReverse = arg.startsWith("r");
        } else isReverse = false;

        JsonNode config;

        // Check for Valid Config
        boolean validConfig = false;
        for (int i = 0; i < Main.CONFIGURATIONS.length; i++)
            if (Objects.equals(Main.CONFIGURATIONS[i], configuration)) {
                validConfig = true;
                break;
            }
        this.validConfig = validConfig;

        // Load Config
        if (validConfig) config = jsonUtility.load("/config/" + configuration + ".json");
        else config = jsonUtility.load(configuration);
        database = jsonUtility.load("/config/database.json");

        width = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).width;
        height = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).height;
        smallScreenMode = width != config.get("width").asInt() || height != config.get("height").asInt();
        resizable = config.get("resizable").asBoolean();
        size = new Dimension(width, height);


        // Blocked Terms
        blockedTerms = new ArrayList<>();

        try {
            String blockedTermsPath = config.get("blockedTermsPath").asText();
            InputStream inputStream;
            if (blockedTermsPath.startsWith("/"))
                inputStream = getClass().getResourceAsStream(blockedTermsPath); // Relative path
            else inputStream = Files.newInputStream(Paths.get(blockedTermsPath)); // Absolute path

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) blockedTerms.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        percentage = config.get("percentage").asInt();
        gap = config.get("gap").asInt();
        jumpHeight = config.get("jumpHeight").asFloat();
        gravity = config.get("gravity").asFloat();
        backgroundSpeed = config.get("backgroundSpeed").asFloat();
        obstacleSpeed = config.get("obstacleSpeed").asFloat();
        cloudSpeed = config.get("cloudSpeed").asFloat();
        rainbowSpawnChance = config.get("rainbowSpawnChance").asFloat();
        rainbowDuration = config.get("rainbowDuration").asInt();
        maxFPS = config.get("maxFPS").asInt();


        ImageReader imageReader = new ImageReader();

        // Assets
        BufferedImage background = imageReader.read(config.get("backgroundImage").asText());
        int backgroundWidth = (int) (((double) background.getWidth() / background.getHeight()) * height);

        icon = imageReader.read(config.get("icon").asText());
        backgroundImage = imageReader.scaleImage(background, (backgroundWidth), height);
        playerImage = imageReader.read(config.get("playerImage").asText());
        obstacleTopImage = imageReader.read(config.get("obstacleTopImage").asText());
        obstacleBottomImage = imageReader.read(config.get("obstacleBottomImage").asText());
        gameOverImage = imageReader.read(config.get("gameOverImage").asText(), Math.min(width, height));
        pauseImage = imageReader.read(config.get("pauseImage").asText(), Math.min(width, height));

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        cloudImages = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            cloudImages[i] = imageReader.read(clouds.get("variant" + i).asText());

        // Animations
        rainbowAnimation = imageReader.readGif(config.get("rainbowAnimation").asText());

        // Colors
        playerColor = config.get("playerColor").asColor();
        playerHitboxColor = config.get("playerHitboxColor").asColor();
        cloudColor = config.get("cloudColor").asColor();
        cloudHitboxColor = config.get("cloudHitboxColor").asColor();
        obstacleTopColor = config.get("obstacleTopColor").asColor();
        obstacleTopHitboxColor = config.get("obstacleTopHitboxColor").asColor();
        obstacleBottomColor = config.get("obstacleBottomColor").asColor();
        obstacleBottomHitboxColor = config.get("obstacleBottomHitboxColor").asColor();
        obstacleHitboxColor = config.get("obstacleHitboxColor").asColor();
        safeZoneColor = config.get("safeZoneColor").asColor();
        safeZoneHitboxColor = config.get("safeZoneHitboxColor").asColor();
        backgroundColor = config.get("backgroundColor").asColor();
        fontColor = config.get("fontColor").asColor();
        scoreColor = config.get("scoreColor").asColor();
        fpsColor = config.get("fpsColor").asColor();


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
        score = messages.get("score").asText();
        username = messages.get("username").asText();
        usernameToolTip = messages.get("usernameToolTip").asText();
        rank = messages.get("rank").asText();
        scorePrefix = messages.get("scorePrefix").asText();
        fpsPrefix = messages.get("fpsPrefix").asText();
        start = messages.get("start").asText();
        startToolTip = messages.get("startToolTip").asText();
        sound = messages.get("sound").asText();
        soundToolTip = messages.get("soundToolTip").asText();
        fpsToolTip = messages.get("fpsToolTip").asText();
        cheatsDetected = messages.get("cheatsDetected").asText();
        cheatsDetectedTitle = messages.get("cheatsDetectedTitle").asText();
        instruction = messages.get("instruction").asText();
        confirm = messages.get("confirm").asText();
        confirmToolTip = messages.get("confirmToolTip").asText();
        invalidUsername = messages.get("invalidUsername").asText();
        invalidUsernameTitle = messages.get("invalidUsernameTitle").asText();
    }

    // Constructor with URL
    public Config(String[] args, String url) {
        this.args = args;

        JsonUtility jsonUtility = new JsonUtility(url);

        // Language
        if (args.length == 0) language = "en";
        else language = args[0];

        if (args.length > 1) configuration = args[1];
        else configuration = "lena";

        if (args.length > 2) {
            String arg = args[2].toLowerCase();
            while (arg.startsWith(" ") || arg.startsWith("-") || arg.startsWith("/")) arg = arg.substring(1);
            isReverse = arg.startsWith("r");
        } else isReverse = false;

        JsonNode config;

        // Check for Valid Config
        boolean validConfig = false;
        for (int i = 0; i < Main.CONFIGURATIONS.length; i++)
            if (Objects.equals(Main.CONFIGURATIONS[i], configuration)) {
                validConfig = true;
                break;
            }
        this.validConfig = validConfig;

        // Load Config
        if (validConfig) config = jsonUtility.load("/config/" + configuration + ".json");
        else config = jsonUtility.load(configuration);
        database = jsonUtility.load("/config/database.json");

        width = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).width;
        height = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).height;
        smallScreenMode = width != config.get("width").asInt() || height != config.get("height").asInt();
        resizable = config.get("resizable").asBoolean();
        size = new Dimension(width, height);


        // Blocked Terms
        blockedTerms = new ArrayList<>();

        try {
            String blockedTermsPath = config.get("blockedTermsPath").asText();
            BufferedReader reader;
            if (blockedTermsPath.startsWith("/"))
                reader = new BufferedReader(new InputStreamReader(new URL(url + blockedTermsPath).openStream())); // Relative path url
            else
                reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Files.newInputStream(Paths.get(blockedTermsPath))))); // Absolute path


            String line;
            while ((line = reader.readLine()) != null) blockedTerms.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        percentage = config.get("percentage").asInt();
        gap = config.get("gap").asInt();
        jumpHeight = config.get("jumpHeight").asFloat();
        gravity = config.get("gravity").asFloat();
        backgroundSpeed = config.get("backgroundSpeed").asFloat();
        obstacleSpeed = config.get("obstacleSpeed").asFloat();
        cloudSpeed = config.get("cloudSpeed").asFloat();
        rainbowSpawnChance = config.get("rainbowSpawnChance").asFloat();
        rainbowDuration = config.get("rainbowDuration").asInt();
        maxFPS = config.get("maxFPS").asInt();


        ImageStreamer imageStreamer = new ImageStreamer(url);

        // Assets
        BufferedImage background = imageStreamer.read(config.get("backgroundImage").asText());
        int backgroundWidth = (int) (((double) background.getWidth() / background.getHeight()) * height);

        icon = imageStreamer.read(config.get("icon").asText());
        backgroundImage = imageStreamer.scaleImage(background, (backgroundWidth), height);
        playerImage = imageStreamer.read(config.get("playerImage").asText());
        obstacleTopImage = imageStreamer.read(config.get("obstacleTopImage").asText());
        obstacleBottomImage = imageStreamer.read(config.get("obstacleBottomImage").asText());
        gameOverImage = imageStreamer.read(config.get("gameOverImage").asText(), Math.min(width, height));
        pauseImage = imageStreamer.read(config.get("pauseImage").asText(), Math.min(width, height));

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        cloudImages = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            cloudImages[i] = imageStreamer.read(clouds.get("variant" + i).asText());

        // Animations
        rainbowAnimation = imageStreamer.readGif(config.get("rainbowAnimation").asText());

        // Colors
        playerColor = config.get("playerColor").asColor();
        playerHitboxColor = config.get("playerHitboxColor").asColor();
        cloudColor = config.get("cloudColor").asColor();
        cloudHitboxColor = config.get("cloudHitboxColor").asColor();
        obstacleTopColor = config.get("obstacleTopColor").asColor();
        obstacleTopHitboxColor = config.get("obstacleTopHitboxColor").asColor();
        obstacleBottomColor = config.get("obstacleBottomColor").asColor();
        obstacleBottomHitboxColor = config.get("obstacleBottomHitboxColor").asColor();
        obstacleHitboxColor = config.get("obstacleHitboxColor").asColor();
        safeZoneColor = config.get("safeZoneColor").asColor();
        safeZoneHitboxColor = config.get("safeZoneHitboxColor").asColor();
        backgroundColor = config.get("backgroundColor").asColor();
        fontColor = config.get("fontColor").asColor();
        scoreColor = config.get("scoreColor").asColor();
        fpsColor = config.get("fpsColor").asColor();

        // Sounds
        dieSound = config.get("dieSound").asText();
        flapSound = config.get("flapSound").asText();
        hitSound = config.get("hitSound").asText();
        pointSound = config.get("pointSound").asText();
        rainbowSound = config.get("rainbowSound").asText();
        backgroundMusic = config.get("backgroundMusic").asText();

        // Load Sounds
        audioPlayer = new AudioPlayer(url);
        audioPlayer.loadAudio(dieSound);
        audioPlayer.loadAudio(flapSound);
        audioPlayer.loadAudio(hitSound);
        audioPlayer.loadAudio(pointSound);
        audioPlayer.loadAudio(rainbowSound);
        audioPlayer.loadAudio(backgroundMusic);

        JsonNode messages = jsonUtility.load("/languages/" + language + ".json");

        // Messages
        title = messages.get("title").asText();
        score = messages.get("score").asText();
        username = messages.get("username").asText();
        usernameToolTip = messages.get("usernameToolTip").asText();
        rank = messages.get("rank").asText();
        scorePrefix = messages.get("scorePrefix").asText();
        fpsPrefix = messages.get("fpsPrefix").asText();
        start = messages.get("start").asText();
        startToolTip = messages.get("startToolTip").asText();
        sound = messages.get("sound").asText();
        soundToolTip = messages.get("soundToolTip").asText();
        fpsToolTip = messages.get("fpsToolTip").asText();
        cheatsDetected = messages.get("cheatsDetected").asText();
        cheatsDetectedTitle = messages.get("cheatsDetectedTitle").asText();
        instruction = messages.get("instruction").asText();
        confirm = messages.get("confirm").asText();
        confirmToolTip = messages.get("confirmToolTip").asText();
        invalidUsername = messages.get("invalidUsername").asText();
        invalidUsernameTitle = messages.get("invalidUsernameTitle").asText();
    }

    // Association getter
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    // Constants getter
    public String[] getArgs() {
        return args;
    }

    public String getConfiguration() {
        return configuration;
    }

    public boolean isValidConfig() {
        return validConfig;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isSmallScreenMode() {
        return smallScreenMode;
    }

    public boolean isResizable() {
        return resizable;
    }

    public Dimension getSize() {
        return size;
    }

    public JsonNode getDatabase() {
        return database;
    }

    public ArrayList<String> getBlockedTerms() {
        return blockedTerms;
    }

    // Game logic constants getter
    public boolean isReverse() {
        return isReverse;
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

    public float getRainbowSpawnChance() {
        return rainbowSpawnChance;
    }

    public int getRainbowDuration() {
        return rainbowDuration;
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

    public Color getSafeZoneColor() {
        return safeZoneColor;
    }

    public Color getSafeZoneHitboxColor() {
        return safeZoneHitboxColor;
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

    public Color getFpsColor() {
        return fpsColor;
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

    public String getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameToolTip() {
        return usernameToolTip;
    }

    public String getRank() {
        return rank;
    }

    public String getScorePrefix() {
        return scorePrefix;
    }

    public String getFpsPrefix() {
        return fpsPrefix;
    }

    public String getStart() {
        return start;
    }

    public String getStartToolTip() {
        return startToolTip;
    }

    public String getSound() {
        return sound;
    }

    public String getSoundToolTip() {
        return soundToolTip;
    }

    public String getFpsToolTip() {
        return fpsToolTip;
    }

    public String getCheatsDetected() {
        return cheatsDetected;
    }

    public String getCheatsDetectedTitle() {
        return cheatsDetectedTitle;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getConfirmToolTip() {
        return confirmToolTip;
    }

    public String getInvalidUsername() {
        return invalidUsername;
    }

    public String getInvalidUsernameTitle() {
        return invalidUsernameTitle;
    }
}