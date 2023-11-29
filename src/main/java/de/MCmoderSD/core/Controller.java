package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.GameUI;
import de.MCmoderSD.UI.InputHandler;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.objects.*;
import de.MCmoderSD.utilities.sound.AudioPlayer;

import java.util.ArrayList;
import java.util.Random;

public class Controller implements Runnable {

    // Associations
    private final Frame frame;
    private final InputHandler inputHandler;
    private final GameUI gameUI;
    private final Config config;
    private final AudioPlayer audioPlayer;
    private final Random random;

    // Constants
    private final double tickrate;
    private final int obstacleSpawnRate;
    private final int[] cloudSpawnChance;

    // Attributes
    private Player player;
    private final ArrayList<Background> backgrounds;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<SafeZone> safeZones;
    private final ArrayList<Cloud> clouds;

    // Variables
    private boolean isPaused;
    private boolean gameOver;
    private boolean gameStarted;
    private double speedModifier;

    private int score;
    private int obstacleSpawnTimer;

    // Constructor
    public Controller(Frame frame, InputHandler inputHandler, GameUI gameUI, Config config) {
        this.frame = frame;
        this.inputHandler = inputHandler;
        this.gameUI = gameUI;
        this.config = config;

        audioPlayer = config.getAudioPlayer();
        random = new Random();

        // Constants
        tickrate = 2777778;
        obstacleSpawnRate = (int) (200 / config.getObstacleSpeed());
        cloudSpawnChance = new int[]{1, 5000};

        // Init Game Variables
        speedModifier = 1;
        isPaused = false;
        gameOver = false;
        gameStarted = false;
        score = 0;
        obstacleSpawnTimer = obstacleSpawnRate;

        // Init Game Objects
        player = new Player(config);
        backgrounds = new ArrayList<>();
        obstacles = new ArrayList<>();
        safeZones = new ArrayList<>();
        clouds = new ArrayList<>();


        // Init Backgrounds
        backgrounds.add(new Background(config, 0, 0));
        while (backgrounds.get(backgrounds.size() - 1).getX() + backgrounds.get(backgrounds.size() - 1).getWidth() < config.getWidth())
            backgrounds.add(new Background(config, backgrounds.get(backgrounds.size() - 1).getX() + backgrounds.get(backgrounds.size() - 1).getWidth(), 0));

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (Main.isRunning) {
            // Timer
            double delta = 0;
            long current;
            long timer = 0;
            long now = System.nanoTime();
            int renderedFrames = 0;

            if (inputHandler.isJump()) gameStarted = true;

            // Game Loop
            while (gameStarted) {
                current = System.nanoTime();
                delta += (current - now) / (tickrate / speedModifier);
                timer += current - now;
                now = current;

                // Pause
                if (isPaused) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    delta = 0;
                }

                // Tick
                if (delta >= 1) {
                    // Game Loop Start:


                    // Remove Backgrounds
                    backgrounds.removeIf(backgroundCheck -> backgroundCheck.getX() + backgroundCheck.getWidth() < 0);

                    // Remove Obstacles
                    obstacles.removeIf(obstacleCheck -> obstacleCheck.getX() + obstacleCheck.getWidth() < 0);

                    // Remove Safe Zones
                    safeZones.removeIf(safeZoneCheck -> safeZoneCheck.getX() + safeZoneCheck.getWidth() < 0);

                    // Remove Clouds
                    clouds.removeIf(cloudCheck -> cloudCheck.getX() + cloudCheck.getWidth() < 0);

                    // Background Spawn
                    Background lastBackground = backgrounds.get(backgrounds.size() - 1);
                    if (lastBackground.getX() + lastBackground.getWidth() <= config.getWidth())
                        backgrounds.add(new Background(config, config.getWidth(), 0));

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

                    // Cloud Spawn
                    if (random.nextInt(cloudSpawnChance[1]) < cloudSpawnChance[0]) {
                        Cloud cloud = new Cloud(config, config.getWidth(), (int) (Math.random() * config.getHeight()));
                        clouds.add(cloud);
                    }

                    // Player Movement
                    if (inputHandler.isJump()) {
                        player.jump();
                        audioPlayer.playAudio(config.getFlapSound());
                    }
                    player.fall();

                    // Move Backgrounds
                    for (Background background : backgrounds) background.move();

                    // Move Obstacles
                    for (Obstacle obstacle : obstacles) obstacle.move();

                    // Move Safe Zones
                    for (SafeZone safeZone : safeZones) safeZone.move();

                    // Move Clouds
                    for (Cloud cloud : clouds) cloud.move();

                    // Check for fall
                    if (player.getY() + player.getHeight() >= config.getHeight()) fall();

                    // Check for Collision
                    for (Obstacle obstacle : obstacles)
                        if (player.getHitbox().intersects(obstacle.getHitbox())) collision();

                    // Check for Safe Zone
                    for (SafeZone safeZone : safeZones)
                        if (player.getHitbox().intersects(safeZone.getHitbox())) point(safeZone);


                    // Update Frame
                    if (renderedFrames < config.getMaxFPS()) {
                        frame.repaint();
                        renderedFrames++;
                    }

                    // FPS Counter
                    if (timer >= 1000000000) {
                        timer = 0;
                        renderedFrames = 0;
                    }


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

    private void fall() {
        audioPlayer.playAudio(config.getDieSound());
        if (!gameOver) gameOver();
    }

    private void point(SafeZone safeZone) {
        audioPlayer.playAudio(config.getPointSound());
        safeZones.remove(safeZone);
        score++;
    }

    private void collision() {
        audioPlayer.playAudio(config.getDieSound());
        gameOver();
    }

    private void gameOver() {
        gameOver = true;
        gameStarted = false;
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

    public int getScore() {
        return score;
    }

    // Setter
    public void togglePause() {
        isPaused = !isPaused;
    }
}
