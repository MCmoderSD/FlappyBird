package de.MCmoderSD.main;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.JavaAudioLibrary.AudioLoader;
import de.MCmoderSD.imageloader.AnimationLoader;
import de.MCmoderSD.imageloader.ImageLoader;
import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.utilities.Calculate;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Config {

    // Constants
    public static String[] ARGS;
    public static String CONFIGURATION;
    public static boolean VALID_CONFIG;
    public static int WIDTH;
    public static int HEIGHT;
    public static boolean SMALL_SCREEN_MODE;
    public static boolean RESIZABLE;
    public static Dimension SIZE;
    public static JsonNode DATABASE;
    public static ArrayList<String> BLOCKED_TERMS;

    // Game logic constants
    public static boolean IS_REVERSE;
    public static int PERCENTAGE;
    public static int GAP;
    public static float JUMP_HEIGHT;
    public static float GRAVITY;
    public static float BACKGROUND_SPEED;
    public static float OBSTACLE_SPEED;
    public static float CLOUD_SPEED;
    public static float RAINBOW_SPAWN_CHANCE;
    public static int RAINBOW_DURATION;
    public static int MAX_FPS;

    // Assets
    public static BufferedImage ICON;
    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage PLAYER_IMAGE;
    public static BufferedImage OBSTACLE_TOP_IMAGE;
    public static BufferedImage OBSTACLE_BOTTOM_IMAGE;
    public static BufferedImage GAME_OVER_IMAGE;
    public static BufferedImage PAUSE_IMAGE;
    public static BufferedImage[] CLOUD_IMAGES;

    // Colors
    public static Color PLAYER_COLOR;
    public static Color PLAYER_HITBOX_COLOR;
    public static Color CLOUD_COLOR;
    public static Color CLOUD_HITBOX_COLOR;
    public static Color OBSTACLE_TOP_COLOR;
    public static Color OBSTACLE_TOP_HITBOX_COLOR;
    public static Color OBSTACLE_BOTTOM_COLOR;
    public static Color OBSTACLE_BOTTOM_HITBOX_COLOR;
    public static Color OBSTACLE_HITBOX_COLOR;
    public static Color SAFE_ZONE_COLOR;
    public static Color SAFE_ZONE_HITBOX_COLOR;
    public static Color BACKGROUND_COLOR;
    public static Color FONT_COLOR;
    public static Color SCORE_COLOR;
    public static Color FPS_COLOR;

    // Animations
    public static ImageIcon RAINBOW_ANIMATION;

    // Sounds
    public static AudioFile DIE_SOUND;
    public static AudioFile FLAP_SOUND;
    public static AudioFile HIT_SOUND;
    public static AudioFile POINT_SOUND;
    public static AudioFile RAINBOW_SOUND;
    public static AudioFile BACKGROUND_MUSIC;

    // Messages
    public static String LANGUAGE;
    public static String TITLE;
    public static String SCORE;
    public static String USERNAME;
    public static String USERNAME_TOOL_TIP;
    public static String RANK;
    public static String SCORE_PREFIX;
    public static String FPS_PREFIX;
    public static String START;
    public static String START_TOOL_TIP;
    public static String SOUND;
    public static String SOUND_TOOL_TIP;
    public static String FPS_TOOL_TIP;
    public static String CHEATS_DETECTED;
    public static String CHEATS_DETECTED_TITLE;
    public static String INSTRUCTION;
    public static String CONFIRM;
    public static String CONFIRM_TOOL_TIP;
    public static String INVALID_USERNAME;
    public static String INVALID_USERNAME_TITLE;

    // Constructor
    public static void init(String[] args) {

        ARGS = args;

        JsonUtility jsonUtility = new JsonUtility();
        ImageLoader imageLoader = new ImageLoader();
        AudioLoader audioLoader = new AudioLoader();


        // Language
        if (args.length == 0) LANGUAGE = "en";
        else {
            String arg = args[0].toLowerCase();
            while (arg.startsWith(" ") || arg.startsWith("-") || arg.startsWith("/")) arg = arg.substring(1);
            while (arg.endsWith(" ") || arg.endsWith("-") || arg.endsWith("/"))
                arg = arg.substring(0, arg.length() - 1);
            LANGUAGE = args[0];
        }

        if (Calculate.checkDate(11, 9) || Calculate.checkDate(9, 11)) CONFIGURATION = "911";
        else if (args.length > 1) {
            String arg = args[1];
            while (arg.startsWith(" ") || arg.startsWith("-") || arg.startsWith("/")) arg = arg.substring(1);
            while (arg.endsWith(" ") || arg.endsWith("-") || arg.endsWith("/"))
                arg = arg.substring(0, arg.length() - 1);
            CONFIGURATION = args[1];
        } else CONFIGURATION = "lena";

        if (args.length > 2) {
            String arg = args[2].toLowerCase();
            while (arg.startsWith(" ") || arg.startsWith("-") || arg.startsWith("/")) arg = arg.substring(1);
            IS_REVERSE = arg.startsWith("r");
        } else IS_REVERSE = false;

        JsonNode config;

        // Check for Valid Config
        for (var i = 0; i < Main.CONFIGURATIONS.length; i++)
            if (Objects.equals(Main.CONFIGURATIONS[i], CONFIGURATION)) {
                VALID_CONFIG = true;
                break;
            }

        // Load Config
        try {

            // Load Config
            if (VALID_CONFIG) config = jsonUtility.load("/config/" + CONFIGURATION + ".json");
            else config = jsonUtility.load(CONFIGURATION, true);

            // Load Database Config
            DATABASE = jsonUtility.load("/config/database.json");

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Screen
        WIDTH = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).width;
        HEIGHT = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).height;
        SMALL_SCREEN_MODE = WIDTH != config.get("width").asInt() || HEIGHT != config.get("height").asInt();

        // Resizable
        RESIZABLE = config.get("resizable").asBoolean();
        SIZE = new Dimension(WIDTH, HEIGHT);


        // Blocked Terms
        BLOCKED_TERMS = new ArrayList<>();

        try {
            String blockedTermsPath = config.get("blockedTermsPath").asText();
            InputStream inputStream;

            // Load Blocked Terms
            if (blockedTermsPath.startsWith("/")) inputStream = Config.class.getResourceAsStream(blockedTermsPath); // Relative path
            else inputStream = Files.newInputStream(Paths.get(blockedTermsPath)); // Absolute path

            // Initialize BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            // Read Blocked Terms
            String line;
            while ((line = reader.readLine()) != null) BLOCKED_TERMS.add(line);

            // Close BufferedReader
            reader.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        PERCENTAGE = config.get("percentage").asInt();
        GAP = config.get("gap").asInt();
        JUMP_HEIGHT = Float.parseFloat(config.get("jumpHeight").asText());
        GRAVITY = Float.parseFloat(config.get("gravity").asText());
        BACKGROUND_SPEED = Float.parseFloat(config.get("backgroundSpeed").asText());
        OBSTACLE_SPEED = Float.parseFloat(config.get("obstacleSpeed").asText());
        CLOUD_SPEED = Float.parseFloat(config.get("cloudSpeed").asText());
        RAINBOW_SPAWN_CHANCE = Float.parseFloat(config.get("rainbowSpawnChance").asText());
        RAINBOW_DURATION = config.get("rainbowDuration").asInt();
        MAX_FPS = config.get("maxFPS").asInt();

        // Assets
        try {

            BufferedImage background = imageLoader.load(config.get("backgroundImage").asText());
            int backgroundWidth = (int) (((double) background.getWidth() / background.getHeight()) * HEIGHT);

            ICON = imageLoader.load(config.get("icon").asText());
            BACKGROUND_IMAGE = scaleImage(background, (backgroundWidth), HEIGHT);
            PLAYER_IMAGE = imageLoader.load(config.get("playerImage").asText());
            OBSTACLE_TOP_IMAGE = imageLoader.load(config.get("obstacleTopImage").asText());
            OBSTACLE_BOTTOM_IMAGE = imageLoader.load(config.get("obstacleBottomImage").asText());
            GAME_OVER_IMAGE = scaleImage(imageLoader.load(config.get("gameOverImage").asText()), Math.min(WIDTH, HEIGHT), Math.min(WIDTH, HEIGHT));
            PAUSE_IMAGE = scaleImage(imageLoader.load(config.get("pauseImage").asText()), Math.min(WIDTH, HEIGHT), Math.min(WIDTH, HEIGHT));

            // Cloud
            JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
            CLOUD_IMAGES = new BufferedImage[clouds.size()];
            for (int i = 0; i < clouds.size(); i++)
                CLOUD_IMAGES[i] = imageLoader.load(clouds.get("variant" + i).asText());

            // Animations
            RAINBOW_ANIMATION = AnimationLoader.loadAnimation(config.get("rainbowAnimation").asText(), false);


        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Colors
        PLAYER_COLOR = getColor(config.get("playerColor").asText());
        PLAYER_HITBOX_COLOR = getColor(config.get("playerHitboxColor").asText());
        CLOUD_COLOR = getColor(config.get("cloudColor").asText());
        CLOUD_HITBOX_COLOR = getColor(config.get("cloudHitboxColor").asText());
        OBSTACLE_TOP_COLOR = getColor(config.get("obstacleTopColor").asText());
        OBSTACLE_TOP_HITBOX_COLOR = getColor(config.get("obstacleTopHitboxColor").asText());
        OBSTACLE_BOTTOM_COLOR = getColor(config.get("obstacleBottomColor").asText());
        OBSTACLE_BOTTOM_HITBOX_COLOR = getColor(config.get("obstacleBottomHitboxColor").asText());
        OBSTACLE_HITBOX_COLOR = getColor(config.get("obstacleHitboxColor").asText());
        SAFE_ZONE_COLOR = getColor(config.get("safeZoneColor").asText());
        SAFE_ZONE_HITBOX_COLOR = getColor(config.get("safeZoneHitboxColor").asText());
        BACKGROUND_COLOR = getColor(config.get("backgroundColor").asText());
        FONT_COLOR = getColor(config.get("fontColor").asText());
        SCORE_COLOR = getColor(config.get("scoreColor").asText());
        FPS_COLOR = getColor(config.get("fpsColor").asText());

        // Sounds
        try {
            DIE_SOUND = audioLoader.load(config.get("dieSound").asText());
            FLAP_SOUND = audioLoader.load(config.get("flapSound").asText());
            HIT_SOUND = audioLoader.load(config.get("hitSound").asText());
            POINT_SOUND = audioLoader.load(config.get("pointSound").asText());
            RAINBOW_SOUND = audioLoader.load(config.get("rainbowSound").asText());
            BACKGROUND_MUSIC = audioLoader.load(config.get("backgroundMusic").asText());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


        JsonNode messages;
        try {
            messages = jsonUtility.load("/languages/" + LANGUAGE + ".json");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Messages
        TITLE = messages.get("title").asText();
        SCORE = messages.get("score").asText();
        USERNAME = messages.get("username").asText();
        USERNAME_TOOL_TIP = messages.get("usernameToolTip").asText();
        RANK = messages.get("rank").asText();
        SCORE_PREFIX = messages.get("scorePrefix").asText();
        FPS_PREFIX = messages.get("fpsPrefix").asText();
        START = messages.get("start").asText();
        START_TOOL_TIP = messages.get("startToolTip").asText();
        SOUND = messages.get("sound").asText();
        SOUND_TOOL_TIP = messages.get("soundToolTip").asText();
        FPS_TOOL_TIP = messages.get("fpsToolTip").asText();
        CHEATS_DETECTED = messages.get("cheatsDetected").asText();
        CHEATS_DETECTED_TITLE = messages.get("cheatsDetectedTitle").asText();
        INSTRUCTION = messages.get("instruction").asText();
        CONFIRM = messages.get("confirm").asText();
        CONFIRM_TOOL_TIP = messages.get("confirmToolTip").asText();
        INVALID_USERNAME = messages.get("invalidUsername").asText();
        INVALID_USERNAME_TITLE = messages.get("invalidUsernameTitle").asText();
    }

    // Helper Methods
    private static BufferedImage scaleImage(BufferedImage image, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, width, height, null);
        return scaledImage;
    }

    private static Color getColor(String hex) {
        return new Color(Integer.parseInt(hex.substring(1), 16));
    }
}