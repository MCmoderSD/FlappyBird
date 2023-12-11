package de.MCmoderSD.UI;

import de.MCmoderSD.core.Game;
import de.MCmoderSD.main.Config;
import de.MCmoderSD.objects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameUI extends JPanel {

    // Associations
    private final Frame frame;
    private final Config config;

    // Attributes
    private final JLabel scoreLabel;
    private final JLabel fpsLabel;

    // Constructor
    public GameUI(Frame frame, Config config) {
        super();

        this.frame = frame;
        this.config = config;

        setPreferredSize(config.getSize());
        setLocation(-config.getWidth(), -config.getHeight());
        setDoubleBuffered(true);
        setLayout(null);
        setVisible(false);
        frame.add(this);

        Font font = new Font("Roboto", Font.PLAIN, 18);

        // Init Score Label
        scoreLabel = new JLabel(config.getScorePrefix());
        scoreLabel.setFont(font);
        scoreLabel.setForeground(config.getScoreColor());
        scoreLabel.setSize(Math.toIntExact(Math.round((config.getWidth() * 0.125))), Math.toIntExact(Math.round((config.getHeight() * 0.05))));
        scoreLabel.setLocation(config.getWidth() - scoreLabel.getWidth() - 10, 10);
        add(scoreLabel);

        // Init FPS Label
        fpsLabel = new JLabel("FPS: " + config.getMaxFPS());
        fpsLabel.setFont(font);
        fpsLabel.setForeground(config.getFpsColor());
        fpsLabel.setSize(Math.toIntExact(Math.round((config.getWidth() * 0.125))), Math.toIntExact(Math.round((config.getHeight() * 0.05))));
        fpsLabel.setLocation(10, 10);
        fpsLabel.setVisible(false);
        add(fpsLabel);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        Game game = frame.getGame();
        Player player = game.getPlayer();
        ArrayList<Obstacle> obstacles = game.getObstacles();
        ArrayList<SafeZone> safeZones = game.getSafeZones();
        ArrayList<Cloud> clouds = game.getClouds();
        ArrayList<Background> backgrounds = game.getBackgrounds();

        // Draw Background
        for (Background background : backgrounds) {
            g.setColor(background.getColor());
            g.fill(background.getHitbox());
            g.drawImage(background.getImage(), background.getX(), background.getY(), null);
        }

        // Draw Clouds
        for (Cloud cloud : clouds) {
            g.setColor(cloud.getColor());
            //g.fill(cloud.getHitbox());
            g.drawImage(cloud.getImage(), cloud.getX(), cloud.getY(), null);
        }

        // Draw Player
        g.setColor(player.getColor());
        //g.fill(player.getHitbox());
        if (game.isRainbow()) g.drawImage(player.getAnimation().getImage(), player.getX(), player.getY(), null);
        else g.drawImage(player.getImage(), player.getX(), player.getY(), null);

        // Draw Obstacles
        for (Obstacle obstacle : obstacles) {
            g.setColor(obstacle.getColor());
            //g.fill(obstacle.getHitbox());
            g.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), null);
        }

        // Draw Pause or GameOver Image
        if (game.isPaused() || game.isGameOver()) {
            BufferedImage image = game.isPaused() ? config.getPauseImage() : config.getGameOverImage();
            g.drawImage(image, (config.getWidth() - image.getWidth()) / 2, (config.getHeight() - image.getHeight()) / 2, null);
        }

        // HitBox
        if (game.isHitboxes() || game.isDebug()) {

            for (Cloud cloud : clouds) {
                g.setColor(cloud.getHitboxColor());
                g.draw(cloud.getHitbox());
            }

            g.setColor(player.getHitboxColor());
            g.draw(player.getHitbox());

            for (Obstacle obstacle : obstacles) {
                g.setColor(obstacle.getHitboxColor());
                g.draw(obstacle.getHitbox());
            }

            for (SafeZone safeZone : safeZones) {
                g.setColor(safeZone.getHitboxColor());
                g.draw(safeZone.getHitbox());
            }
        }

        scoreLabel.setText(config.getScorePrefix() + game.getScore());
        fpsLabel.setVisible(game.isShowFps() || game.isDebug());
        fpsLabel.setText(config.getFpsPrefix() + game.getFps());

        // Draw UI Elements
        paintComponents(g);
    }
}