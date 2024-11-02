package de.MCmoderSD.UI;

import de.MCmoderSD.core.Game;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.objects.Background;
import de.MCmoderSD.objects.Cloud;
import de.MCmoderSD.objects.Obstacle;
import de.MCmoderSD.objects.Player;
import de.MCmoderSD.objects.SafeZone;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import static de.MCmoderSD.main.Config.*;

public class GameUI extends JPanel {

    // Associations
    private final Frame frame;

    // Attributes
    private final JLabel scoreLabel;
    private final JLabel fpsLabel;
    private final JLabel tpsLabel;

    // Variables
    private boolean debug;
    private boolean hitbox;
    private boolean fill;

    // Constructor
    public GameUI(Frame frame) {

        // Init
        super();
        this.frame = frame;
        setPreferredSize(SIZE);
        setLayout(null);
        setVisible(false);
        setBackground(Color.BLACK);
        frame.add(this, BorderLayout.SOUTH);

        // Variables
        Font font = new Font("Roboto", Font.PLAIN, Math.round(Config.HEIGHT * 0.025f));
        var componentSize = Math.round(Config.WIDTH * 0.125f);
        var componentHeight = Math.round(Config.HEIGHT * 0.05f);
        var padding = 10;

        // Init Score Label
        scoreLabel = new JLabel(SCORE_PREFIX + "0");
        scoreLabel.setSize(componentSize, componentHeight);
        scoreLabel.setLocation(Config.WIDTH - scoreLabel.getWidth() - padding, padding);
        scoreLabel.setForeground(SCORE_COLOR);
        scoreLabel.setFont(font);
        add(scoreLabel);

        // Init FPS Label
        fpsLabel = new JLabel("FPS: 0"); // ToDo Make FPS PREFIX
        fpsLabel.setSize(componentSize, componentHeight);
        fpsLabel.setLocation(padding, padding);
        fpsLabel.setForeground(FPS_COLOR);
        fpsLabel.setFont(font);
        fpsLabel.setVisible(false);
        add(fpsLabel);

        // Init TPS Label
        tpsLabel = new JLabel("TPS: 0"); // ToDo Make TPS PREFIX
        tpsLabel.setSize(componentSize, componentHeight);
        tpsLabel.setLocation(padding, padding + componentHeight);
        tpsLabel.setForeground(FPS_COLOR); // ToDo Make TPS Color
        tpsLabel.setFont(font);
        tpsLabel.setVisible(false);
        add(tpsLabel);
    }

    @Override
    public void paintComponent(Graphics graphics) {

        // Graphics
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        // Variables
        Game game = frame.getGame();
        Player player = game.getPlayer();
        ArrayList<Obstacle> obstacles = game.getObstacles();
        ArrayList<SafeZone> safeZones = game.getSafeZones();
        ArrayList<Cloud> clouds = game.getClouds();
        ArrayList<Background> backgrounds = game.getBackgrounds();

        // Draw Background
        for (Background background : backgrounds) {

            // Fill
            if (fill) {
                g.setColor(BACKGROUND_COLOR);
                g.fill(background.getHitbox());
            }

            // Image
            g.drawImage(BACKGROUND_IMAGE, background.getX(), background.getY(), null);
        }

        // Draw Clouds
        for (Cloud cloud : clouds) {

            // Fill
            if (fill) {
                g.setColor(CLOUD_COLOR);
                g.fill(cloud.getHitbox());
            }

            // Image
            g.drawImage(cloud.getImage(), cloud.getX(), cloud.getY(), null);

            // HitBox
            if (hitbox) {
                g.setColor(CLOUD_HITBOX_COLOR);
                g.drawRect(cloud.getX(), cloud.getY(), cloud.getWidth(), cloud.getHeight());
            }
        }

        // Fill
        if (fill) {
            g.setColor(PLAYER_COLOR);
            g.fill(player.getHitbox());
        }

        // Image or Animation
        if (game.isRainbow()) g.drawImage(RAINBOW_ANIMATION.getImage(), player.getX(), player.getY(), null);
        else g.drawImage(PLAYER_IMAGE, player.getX(), player.getY(), null);

        // HitBox
        if (hitbox) {
            g.setColor(PLAYER_HITBOX_COLOR);
            g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        // Draw Obstacles
        for (Obstacle obstacle : obstacles) {

            // Check if Obstacle is visible
            var x = obstacle.getX();
            if (x + obstacle.getWidth() < 0) continue;

            // Fill
            if (fill) {
                g.setColor(obstacle.getColor());
                g.fill(obstacle.getHitbox());
            }

            g.drawImage(obstacle.getImage(), x, obstacle.getY(), null);

            // HitBox
            if (hitbox) {
                g.setColor(OBSTACLE_HITBOX_COLOR);
                g.drawRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
            }
        }

        // Draw SafeZones
        if (hitbox) {
            g.setColor(SAFE_ZONE_COLOR);
            for (SafeZone safeZone : safeZones) g.drawRect(safeZone.getX(), safeZone.getY(), safeZone.getWidth(), safeZone.getHeight());
        }

        // Draw Pause or GameOver Image
        if (game.isPaused() || game.isGameOver()) {
            BufferedImage image = game.isPaused() ? PAUSE_IMAGE : GAME_OVER_IMAGE;
            g.drawImage(image, (Config.WIDTH - image.getWidth()) / 2, (Config.HEIGHT - image.getHeight()) / 2, null);
        }

        // Draw UI Elements
        paintComponents(g);
    }

    // Setter
    public void setScore(int score) {
        scoreLabel.setText(SCORE_PREFIX + score);
    }

    public void setFPS(int fps) {
        fpsLabel.setText("FPS: " + fps);
    }

    public void setTPS(int tps) {
        tpsLabel.setText("TPS: " + tps);
    }

    public void setFPSVisible(boolean visible) {
        fpsLabel.setVisible(visible);
    }

    public void setTPSVisible(boolean visible) {
        tpsLabel.setVisible(visible);
    }

    public void setHitbox(boolean hitbox) {
        this.hitbox = hitbox;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}