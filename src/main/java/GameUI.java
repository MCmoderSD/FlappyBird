import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class GameUI extends JFrame {
    public int xPosition = -Main.JumpHeight;
    public JLabel player, gameOver, background;
    public JPanel mainPanel, backgroundPanel;
    public Rectangle rPlayer;
    public ArrayList<JLabel> obstacles = new ArrayList<>();
    public ArrayList<Rectangle> rObstacles = new ArrayList<>();
    private int playerMoveInt = 0, obstacleMoveInt = 200;
    public GameUI() {
        initFrame(Main.WindowSizeX, Main.WindowsSizeY);
        initPlayer();
        initGameOver();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) GameLogic.instance.handleSpaceKeyPress();
            }
        });
    }
    private void initFrame(int width, int height) {
        setTitle(Main.Title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setIconImage((reader(Main.Icon)));
        initBackground(width, height);
        initMainPanel(width, height);
    }
    private void initMainPanel(int width, int height) {
        mainPanel = new JPanel();
        mainPanel.setSize(width, height);
        mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);
    }
    private void initBackground(int width, int height) {
        backgroundPanel = new JPanel();
        backgroundPanel.setSize(width, height);
        background = new JLabel();
        background.setSize(width, height);
        backgroundPanel.add(background);
        background.setIcon(new ImageIcon(reader(Main.Background)));
        setContentPane(backgroundPanel);
    }
    private void initPlayer() {
        player = new JLabel();
        mainPanel.add(player);
        player.setSize(32, 32);
        player.setBounds(250, player.getY(), 32, 32);
        rPlayer = new Rectangle(player.getBounds());
        player.setIcon(new ImageIcon(reader(Main.Player)));
        player.setLocation(250, 400);
    }

    private void initGameOver() {
        gameOver = new JLabel();
        mainPanel.add(gameOver);
        gameOver.setVisible(false);
        gameOver.setSize(Main.WindowSizeX, Main.WindowsSizeY);
        gameOver.setLocation(0, 0);
        gameOver.setIcon(new ImageIcon(reader(Main.GameOver)));
    }
    public void movePlayer() {
        if (playerMoveInt == 3) {
            xPosition = xPosition + 1;
            int yPosition = (player.getY() - calculateGravity(xPosition));
            player.setLocation(250, yPosition);
            playerMoveInt = 0;
        }
        playerMoveInt = playerMoveInt + 1;
    }
    public void generateObstacles(int width, int height, int percentage) {
        int minY = ((height * percentage) / 100); // Mindesthöhe des ersten Hindernisses
        int maxY = height - ((height * percentage) / 100); // Maximale Höhe des ersten Hindernisses
        int verticalGap = 200; // Vertikaler Abstand zwischen den Hindernissen
        int obstacleWidth = 32, obstacleHeight = 1024; // Breite und Höhe der Hindernisse
        JLabel obstacleTop = new JLabel(), obstacleBottom = new JLabel(); // Erstelle die Hindernisse
        mainPanel.add(obstacleTop); // Füge die Hindernisse dem Fenster hinzu
        mainPanel.add(obstacleBottom); // Füge die Hindernisse dem Fenster hinzu
        obstacleTop.setIcon(new ImageIcon(reader(Main.ObstacleTop))); // Setze das Bild des Hindernisses
        obstacleBottom.setIcon(new ImageIcon(reader(Main.ObstacleBottom))); // Setze das Bild des Hindernisses
        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleHeight; // Zufällige Höhe des ersten Hindernisses
        int yBottom = yTop + verticalGap + obstacleHeight; // Berechne die Höhe des zweiten Hindernisses
        obstacleTop.setBounds(width, yTop, obstacleWidth, obstacleHeight); // Setze die Position des ersten Hindernisses
        obstacleTop.setLocation(width, yTop); // Setze die Position des ersten Hindernisses
        obstacleBottom.setSize(obstacleWidth, obstacleHeight); // Setze die Größe des zweiten Hindernisses
        obstacleBottom.setBounds(width, yBottom, obstacleWidth, obstacleHeight); // Setze die Position des zweiten Hindernisses
        obstacleBottom.setLocation(width, yBottom); // Setze die Position des zweiten Hindernisses
        obstacles.add(obstacleTop); // Füge das erste Hindernis der Liste hinzu
        obstacles.add(obstacleBottom); // Füge das zweite Hindernis der Liste hinzu
        Rectangle rObstacleTop = new Rectangle(obstacleTop.getBounds()); // Erstelle ein Rechteck für das erste Hindernis
        rObstacleTop.setBounds(obstacleTop.getBounds()); // Erstelle ein Rechteck für das erste Hindernis
        Rectangle rObstacleBottom = new Rectangle(obstacleBottom.getBounds()); // Erstelle ein Rechteck für das zweite Hindernis
        rObstacleBottom.setBounds(obstacleBottom.getBounds()); // Erstelle ein Rechteck für das zweite Hindernis
        rObstacles.add(rObstacleTop); // Füge das erste Rechteck der Liste hinzu
        rObstacles.add(rObstacleBottom); // Füge das zweite Rechteck der Liste hinzu
    }

    public void moveObstacles() {
        for (JLabel component : obstacles) {
            if (component != null && component.getIcon() != null) {
                int x = component.getX();
                int newX = x - 1;
                component.setLocation(newX, component.getY());
            }
        }
        for (Rectangle component : rObstacles) {
            if (component != null) {
                component.getBounds();
                int x =  (int) component.getX();
                int newX = x - 1;
                component.setLocation(newX, (int) component.getY());
            }
        }
        obstacleMoveInt = obstacleMoveInt + 1;
        if (obstacleMoveInt >= 200) {
            generateObstacles(Main.WindowSizeX, Main.WindowsSizeY, 25);
            obstacleMoveInt = 0;
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
        /*Iterator<Rectangle> rIterator = rObstacles.iterator();
        while (rIterator.hasNext()) {
            JLabel component = iterator.next();
            int x = component.getX();
            if (x < -64) {
                remove(component);
                iterator.remove();
                System.out.println(" removed at " + x);
            }
        }*/
    }

    public void checkCollision() {
        rPlayer.setLocation(player.getX(), player.getY());
        if (player.getY() > Main.WindowsSizeY) GameLogic.instance.handleCollision();
    }

    private BufferedImage reader(String resource) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int calculateGravity(int x) {
        //return (int) (0.5 * 9.81 * Math.pow(x, 2));
        return -3*x+4;
    }
























}