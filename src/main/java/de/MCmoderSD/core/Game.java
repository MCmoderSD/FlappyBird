package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.InputHandler;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.objects.*;
import de.MCmoderSD.utilities.Calculate;
import de.MCmoderSD.utilities.sound.AudioPlayer;

import java.util.ArrayList;
import java.util.Random;

public class Game implements Runnable {

    // Associations
    private final Frame frame;
    private final InputHandler inputHandler;
    private final Config config;
    private final AudioPlayer audioPlayer;
    private final Random random;

    // Constants
    private final double tickrate;
    private final int obstacleSpawnRate;
    private final int[] cloudSpawnChance;
    private final boolean isReverse;
    private int frameRate;
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
    private double speedModifier;
    private int score;
    private int fps;
    private int obstacleSpawnTimer;

    // Constructor
    public Game(Frame frame, InputHandler inputHandler, Config config) {
        this.frame = frame;
        this.inputHandler = inputHandler;
        this.config = config;

        isReverse = config.isReverse();
        audioPlayer = config.getAudioPlayer();
        random = new Random();

        // Constants
        tickrate = 2777778;
        obstacleSpawnRate = (int) (200 / config.getObstacleSpeed());
        cloudSpawnChance = new int[]{1, 5000};
        init(0);

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (Main.IS_RUNNING) {

            // Timer
            double delta = 0;
            long current;
            long timer = 0;
            long now = System.nanoTime();
            int renderedFrames = 0;

            if (inputHandler.isJump() && frame.getGameUI().isVisible()) gameStarted = true;

            // Game Loop
            while (gameStarted) {
                current = System.nanoTime();
                delta += (current - now) / (tickrate / speedModifier);
                timer += current - now;
                now = current;

                // Tick
                if (delta >= 1) {
                    // Game Loop Start:

                    // Anti Cheat Generate Events
                    double event = random.nextDouble();


                    if (!isPaused()) {

                        // Check for Restart
                        if (gameOver && inputHandler.isJump()) restart();

                        // Temp lists for removal
                        ArrayList<Background> backgroundsToRemove = new ArrayList<>();
                        ArrayList<Obstacle> obstaclesToRemove = new ArrayList<>();
                        ArrayList<SafeZone> safeZonesToRemove = new ArrayList<>();
                        ArrayList<Cloud> cloudsToRemove = new ArrayList<>();

                        // Check for fall
                        if (!gameOver && !cheatsActive && player.getY() - player.getHeight() >= config.getHeight())
                            fall();

                        // Check for Collision
                        if (!hasCollided && !gameOver && !cheatsActive) for (Obstacle obstacle : obstacles)
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

                        // Background Spawn
                        Background lastBackground = backgrounds.get(backgrounds.size() - 1);
                        if (lastBackground.getX() + lastBackground.getWidth() <= config.getWidth())
                            backgrounds.add(new Background(config, config.getWidth(), 0));

                        // Cloud Spawn
                        if (random.nextInt(cloudSpawnChance[1]) < cloudSpawnChance[0]) {
                            Cloud cloud = new Cloud(config, config.getWidth(), (int) (Math.random() * config.getHeight() / 2));
                            clouds.add(cloud);
                        }

                        // Obstacle Spawn
                        if (obstacleSpawnTimer >= obstacleSpawnRate) {
                            Obstacle obstacleTop = new Obstacle(config, true);
                            Obstacle obstacleBottom = new Obstacle(config, false);

                            // Calculate the minimum and maximum Y value
                            int minY = ((config.getHeight() * config.getPercentage()) / 100);
                            int maxY = config.getHeight() - ((config.getHeight() * config.getPercentage()) / 100);

                            // Calculate the Y value of the obstacles
                            int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleTop.getHeight();
                            int yBottom = yTop + config.getGap() + obstacleBottom.getHeight();

                            // Set the location of the obstacles
                            obstacleTop.setLocation(config.getWidth(), yTop);
                            obstacleBottom.setLocation(config.getWidth(), yBottom);

                            // Generate Safe Zone
                            SafeZone safeZone = new SafeZone(config, obstacleTop, obstacleBottom);

                            // Add the obstacles and the safe zone to the lists
                            obstacles.add(obstacleTop);
                            obstacles.add(obstacleBottom);
                            safeZones.add(safeZone);

                            // Reset the timer
                            obstacleSpawnTimer = 0;
                        } else obstacleSpawnTimer++;

                        // Player Movement
                        if (!gameOver && !hasCollided && inputHandler.isJump() && player.getY() + player.getHeight() > 0) {
                            if (sound) audioPlayer.play(config.getFlapSound());
                            if (isReverse) {
                                boolean toHigh = false;
                                for (Obstacle obstacle : obstacles)
                                    if (!obstacle.isTop() && obstacle.getY() + obstacle.getHeight() * 0.85 <= config.getHeight())
                                        toHigh = true;
                                if (!toHigh) {
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

                    // Update Frame
                    boolean update = renderedFrames < config.getMaxFPS();
                    int modulo = renderedFrames % frameRate;
                    if (modulo != 0 && update) renderedFrames++;
                    if (modulo == 0 && update) {
                        frame.repaint();
                        renderedFrames++;
                    }


                    // FPS Counter
                    if (timer >= 1000000000) {
                        timer = 0;
                        fps = renderedFrames / frameRate;
                        renderedFrames = 0;
                    }

                    // Anti Cheat
                    events.add(event);

                    // Game Loop End:
                    delta--;
                }
            }

            // Delay to prevent 100% CPU Usage
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Methods
    public void init(int backgroundPos) {
        // Init Game Variables
        speedModifier = 1;
        isPaused = false;
        hasCollided = false;
        gameOver = false;
        gameStarted = false;
        showFps = false;
        hitboxes = false;
        debug = false;
        cheatsActive = false;
        score = 0;
        obstacleSpawnTimer = obstacleSpawnRate;

        // Init Game Objects
        player = new Player(config);
        backgrounds = new ArrayList<>();
        obstacles = new ArrayList<>();
        safeZones = new ArrayList<>();
        clouds = new ArrayList<>();
        keys = new ArrayList<>();
        events = new ArrayList<>();


        // Init Backgrounds
        backgrounds.add(new Background(config, backgroundPos, 0));
        while (backgrounds.get(backgrounds.size() - 1).getX() + backgrounds.get(backgrounds.size() - 1).getWidth() < config.getWidth())
            backgrounds.add(new Background(config, backgrounds.get(backgrounds.size() - 1).getX() + backgrounds.get(backgrounds.size() - 1).getWidth(), 0));
    }

    private void fall() {
        if (sound) audioPlayer.play(config.getDieSound());
        gameOver = true;
    }

    private void point(double event) {
        if (sound) audioPlayer.play(config.getPointSound());
        score++;
        keys.add(event);
    }

    private void collision() {
        if (sound) audioPlayer.play(config.getHitSound());
        hasCollided = true;
    }

    private boolean hasCheated() {
        if (events.size() <= keys.size()) return true;
        for (double key : keys) if (!events.contains(key)) return true;
        return false;
    }

    private void restart() {

        // Cheats Detected
        if (hasCheated()) {
            frame.showMessage(config.getCheatsDetected(), config.getCheatsDetectedTitle());
            Calculate.systemShutdown(5);
        }

        // Reset Game
        frame.getController().restart(debug, hasCheated(), sound, score);
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

    public int getFps() {
        return fps;
    }

    // Setter

    public void initGameConstants(boolean sound, int fps) {
        this.sound = sound;
        frameRate = config.getMaxFPS() / fps;
    }

    public void togglePause() {
        if (!gameOver) isPaused = !isPaused;
    }

    public void toggleFps() {
        showFps = !showFps;
    }

    public void toggleHitboxes() {
        hitboxes = !hitboxes;
    }

    public void toggleKonami() {
        cheatsActive = true;
        debug = !debug;
    }

    public void toggleSound() {
        sound = !sound;
    }
}
