package de.MCmoderSD.main;

import de.MCmoderSD.utilities.Calculate;
import de.MCmoderSD.utilities.image.ImageReader;
import de.MCmoderSD.utilities.image.ImageStreamer;
import de.MCmoderSD.utilities.json.JsonNode;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.sound.AudioPlayer;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Config {

    // Utility
    private final AudioPlayer audioPlayer;

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
    public static String DIE_SOUND;
    public static String FLAP_SOUND;
    public static String HIT_SOUND;
    public static String POINT_SOUND;
    public static String RAINBOW_SOUND;
    public static String BACKGROUND_MUSIC;

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
    public Config(String[] args) {
        ARGS = args;

        JsonUtility jsonUtility = new JsonUtility();

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

        for (int i = 0; i < Main.CONFIGURATIONS.length; i++)
            if (Objects.equals(Main.CONFIGURATIONS[i], CONFIGURATION)) {
                VALID_CONFIG = true;
                break;
            }

        // Load Config
        if (VALID_CONFIG) config = jsonUtility.load("/config/" + CONFIGURATION + ".json");
        else config = jsonUtility.load(CONFIGURATION, true);
        DATABASE = jsonUtility.load("/config/database.json");

        WIDTH = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).width;
        HEIGHT = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).height;
        SMALL_SCREEN_MODE = WIDTH != config.get("width").asInt() || HEIGHT != config.get("height").asInt();

        RESIZABLE = config.get("resizable").asBoolean();
        SIZE = new Dimension(WIDTH, HEIGHT);


        // Blocked Terms
        BLOCKED_TERMS = new ArrayList<>();

        try {
            String blockedTermsPath = config.get("blockedTermsPath").asText();
            InputStream inputStream;
            if (blockedTermsPath.startsWith("/"))
                inputStream = getClass().getResourceAsStream(blockedTermsPath); // Relative path
            else inputStream = Files.newInputStream(Paths.get(blockedTermsPath)); // Absolute path

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) BLOCKED_TERMS.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        PERCENTAGE = config.get("percentage").asInt();
        GAP = config.get("gap").asInt();
        JUMP_HEIGHT = config.get("jumpHeight").asFloat();
        GRAVITY = config.get("gravity").asFloat();
        BACKGROUND_SPEED = config.get("backgroundSpeed").asFloat();
        OBSTACLE_SPEED = config.get("obstacleSpeed").asFloat();
        CLOUD_SPEED = config.get("cloudSpeed").asFloat();
        RAINBOW_SPAWN_CHANCE = config.get("rainbowSpawnChance").asFloat();
        RAINBOW_DURATION = config.get("rainbowDuration").asInt();
        MAX_FPS = config.get("maxFPS").asInt();


        ImageReader imageReader = new ImageReader(!VALID_CONFIG);

        // Assets
        BufferedImage background = imageReader.read(config.get("backgroundImage").asText());
        int backgroundWidth = (int) (((double) background.getWidth() / background.getHeight()) * HEIGHT);

        ICON = imageReader.read(config.get("icon").asText());
        BACKGROUND_IMAGE = imageReader.scaleImage(background, (backgroundWidth), HEIGHT);
        PLAYER_IMAGE = imageReader.read(config.get("playerImage").asText());
        OBSTACLE_TOP_IMAGE = imageReader.read(config.get("obstacleTopImage").asText());
        OBSTACLE_BOTTOM_IMAGE = imageReader.read(config.get("obstacleBottomImage").asText());
        GAME_OVER_IMAGE = imageReader.read(config.get("gameOverImage").asText(), Math.min(WIDTH, HEIGHT));
        PAUSE_IMAGE = imageReader.read(config.get("pauseImage").asText(), Math.min(WIDTH, HEIGHT));

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        CLOUD_IMAGES = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            CLOUD_IMAGES[i] = imageReader.read(clouds.get("variant" + i).asText());

        // Animations
        RAINBOW_ANIMATION = imageReader.readGif(config.get("rainbowAnimation").asText());

        // Colors
        PLAYER_COLOR = config.get("playerColor").asColor();
        PLAYER_HITBOX_COLOR = config.get("playerHitboxColor").asColor();
        CLOUD_COLOR = config.get("cloudColor").asColor();
        CLOUD_HITBOX_COLOR = config.get("cloudHitboxColor").asColor();
        OBSTACLE_TOP_COLOR = config.get("obstacleTopColor").asColor();
        OBSTACLE_TOP_HITBOX_COLOR = config.get("obstacleTopHitboxColor").asColor();
        OBSTACLE_BOTTOM_COLOR = config.get("obstacleBottomColor").asColor();
        OBSTACLE_BOTTOM_HITBOX_COLOR = config.get("obstacleBottomHitboxColor").asColor();
        OBSTACLE_HITBOX_COLOR = config.get("obstacleHitboxColor").asColor();
        SAFE_ZONE_COLOR = config.get("safeZoneColor").asColor();
        SAFE_ZONE_HITBOX_COLOR = config.get("safeZoneHitboxColor").asColor();
        BACKGROUND_COLOR = config.get("backgroundColor").asColor();
        FONT_COLOR = config.get("fontColor").asColor();
        SCORE_COLOR = config.get("scoreColor").asColor();
        FPS_COLOR = config.get("fpsColor").asColor();


        audioPlayer = new AudioPlayer();

        // Sounds
        audioPlayer.loadAudio(DIE_SOUND = config.get("dieSound").asText());
        audioPlayer.loadAudio(FLAP_SOUND = config.get("flapSound").asText());
        audioPlayer.loadAudio(HIT_SOUND = config.get("hitSound").asText());
        audioPlayer.loadAudio(POINT_SOUND = config.get("pointSound").asText());
        audioPlayer.loadAudio(RAINBOW_SOUND = config.get("rainbowSound").asText());
        audioPlayer.loadAudio(BACKGROUND_MUSIC = config.get("backgroundMusic").asText());


        JsonNode messages = jsonUtility.load("/languages/" + LANGUAGE + ".json");

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

    // Constructor with URL
    public Config(String[] args, String url) {
        ARGS = args;

        JsonUtility jsonUtility = new JsonUtility(url);

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

        for (int i = 0; i < Main.CONFIGURATIONS.length; i++)
            if (Objects.equals(Main.CONFIGURATIONS[i], CONFIGURATION)) {
                VALID_CONFIG = true;
                break;
            }

        // Load Config
        if (VALID_CONFIG) config = jsonUtility.load("/config/" + CONFIGURATION + ".json");
        else config = jsonUtility.load(CONFIGURATION, true);
        DATABASE = jsonUtility.load("/config/database.json");

        WIDTH = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).width;
        HEIGHT = Calculate.calculateMaxDimension(config.get("width").asInt(), config.get("height").asInt()).height;
        SMALL_SCREEN_MODE = WIDTH != config.get("width").asInt() || HEIGHT != config.get("height").asInt();

        RESIZABLE = config.get("resizable").asBoolean();
        SIZE = new Dimension(WIDTH, HEIGHT);


        // Blocked Terms
        BLOCKED_TERMS = new ArrayList<>();

        try {
            String blockedTermsPath = config.get("blockedTermsPath").asText();
            InputStream inputStream;
            if (blockedTermsPath.startsWith("/"))
                inputStream = getClass().getResourceAsStream(blockedTermsPath); // Relative path
            else inputStream = Files.newInputStream(Paths.get(blockedTermsPath)); // Absolute path

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) BLOCKED_TERMS.add(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        PERCENTAGE = config.get("percentage").asInt();
        GAP = config.get("gap").asInt();
        JUMP_HEIGHT = config.get("jumpHeight").asFloat();
        GRAVITY = config.get("gravity").asFloat();
        BACKGROUND_SPEED = config.get("backgroundSpeed").asFloat();
        OBSTACLE_SPEED = config.get("obstacleSpeed").asFloat();
        CLOUD_SPEED = config.get("cloudSpeed").asFloat();
        RAINBOW_SPAWN_CHANCE = config.get("rainbowSpawnChance").asFloat();
        RAINBOW_DURATION = config.get("rainbowDuration").asInt();
        MAX_FPS = config.get("maxFPS").asInt();


        ImageStreamer imageStreamer = new ImageStreamer(url);

        // Assets
        BufferedImage background = imageStreamer.read(config.get("backgroundImage").asText());
        int backgroundWidth = (int) (((double) background.getWidth() / background.getHeight()) * HEIGHT);

        ICON = imageStreamer.read(config.get("icon").asText());
        BACKGROUND_IMAGE = imageStreamer.scaleImage(background, (backgroundWidth), HEIGHT);
        PLAYER_IMAGE = imageStreamer.read(config.get("playerImage").asText());
        OBSTACLE_TOP_IMAGE = imageStreamer.read(config.get("obstacleTopImage").asText());
        OBSTACLE_BOTTOM_IMAGE = imageStreamer.read(config.get("obstacleBottomImage").asText());
        GAME_OVER_IMAGE = imageStreamer.read(config.get("gameOverImage").asText(), Math.min(WIDTH, HEIGHT));
        PAUSE_IMAGE = imageStreamer.read(config.get("pauseImage").asText(), Math.min(WIDTH, HEIGHT));

        // Cloud
        JsonNode clouds = jsonUtility.load(config.get("clouds").asText());
        CLOUD_IMAGES = new BufferedImage[clouds.getSize()];
        for (int i = 0; i < clouds.getSize(); i++)
            CLOUD_IMAGES[i] = imageStreamer.read(clouds.get("variant" + i).asText());

        // Animations
        RAINBOW_ANIMATION = imageStreamer.readGif(config.get("rainbowAnimation").asText());

        // Colors
        PLAYER_COLOR = config.get("playerColor").asColor();
        PLAYER_HITBOX_COLOR = config.get("playerHitboxColor").asColor();
        CLOUD_COLOR = config.get("cloudColor").asColor();
        CLOUD_HITBOX_COLOR = config.get("cloudHitboxColor").asColor();
        OBSTACLE_TOP_COLOR = config.get("obstacleTopColor").asColor();
        OBSTACLE_TOP_HITBOX_COLOR = config.get("obstacleTopHitboxColor").asColor();
        OBSTACLE_BOTTOM_COLOR = config.get("obstacleBottomColor").asColor();
        OBSTACLE_BOTTOM_HITBOX_COLOR = config.get("obstacleBottomHitboxColor").asColor();
        OBSTACLE_HITBOX_COLOR = config.get("obstacleHitboxColor").asColor();
        SAFE_ZONE_COLOR = config.get("safeZoneColor").asColor();
        SAFE_ZONE_HITBOX_COLOR = config.get("safeZoneHitboxColor").asColor();
        BACKGROUND_COLOR = config.get("backgroundColor").asColor();
        FONT_COLOR = config.get("fontColor").asColor();
        SCORE_COLOR = config.get("scoreColor").asColor();
        FPS_COLOR = config.get("fpsColor").asColor();


        audioPlayer = new AudioPlayer(url);

        // Sounds
        audioPlayer.loadAudio(DIE_SOUND = config.get("dieSound").asText());
        audioPlayer.loadAudio(FLAP_SOUND = config.get("flapSound").asText());
        audioPlayer.loadAudio(HIT_SOUND = config.get("hitSound").asText());
        audioPlayer.loadAudio(POINT_SOUND = config.get("pointSound").asText());
        audioPlayer.loadAudio(RAINBOW_SOUND = config.get("rainbowSound").asText());
        audioPlayer.loadAudio(BACKGROUND_MUSIC = config.get("backgroundMusic").asText());


        JsonNode messages = jsonUtility.load("/languages/" + LANGUAGE + ".json");

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

    // Getter
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}