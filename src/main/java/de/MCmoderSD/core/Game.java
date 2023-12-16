package de.MCmoderSD.core;

import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.main.Main;
import de.MCmoderSD.objects.Background;
import de.MCmoderSD.objects.Cloud;
import de.MCmoderSD.objects.Obstacle;
import de.MCmoderSD.objects.Player;
import de.MCmoderSD.objects.SafeZone;
import de.MCmoderSD.utilities.Calculate;
import de.MCmoderSD.utilities.sound.AudioPlayer;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;

public class Game implements Runnable {

    // Associations
    private final Frame frame;
    private final Config config;
    private final AudioPlayer audioPlayer;
    private final Random random;

    // Constants
    private final double tickrate;
    private final int obstacleSpawnRate;
    private final int[] cloudSpawnChance;
    private final boolean isReverse;
    private final boolean isLinux;
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
    private boolean tooHigh;
    private boolean isJump;
    private boolean isRainbow;
    private double speedModifier;
    private int score;
    private int fps;
    private int obstacleSpawnTimer;

    // Constructor
    public Game(Frame frame, Config config) {
        this.frame = frame;
        this.config = config;

        isReverse = config.isReverse();
        audioPlayer = config.getAudioPlayer();
        random = new Random();

        // Constants
        tickrate = 2777778;
        isLinux = System.getProperty("os.name").equals("Linux");
        obstacleSpawnRate = (int) (200 / config.getObstacleSpeed());
        cloudSpawnChance = new int[]{1, 5000};
        init(0);

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (Main.IS_RUNNING) {

            // Timer Variables
            double delta = 0;
            long current;
            long timer = 0;
            long now = System.nanoTime();
            int renderedFrames = 0;

            // Wait for start
            if (isJump && frame.getGameUI().isVisible()) gameStarted = true;

            // Game Loop
            while (gameStarted) {

                // Timer
                current = System.nanoTime();
                delta += (current - now) / (tickrate / speedModifier);
                timer += current - now;
                now = current;


                // Tick
                if (delta >= 1) {
                    if (isLinux) Toolkit.getDefaultToolkit().sync();


                    /* <-- Game Loop Start --> */

                    // Game Tick Event
                    double event = gameTick();

                    // Update Frame
                    boolean update = renderedFrames < config.getMaxFPS();
                    int modulo = renderedFrames % frameRate;
                    if (modulo != 0 && update) renderedFrames++;
                    if (modulo == 0 && update) {
                        if (isLinux) Toolkit.getDefaultToolkit().sync();
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

                    /* <-- Game Loop End --> */


                    if (isLinux) Toolkit.getDefaultToolkit().sync();
                    delta--;
                }
            }

            // Delay to prevent 100% CPU usage
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Game Tick
    private double gameTick() {

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
            if (!gameOver && !cheatsActive && player.getY() - player.getHeight() >= config.getHeight())
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
            if (!gameOver && !isPaused && gameStarted && sound && !config.getBackgroundMusic().endsWith("empty.wav") && !audioPlayer.isPlaying(config.getBackgroundMusic()))
                audioPlayer.play(config.getBackgroundMusic(), true);

            // Background Spawn
            Background lastBackground = backgrounds.get(backgrounds.size() - 1);
            if (lastBackground.getX() + lastBackground.getWidth() <= config.getWidth())
                backgrounds.add(new Background(config, config.getWidth(), 0));

            // Cloud Spawn
            if (random.nextInt(cloudSpawnChance[1]) < cloudSpawnChance[0])
                clouds.add(new Cloud(config, config.getWidth(), (int) (Math.random() * config.getHeight() / 2)));

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
            if (!gameOver && !hasCollided && isJump && player.getY() + player.getHeight() > 0) {
                if (isReverse) {
                    tooHigh = false;
                    for (Obstacle obstacle : obstacles)
                        if (!obstacle.isTop() && obstacle.getY() + obstacle.getHeight() * 0.85 <= config.getHeight())
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
        return event;
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

    // Methods

    public void jump() {
        isJump = true;
        if (!isReverse && sound && !gameOver && !hasCollided && !isPaused && player.getY() + player.getHeight() > 0)
            audioPlayer.play(config.getFlapSound());
        else if (isReverse && sound && !gameOver && !hasCollided && !isPaused && !tooHigh)
            audioPlayer.play(config.getFlapSound());
    }

    private void fall() {
        if (sound) audioPlayer.play(config.getDieSound());
        gameOver = true;
    }

    private void point(double event) {
        if (sound) audioPlayer.play(config.getPointSound());
        score++;
        if (score % 5 == 0 && Calculate.randomChance(config.getRainbowSpawnChance())) rainbowUlt();
        keys.add(event);
    }

    private void collision() {
        if (sound) audioPlayer.play(config.getHitSound());
        hasCollided = true;
    }

    private void rainbowUlt() {
        new Thread(() -> {
            try {
                isRainbow = true;
                if (sound) audioPlayer.play(config.getRainbowSound());
                Thread.sleep(config.getRainbowDuration());
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
            frame.showMessage(config.getCheatsDetected(), config.getCheatsDetectedTitle());
            Calculate.systemShutdown(5);
        }

        // Stop Background Music
        if (!config.getBackgroundMusic().endsWith("empty.wav") && audioPlayer.isPlaying(config.getBackgroundMusic()))
            audioPlayer.stop(config.getBackgroundMusic());

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

    public int getFps() {
        return fps;
    }

    // Setter
    public void initGameConstants(boolean sound, int fps) {
        this.sound = sound;
        frameRate = config.getMaxFPS() / fps;
    }

    public void togglePause() {
        if (!gameOver) {
            isPaused = !isPaused;
            if (isPaused) audioPlayer.pauseAll();
            else audioPlayer.resumeAll();
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