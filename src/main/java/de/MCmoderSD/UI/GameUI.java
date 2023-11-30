package de.MCmoderSD.UI;

import de.MCmoderSD.core.Controller;
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
        setLayout(null);
        frame.add(this);

        Font font = new Font("Roboto", Font.PLAIN, 18);

        // Init Score Label
        scoreLabel = new JLabel(config.getScorePrefix());
        scoreLabel.setFont(font);
        scoreLabel.setForeground(config.getScoreColor());
        scoreLabel.setSize((int) (config.getWidth() * 0.125), (int) (config.getHeight() * 0.05));
        scoreLabel.setLocation(config.getWidth() - scoreLabel.getWidth() - 10, 10);
        add(scoreLabel);

        // Init FPS Label
        fpsLabel = new JLabel("FPS: " + config.getMaxFPS());
        fpsLabel.setFont(font);
        fpsLabel.setForeground(config.getFpsColor());
        fpsLabel.setSize((int) (config.getWidth() * 0.125), (int) (config.getHeight() * 0.05));
        fpsLabel.setLocation(10, 10);
        fpsLabel.setVisible(false);
        add(fpsLabel);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        Controller controller = frame.getController();
        Player player = controller.getPlayer();
        ArrayList<Obstacle> obstacles = controller.getObstacles();
        ArrayList<SafeZone> safeZones = controller.getSafeZones();
        ArrayList<Cloud> clouds = controller.getClouds();
        ArrayList<Background> backgrounds = controller.getBackgrounds();

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
        g.drawImage(player.getImage(), player.getX(), player.getY(), null);

        // Draw Obstacles
        for (Obstacle obstacle : obstacles) {
            g.setColor(obstacle.getColor());
            //g.fill(obstacle.getHitbox());
            g.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), null);
        }

        // Draw Pause or GameOver Image
        if (controller.isPaused() || controller.isGameOver()) {
            BufferedImage image = controller.isPaused() ? config.getPauseImage() : config.getGameOverImage();
            g.drawImage(image, (config.getWidth() - image.getWidth()) / 2, (config.getHeight() - image.getHeight()) / 2, null);
        }

        // HitBox
        if (controller.isHitboxes() || controller.isDebug()) {

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

        scoreLabel.setText(config.getScorePrefix() + controller.getScore());
        fpsLabel.setVisible(controller.isShowFps() || controller.isDebug());
        fpsLabel.setText(config.getFpsPrefix() + controller.getFps());

        // Draw UI Elements
        paintComponents(g);
    }
}
