package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.GameUI;
import de.MCmoderSD.UI.InputHandler;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.objects.Cloud;
import de.MCmoderSD.objects.Obstacle;
import de.MCmoderSD.objects.Player;
import de.MCmoderSD.objects.SafeZone;

import java.util.ArrayList;

public class Controller implements Runnable {

    // Associations
    private final Frame frame;
    private final InputHandler inputHandler;
    private final GameUI gameUI;
    private final Config config;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<SafeZone> safeZones;
    private final ArrayList<Cloud> clouds;
    // Attributes
    private final Player player;
    // Variables
    private final boolean isPaused;
    private final boolean gameOver;
    private final boolean gameStarted;
    private final double speedModifier;
    private final double tickrate;
    private final int score;
    private int obstacleSpawnTimer;

    // Constructor
    public Controller(Frame frame, InputHandler inputHandler, GameUI gameUI, Config config) {
        this.frame = frame;
        this.inputHandler = inputHandler;
        this.gameUI = gameUI;
        this.config = config;

        // Init Game Variables
        speedModifier = 1;
        isPaused = false;
        gameOver = false;
        gameStarted = true;
        tickrate = config.getMaxFPS();
        score = 0;
        obstacleSpawnTimer = 720;

        // Init Game Objects
        player = new Player(config);
        obstacles = new ArrayList<>();
        safeZones = new ArrayList<>();
        clouds = new ArrayList<>();

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


            // Game Loop
            while (Main.isRunning) {
                current = System.nanoTime();
                delta += (current - now) / (tickrate / speedModifier);
                timer += current - now;
                now = current;

                // Wait for Start
                if (gameStarted) {
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

                    System.out.println("Tick");

                    // ToDo Player Gravity


                    // Obstacle Spawn
                    if (obstacleSpawnTimer >= 720) {
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


                    // Move Obstacles
                    for (Obstacle obstacle : obstacles) {
                        obstacle.move();
                    }

                    // Move Safe Zones
                    for (SafeZone safeZone : safeZones) {
                        safeZone.move();
                    }


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

    // Getter
    public Player getPlayer() {
        return player;
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
}
