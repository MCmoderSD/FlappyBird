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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameUI extends JPanel {

    // Associations
    private final Frame frame;

    // Attributes
    private final JLabel scoreLabel;
    private final JLabel fpsLabel;

    // Constructor
    public GameUI(Frame frame) {
        super();

        this.frame = frame;

        setPreferredSize(Config.SIZE);
        setLocation(-Config.WIDTH, -Config.HEIGHT);
        setDoubleBuffered(true);
        setLayout(null);
        setVisible(false);
        frame.add(this);

        Font font = new Font("Roboto", Font.PLAIN, 18);

        // Init Score Label
        scoreLabel = new JLabel(Config.SCORE_PREFIX);
        scoreLabel.setFont(font);
        scoreLabel.setForeground(Config.SCORE_COLOR);
        scoreLabel.setSize(Math.toIntExact(Math.round((Config.WIDTH * 0.125))), Math.toIntExact(Math.round((Config.HEIGHT * 0.05))));
        scoreLabel.setLocation(Config.WIDTH - scoreLabel.getWidth() - 10, 10);
        add(scoreLabel);

        // Init FPS Label
        fpsLabel = new JLabel("FPS: " + Config.MAX_FPS);
        fpsLabel.setFont(font);
        fpsLabel.setForeground(Config.FPS_COLOR);
        fpsLabel.setSize(Math.toIntExact(Math.round((Config.WIDTH * 0.125))), Math.toIntExact(Math.round((Config.HEIGHT * 0.05))));
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
            BufferedImage image = game.isPaused() ? Config.PAUSE_IMAGE : Config.GAME_OVER_IMAGE;
            g.drawImage(image, (Config.WIDTH - image.getWidth()) / 2, (Config.HEIGHT - image.getHeight()) / 2, null);
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

        scoreLabel.setText(Config.SCORE_PREFIX + game.getScore());
        fpsLabel.setVisible(game.isShowFps() || game.isDebug());
        fpsLabel.setText(Config.FPS_PREFIX + game.getFps());

        // Draw UI Elements
        paintComponents(g);
    }
}