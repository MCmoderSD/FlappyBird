package de.MCmoderSD.core;

import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.executor.NanoLoop;
import de.MCmoderSD.main.Config;

import de.MCmoderSD.objects.Background;
import de.MCmoderSD.objects.Cloud;
import de.MCmoderSD.objects.Obstacle;
import de.MCmoderSD.objects.Player;
import de.MCmoderSD.objects.SafeZone;
import de.MCmoderSD.utilities.Calculate;

import java.util.ArrayList;
import java.util.Random;

import static de.MCmoderSD.main.Config.*;

public class Game {

    // Associations
    private final Frame frame;

    // Utilities
    private final Random random;



    // Game Threads
    private final NanoLoop tickExecutor;
    private final NanoLoop renderExecutor;
    private final NanoLoop debugExecutor;

    // Debug Variables
    private int fps;
    private int tps;

    // Constants
    private final int obstacleSpawnRate;
    private final int[] cloudSpawnChance;
    private final boolean isReverse;
    private boolean sound;

    // Attributes
    private Player player;
    private ArrayList<Background> backgrounds;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<SafeZone> safeZones;
    private ArrayList<Cloud> clouds;
    private ArrayList<Double> keys;
    private ArrayList<Double> events;

    // Variables
    private boolean isPaused;
    private boolean hasCollided;
    private boolean gameOver;
    private boolean showFps;
    private boolean hitboxes;
    private boolean debug;
    private boolean cheatsActive;
    private boolean gameStarted;
    private boolean tooHigh;
    private boolean isJump;
    private boolean isRainbow;
    private double speedModifier;
    private int score;
    private int obstacleSpawnTimer;

