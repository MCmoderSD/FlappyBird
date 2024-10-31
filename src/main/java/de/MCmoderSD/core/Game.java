package de.MCmoderSD.core;

import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.executor.NanoLoop;

import de.MCmoderSD.objects.Background;
import de.MCmoderSD.objects.Cloud;
import de.MCmoderSD.objects.Obstacle;
import de.MCmoderSD.objects.Player;
import de.MCmoderSD.objects.SafeZone;
import de.MCmoderSD.utilities.Calculate;

import java.util.ArrayList;

import static de.MCmoderSD.main.Config.*;

public class Game {

    // Associations
    private final Frame frame;

    // Game Threads
    private final NanoLoop tickExecutor;
    private final NanoLoop renderExecutor;
    private final NanoLoop debugExecutor;

    // Debug Variables
    private int fps;
    private int tps;

    // Constants
    private final int obstacleSpawnRate;
    private final boolean isReverse;
    private boolean sound;

    // Attributes
    private Player player;
    private ArrayList<Background> backgrounds;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<SafeZone> safeZones;
    private ArrayList<Cloud> clouds;

    // Variables
    private boolean isPaused;
    private boolean hasCollided;
    private boolean gameOver;
    private boolean showFps;
    private boolean hitboxes;
    private boolean debug;
    private boolean cheatsActive;
    private boolean isJump;
    private boolean isRainbow;
    private int score;
    private int obstacleSpawnTimer;

    // Constructor
    public Game(Frame frame) {
        this.frame = frame;

        isReverse = IS_REVERSE;

        sound = true;

        // Constants
        obstacleSpawnRate = (int) (200 / OBSTACLE_SPEED);
        init(0);

        // Game Threads
        tickExecutor = new NanoLoop(this::tick, 360);
        renderExecutor = new NanoLoop(this::render, 480);
        debugExecutor = new NanoLoop(this::debug, 1);

        // Start Threads
        tickExecutor.start();
        renderExecutor.start();
        debugExecutor.start();
    }

    public void debug() {

        // Print Debug Information
        System.out.println("FPS: " + fps);
        System.out.println("TPS: " + tps);

        // Reset Variables
        fps = 0;
        tps = 0;
    }

    public void tick() {

        // Check for Pause
        if (isPaused) return;

        // Check for Restart
        if (gameOver && isJump && frame.getGameUI().isVisible()) restart();

        // Check for fall
        if (!gameOver && !cheatsActive && player.getY() - player.getHeight() >= HEIGHT) fall();

        // Remove Out of Bounds
        removeOutOfBounds();

        // Check for Collision
        if (!hasCollided && !gameOver && !isRainbow && !cheatsActive) for (Obstacle obstacle : obstacles) if (player.getHitbox().intersects(obstacle.getHitbox())) collision();




        // Check for Safe Zone
        SafeZone collisionSafeZone = null;
        if (!gameOver) for (SafeZone safeZone : safeZones) if (player.getHitbox().intersects(safeZone.getHitbox())) {
            collisionSafeZone = safeZone;
            point();
            break;
        }

        // Remove Safe Zone
        if (collisionSafeZone != null) safeZones.remove(collisionSafeZone);



        // Background Spawn
        Background lastBackground = backgrounds.getLast();
        if (lastBackground.getX() + lastBackground.getWidth() <= WIDTH) backgrounds.add(new Background(WIDTH, 0));

        // Cloud Spawn
        // ToDo Cloud Spawn

        // Obstacle Spawn
        if (obstacleSpawnTimer >= obstacleSpawnRate) spawnObstacles();
        else obstacleSpawnTimer++;

        // Player Movement
        if (!gameOver && !hasCollided && isJump && player.getY() + player.getHeight() > 0) player.jump();

        if (!isReverse || hasCollided) player.fall();

        if (!(gameOver || hasCollided)) {

            // Move Clouds
            for (Cloud cloud : clouds) cloud.move();

            // Move Backgrounds
            for (Background background : backgrounds) background.move();

            // Move Obstacles
            for (Obstacle obstacle : obstacles) obstacle.move();

            // Move Safe Zones
            for (SafeZone safeZone : safeZones) safeZone.move();
        }

        isJump = false;

        // Debug
        tps++;
    }

    private void spawnClouds() {
        // ToDo Cloud Spawn
    }

    private void spawnObstacles() {

        // Create Obstacles
        Obstacle obstacleTop = new Obstacle(true);
        Obstacle obstacleBottom = new Obstacle(false);

        // Calculate the minimum and maximum Y value
        var minY = ((HEIGHT * PERCENTAGE) / 100);
        var maxY = HEIGHT - ((HEIGHT * PERCENTAGE) / 100);

        // Calculate the Y value of the obstacles
        var yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleTop.getHeight();
        var yBottom = yTop + GAP + obstacleBottom.getHeight();

        // Set the location of the obstacles
        obstacleTop.setLocation(WIDTH, yTop);
        obstacleBottom.setLocation(WIDTH, yBottom);

        // Generate Safe Zone
        SafeZone safeZone = new SafeZone(obstacleTop, obstacleBottom);

        // Add the obstacles and the safe zone to the lists
        obstacles.add(obstacleTop);
        obstacles.add(obstacleBottom);
        safeZones.add(safeZone);

        // Reset the timer
        obstacleSpawnTimer = 0;
    }

