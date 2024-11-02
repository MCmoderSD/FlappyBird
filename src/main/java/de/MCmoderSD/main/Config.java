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
import java.util.ArrayList;
import java.util.Objects;

public class Config {

    // Constants
    public static String[] ARGS;
    public static String CONFIGURATION;
    public static int WIDTH;
    public static int HEIGHT;
    public static boolean SMALL_SCREEN_MODE;
    public static Dimension SIZE;
    public static JsonNode DATABASE;
    public static ArrayList<String> BLOCKED_TERMS;

    // Game logic constants
    public static float GAP_SIZE;
    public static float GAP_PERCENTAGE;
    public static float JUMP_HEIGHT;
    public static float GRAVITY;
    public static float BACKGROUND_SPEED;
    public static float OBSTACLE_SPEED;
    public static float CLOUD_SPEED;
    public static float RAINBOW_SPAWN_CHANCE;

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

        try {

            // Load Config
            JsonNode config = jsonUtility.load("/config/lena.json");

            // Load Database Config
            DATABASE = jsonUtility.load("/config/database.json");

            // Size
            JsonNode settings = config.get("settings");
            WIDTH = Calculate.calculateMaxDimension(settings.get("width").asInt(), settings.get("height").asInt()).width;
            HEIGHT = Calculate.calculateMaxDimension(settings.get("width").asInt(), settings.get("height").asInt()).height;
            SMALL_SCREEN_MODE = WIDTH != settings.get("width").asInt() || HEIGHT != settings.get("height").asInt();
            SIZE = new Dimension(WIDTH, HEIGHT);


            // Blocked Terms
            BLOCKED_TERMS = new ArrayList<>();
            InputStream inputStream = Config.class.getResourceAsStream("/data/blockedTerms");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) BLOCKED_TERMS.add(line);
            bufferedReader.close();
            inputStream.close();

            // Game logic constants
            GAP_SIZE = Float.parseFloat(settings.get("gapSize").asText());
            GAP_PERCENTAGE = Float.parseFloat(settings.get("gapPercentage").asText());
            JUMP_HEIGHT = Float.parseFloat(settings.get("jumpHeight").asText());
            GRAVITY = Float.parseFloat(settings.get("gravity").asText());
            BACKGROUND_SPEED = Float.parseFloat(settings.get("backgroundSpeed").asText());
            OBSTACLE_SPEED = Float.parseFloat(settings.get("obstacleSpeed").asText());
            CLOUD_SPEED = Float.parseFloat(settings.get("cloudSpeed").asText());
            RAINBOW_SPAWN_CHANCE = Float.parseFloat(settings.get("rainbowSpawnChance").asText());

            // Assets
            JsonNode assets = config.get("assets");

            // Images
            JsonNode images = assets.get("images");
            ICON = imageLoader.load(images.get("icon").asText());
            BACKGROUND_IMAGE = imageLoader.load(images.get("background").asText());
            PLAYER_IMAGE = imageLoader.load(images.get("player").asText());
            OBSTACLE_TOP_IMAGE = imageLoader.load(images.get("obstacleTop").asText());
            OBSTACLE_BOTTOM_IMAGE = imageLoader.load(images.get("obstacleBottom").asText());
            GAME_OVER_IMAGE = scaleImage(imageLoader.load(images.get("gameOver").asText()), Math.min(WIDTH, HEIGHT), Math.min(WIDTH, HEIGHT));
            PAUSE_IMAGE = scaleImage(imageLoader.load(images.get("pause").asText()), Math.min(WIDTH, HEIGHT), Math.min(WIDTH, HEIGHT));

            // Cloud
            JsonNode clouds = images.get("clouds");
            CLOUD_IMAGES = new BufferedImage[clouds.size()];
            for (var i = 0; i < clouds.size(); i++) CLOUD_IMAGES[i] = imageLoader.load(clouds.get(Integer.toString(i)).asText());

            // Animations
            RAINBOW_ANIMATION = AnimationLoader.loadAnimation(assets.get("animations").get("rainbow").asText(), false);

            // Colors
            JsonNode colors = assets.get("colors");
            PLAYER_COLOR = getColor(colors.get("player").asText());
            PLAYER_HITBOX_COLOR = getColor(colors.get("playerHitbox").asText());
            CLOUD_COLOR = getColor(colors.get("cloud").asText());
            CLOUD_HITBOX_COLOR = getColor(colors.get("cloudHitbox").asText());
            OBSTACLE_TOP_COLOR = getColor(colors.get("obstacleTop").asText());
            OBSTACLE_TOP_HITBOX_COLOR = getColor(colors.get("obstacleTopHitbox").asText());
            OBSTACLE_BOTTOM_COLOR = getColor(colors.get("obstacleBottom").asText());
            OBSTACLE_BOTTOM_HITBOX_COLOR = getColor(colors.get("obstacleBottomHitbox").asText());
            OBSTACLE_HITBOX_COLOR = getColor(colors.get("obstacleHitbox").asText());
            SAFE_ZONE_COLOR = getColor(colors.get("safeZone").asText());
            SAFE_ZONE_HITBOX_COLOR = getColor(colors.get("safeZoneHitbox").asText());
            BACKGROUND_COLOR = getColor(colors.get("background").asText());
            FONT_COLOR = getColor(colors.get("font").asText());
            SCORE_COLOR = getColor(colors.get("score").asText());
            FPS_COLOR = getColor(colors.get("fps").asText());

            // Sounds
            JsonNode sounds = assets.get("sounds");
            DIE_SOUND = audioLoader.load(sounds.get("die").asText());
            FLAP_SOUND = audioLoader.load(sounds.get("flap").asText());
            HIT_SOUND = audioLoader.load(sounds.get("hit").asText());
            POINT_SOUND = audioLoader.load(sounds.get("point").asText());
            RAINBOW_SOUND = audioLoader.load(sounds.get("rainbow").asText());

            // Music
            BACKGROUND_MUSIC = audioLoader.load(assets.get("music").get("background").asText());

            // Messages
            LANGUAGE = "en";
            JsonNode messages = jsonUtility.load("/languages/" + LANGUAGE + ".json");
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

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Rescale Background Image
        var aspectRatio = (float) BACKGROUND_IMAGE.getWidth() / BACKGROUND_IMAGE.getHeight();
        BACKGROUND_IMAGE = scaleImage(BACKGROUND_IMAGE,  Math.round(WIDTH * aspectRatio), HEIGHT);
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