import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.awt.Component;
import java.awt.Rectangle;

public class GameUI extends JFrame {

    public static int xPosition = - Main.JumpHeight;
    public static Timer t = new Timer(Main.getTPS(), e -> {
        try {
            GameLogic.instance.handleTimerTick();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    });
    public static JLabel player;
    public static ArrayList<JLabel> obstacles = new ArrayList<>();
    private int playerMoveInt;

    public GameUI() throws IOException {
        this.setTitle("Flappy Bird");
        this.setSize(3000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(true);
        this.setResizable(false);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) GameLogic.instance.handleSpaceKeyPress();
            }
        });
        player = new JLabel();
        add(player);
        player.setSize(32, 32);
        player.setBounds(250, player.getY(), 32, 32);
        BufferedImage playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(Main.Player)));
        player.setIcon(new ImageIcon(playerImage));
        player.setLocation(250, 400);
        generateObstacles(800);
    }

    public void MovePlayer(){
        if (playerMoveInt == 3) {
            xPosition = xPosition + 1;
            int yPosition = (player.getY() - GameLogic.instance.calculateGravity(xPosition));
            player.setLocation(250, yPosition);
            playerMoveInt = 0;
        }
        playerMoveInt = playerMoveInt +1;
    }


    public void moveObstacles() {
        for (JLabel component : obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - 1;
                component.setLocation(newX, component.getY());
            }
        }
    }

    public void generateObstacles(int initial) throws IOException {
        int initialX = 800; // Startposition der Hindernisse (außerhalb des sichtbaren Bereichs)
        int minY = 200; // Mindesthöhe des ersten Hindernisses
        int maxY = 600; // Maximale Höhe des ersten Hindernisses
        int verticalGap = 200; // Vertikaler Abstand zwischen den Hindernissen
        while (obstacles.size() <= 20) {
            JLabel obstacleTop = new JLabel();
            JLabel obstacleBottom = new JLabel();
            add(obstacleTop);
            add(obstacleBottom);
            BufferedImage obstacleTopImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(Main.Obstacle)));
            BufferedImage obstacleBottomImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(Main.Obstacle)));
            obstacleTop.setIcon(new ImageIcon(obstacleTopImage));
            obstacleBottom.setIcon(new ImageIcon(obstacleBottomImage));
            int yTop = (int) (Math.random() * (maxY - minY + 1)) + minY;
            int yBottom = yTop + verticalGap;
            obstacleTop.setSize(64, yTop);
            obstacleBottom.setSize(64, 800 - yBottom); // Gesamthöhe des Fensters abzüglich der Höhe des oberen Hindernisses und des vertikalen Abstands
            int x = initial + (obstacles.size() * 100);
            obstacleTop.setLocation(x, 0);
            obstacleTop.setBounds(x, 0,64, yTop);
            obstacleBottom.setBounds(x, yBottom,64, 800 - yBottom);
            obstacleBottom.setLocation(x, yBottom);
            obstacles.add(obstacleTop);
            obstacles.add(obstacleBottom);
            System.out.println("Obstacle generated at " + x + " " + yTop + " " + yBottom + " in Position " + obstacles.size());
        }
    }


    public void removeObstacles() {
        Iterator<JLabel> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            JLabel component = iterator.next();
            int x = component.getX();
            if (x < -64) {
                remove(component);
                iterator.remove();
                System.out.println("Obstacle removed at " + x);
            }
        }
    }



    public void checkCollision(JLabel player, JLabel obstacle) {
        if (player.getBounds().intersects(obstacle.getBounds())) {
            System.out.println("Collision detected");
            GameLogic.instance.handleCollision();
        }
    }
}