    private void removeOutOfBounds() {

        // Temp lists for removal
        ArrayList<Background> backgroundsToRemove = new ArrayList<>();
        ArrayList<Obstacle> obstaclesToRemove = new ArrayList<>();
        ArrayList<SafeZone> safeZonesToRemove = new ArrayList<>();
        ArrayList<Cloud> cloudsToRemove = new ArrayList<>();

        // Search for elements out of bounds
        for (Background background : backgrounds) if (background.getX() + background.getWidth() < 0) backgroundsToRemove.add(background);
        for (Cloud cloud : clouds) if (cloud.getX() + cloud.getWidth() < 0) cloudsToRemove.add(cloud);
        for (Obstacle obstacle : obstacles) if (obstacle.getX() + obstacle.getWidth() < 0) obstaclesToRemove.add(obstacle);
        for (SafeZone safeZone : safeZones) if (safeZone.getX() + safeZone.getWidth() < 0) safeZonesToRemove.add(safeZone);

        // Remove elements out of bounds
        backgrounds.removeAll(backgroundsToRemove);
        clouds.removeAll(cloudsToRemove);
        obstacles.removeAll(obstaclesToRemove);
        safeZones.removeAll(safeZonesToRemove);
    }

    public void render() {

        // Repaint
        frame.getGameUI().repaint();

        // Debug
        fps++;
    }

    // Init Game Variables
    public void init(int backgroundPos) {

        // Init Booleans
        isPaused = false;
        hasCollided = false;
        gameOver = false;
        showFps = false;
        hitboxes = false;
        debug = false;
        cheatsActive = false;
        isJump = false;
        isRainbow = false;

        // Init Variables
        score = 0;
        fps = 0;
        obstacleSpawnTimer = obstacleSpawnRate;

        // Init Game Objects
        player = new Player();
        backgrounds = new ArrayList<>();
        obstacles = new ArrayList<>();
        safeZones = new ArrayList<>();
        clouds = new ArrayList<>();

        // Init Backgrounds
        backgrounds.add(new Background(backgroundPos, 0));
        while (backgrounds.getLast().getX() + backgrounds.getLast().getWidth() < WIDTH)
            backgrounds.add(new Background(backgrounds.getLast().getX() + backgrounds.getLast().getWidth(), 0));
    }

    // Methods

    public void jump() {
        isJump = true;
        if (sound && !gameOver && !hasCollided && !isPaused && player.getY() + player.getHeight() > 0) {
            AudioFile jumpSound = FLAP_SOUND.copy();
            jumpSound.play();
        }
    }

    private void fall() {
        if (sound) {
            AudioFile dieSound = DIE_SOUND.copy();
            dieSound.play();
        }
        gameOver = true;
    }

    private void point() {

        // Increase Score
        score++;

        // Rainbow Spawn
        if (score % 5 == 0 && Calculate.randomChance(RAINBOW_SPAWN_CHANCE)) rainbowUlt();

        // Check if the sound is enabled
        if (!sound) return;

        // Play Point Sound
        AudioFile pointSound = POINT_SOUND.copy();
        pointSound.play();
    }

    private void collision() {
        //if (sound) audioPlayer.play(HIT_SOUND);
        hasCollided = true;
    }

    private void rainbowUlt() {
        new Thread(() -> {
            try {
                isRainbow = true;
                if (sound) {
                    AudioFile rainbowSound = RAINBOW_SOUND.copy();
                    rainbowSound.play();
                }
                Thread.sleep(RAINBOW_DURATION);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            isRainbow = false;
        }).start();
    }

    private void restart() {
        boolean hasCheated = false;

        // Cheats Detected
        if (hasCheated) {
            frame.showMessage(CHEATS_DETECTED, CHEATS_DETECTED_TITLE);
            Calculate.systemShutdown(5);
        }

        // Stop Background Music
        //if (!BACKGROUND_MUSIC.endsWith("empty.wav") && audioPlayer.isPlaying(BACKGROUND_MUSIC)) audioPlayer.stop(BACKGROUND_MUSIC);

        // Reset Game
        frame.getController().restart(debug, cheatsActive || hasCheated, sound, score);
    }

    // Getter
    public Player getPlayer() {
        return player;
    }

    public ArrayList<Background> getBackgrounds() {
        return backgrounds;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<SafeZone> getSafeZones() {
        return safeZones;
    }

    public ArrayList<Cloud> getClouds() {
        return clouds;
    }

    public boolean isRainbow() {
        return isRainbow;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isShowFps() {
        return showFps;
    }

    public boolean isHitboxes() {
        return hitboxes;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getScore() {
        return score;
    }

    public void togglePause() {
        if (!gameOver) {
            isPaused = !isPaused;
            /*if (isPaused) audioPlayer.pauseAll();
            else audioPlayer.resumeAll();*/
        }
    }

    public void toggleKonami() {
        cheatsActive = true;
        debug = !debug;
    }

    public void toggleFps() {
        showFps = !showFps;
    }

    public void toggleHitboxes() {
        hitboxes = !hitboxes;
    }

    public void toggleSound() {
        sound = !sound;
    }
}