    // Constructor
    public Game(Frame frame) {
        this.frame = frame;

        isReverse = Config.IS_REVERSE;
        random = new Random();

        sound = true;

        // Constants
        obstacleSpawnRate = (int) (200 / Config.OBSTACLE_SPEED);
        cloudSpawnChance = new int[]{1, 5000};
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

        // Wait for start
        if (isJump && frame.getGameUI().isVisible()) gameStarted = true;


        // Generate Event
        double event = random.nextDouble() * System.nanoTime();

        if (!isPaused()) {

            // Check for Restart
            if (gameOver && isJump && frame.getGameUI().isVisible()) restart();

            // Temp lists for removal
            ArrayList<Background> backgroundsToRemove = new ArrayList<>();
            ArrayList<Obstacle> obstaclesToRemove = new ArrayList<>();
            ArrayList<SafeZone> safeZonesToRemove = new ArrayList<>();
            ArrayList<Cloud> cloudsToRemove = new ArrayList<>();

            // Check for fall
            if (!gameOver && !cheatsActive && player.getY() - player.getHeight() >= Config.HEIGHT)
                fall();

            // Check for Collision
            if (!hasCollided && !gameOver && !isRainbow && !cheatsActive)
                for (Obstacle obstacle : obstacles)
                    if (player.getHitbox().intersects(obstacle.getHitbox())) collision();


            // Check for Safe Zone
            if (!gameOver) for (SafeZone safeZone : safeZones)
                if (player.getHitbox().intersects(safeZone.getHitbox())) {
                    safeZonesToRemove.add(safeZone);
                    point(event);
                }


            // Remove elements that are out of bounds
            for (Background background : backgrounds)
                if (background.getX() + background.getWidth() < 0) backgroundsToRemove.add(background);
            for (Cloud cloud : clouds) if (cloud.getX() + cloud.getWidth() < 0) cloudsToRemove.add(cloud);
            for (Obstacle obstacle : obstacles)
                if (obstacle.getX() + obstacle.getWidth() < 0) obstaclesToRemove.add(obstacle);
            for (SafeZone safeZone : safeZones)
                if (safeZone.getX() + safeZone.getWidth() < 0) safeZonesToRemove.add(safeZone);

            // Remove elements from original lists
            backgrounds.removeAll(backgroundsToRemove);
            clouds.removeAll(cloudsToRemove);
            obstacles.removeAll(obstaclesToRemove);
            safeZones.removeAll(safeZonesToRemove);

            // Background Music
           /*if (!gameOver && !isPaused && gameStarted && sound && !Config.BACKGROUND_MUSIC.endsWith("empty.wav") && !audioPlayer.isPlaying(Config.BACKGROUND_MUSIC))
                audioPlayer.play(Config.BACKGROUND_MUSIC, true);*/

            // Background Spawn
            Background lastBackground = backgrounds.getLast();
            if (lastBackground.getX() + lastBackground.getWidth() <= Config.WIDTH)
                backgrounds.add(new Background(Config.WIDTH, 0));

            // Cloud Spawn
            if (random.nextInt(cloudSpawnChance[1]) < cloudSpawnChance[0])
                clouds.add(new Cloud(Config.WIDTH, (int) (Math.random() * Config.HEIGHT / 2)));

            // Obstacle Spawn
            if (obstacleSpawnTimer >= obstacleSpawnRate) {
                Obstacle obstacleTop = new Obstacle(true);
                Obstacle obstacleBottom = new Obstacle(false);

                // Calculate the minimum and maximum Y value
                int minY = ((Config.HEIGHT * Config.PERCENTAGE) / 100);
                int maxY = Config.HEIGHT - ((Config.HEIGHT * Config.PERCENTAGE) / 100);

                // Calculate the Y value of the obstacles
                int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleTop.getHeight();
                int yBottom = yTop + Config.GAP + obstacleBottom.getHeight();

                // Set the location of the obstacles
                obstacleTop.setLocation(Config.WIDTH, yTop);
                obstacleBottom.setLocation(Config.WIDTH, yBottom);

                // Generate Safe Zone
                SafeZone safeZone = new SafeZone(obstacleTop, obstacleBottom);

                // Add the obstacles and the safe zone to the lists
                obstacles.add(obstacleTop);
                obstacles.add(obstacleBottom);
                safeZones.add(safeZone);

                // Reset the timer
                obstacleSpawnTimer = 0;
            } else obstacleSpawnTimer++;

            // Player Movement
            if (!gameOver && !hasCollided && isJump && player.getY() + player.getHeight() > 0) {
                if (isReverse) {
                    tooHigh = false;
                    for (Obstacle obstacle : obstacles)
                        if (!obstacle.isTop() && obstacle.getY() + obstacle.getHeight() * 0.85 <= Config.HEIGHT)
                            tooHigh = true;
                    if (!tooHigh) {
                        obstacles.forEach(Obstacle::jump);
                        safeZones.forEach(SafeZone::jump);
                    }
                } else player.jump();
            }

            if (!isReverse || hasCollided) player.fall();
            else {
                boolean toLow = false;
                for (Obstacle obstacle : obstacles)
                    if (obstacle.isTop() && obstacle.getY() + 1 > 0) toLow = true;

                if (!toLow) {
                    obstacles.forEach(Obstacle::fall);
                    safeZones.forEach(SafeZone::fall);
                }
            }

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
        }

        isJump = false;



        // Debug
        tps++;
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
        gameStarted = false;
        tooHigh = false;
        isJump = false;
        isRainbow = false;

        // Init Variables
        speedModifier = 1;
        score = 0;
        fps = 0;
        obstacleSpawnTimer = obstacleSpawnRate;

        // Init Game Objects
        player = new Player();
        backgrounds = new ArrayList<>();
        obstacles = new ArrayList<>();
        safeZones = new ArrayList<>();
        clouds = new ArrayList<>();
        keys = new ArrayList<>();
        events = new ArrayList<>();

        // Init Backgrounds
        backgrounds.add(new Background(backgroundPos, 0));
        while (backgrounds.getLast().getX() + backgrounds.getLast().getWidth() < Config.WIDTH)
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

    private void point(double event) {
        if (sound) {
            AudioFile pointSound = POINT_SOUND.copy();
            pointSound.play();
        }        score++;
        if (score % 5 == 0 && Calculate.randomChance(Config.RAINBOW_SPAWN_CHANCE)) rainbowUlt();
        keys.add(event);
    }

    private void collision() {
        //if (sound) audioPlayer.play(Config.HIT_SOUND);
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
        boolean hasCheated = Calculate.hasCheated(events, keys);

        // Cheats Detected
        if (hasCheated) {
            frame.showMessage(Config.CHEATS_DETECTED, Config.CHEATS_DETECTED_TITLE);
            Calculate.systemShutdown(5);
        }

        // Stop Background Music
        //if (!Config.BACKGROUND_MUSIC.endsWith("empty.wav") && audioPlayer.isPlaying(Config.BACKGROUND_MUSIC)) audioPlayer.stop(Config.BACKGROUND_MUSIC);

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