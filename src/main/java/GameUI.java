import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
public class GameUI extends JFrame {
    private final ArrayList<JLabel> obstacles = new ArrayList<>();
    private final ArrayList<Rectangle> rObstacles = new ArrayList<>();
    public int xPosition = - Main.JumpHeight;
    public JLabel player, gameOver;
    public Timer tickrate;
    private JPanel mainPanel;
    private Rectangle rPlayer;
    private int playerMoveInt = 0, obstacleMoveInt = 200;
    public GameUI(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate) {
        initFrame(width, height, title, icon, resizable);
        initMainPanel(width, height);
        initPlayer(height, playerPosition, playerWidth, playerHeight, playerImage);
        initGameOver(width, height, gameOverImage);
        tickrate = new Timer(Tickrate, e -> {
            if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync();
            GameLogic.instance.handleTimerTick(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, dieSound, flapSound, hitSound, pointSound);
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) GameLogic.instance.handleSpaceKeyPress(flapSound);
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameLogic.instance.handleSpaceKeyPress(flapSound);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }
    private void initFrame(int width, int height, String title, String icon, boolean resizable) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        setResizable(resizable);
        setIconImage((reader(icon)));
    }
    private void initMainPanel(int width, int height) {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                File imFile = new File("C:\\Users\\MCmoderSD\\Desktop\\Background.png");
                Image im = Toolkit.getDefaultToolkit().getImage(imFile.getAbsolutePath());
                g.drawImage(im, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setSize(width, height);
        mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        add(mainPanel);
    }
    private void initPlayer(int height, int playerPosition, int playerWidth, int playerHeight, String playerImage) {
        player = new JLabel();
        mainPanel.add(player);
        player.setSize(playerWidth, playerHeight);
        player.setBounds(playerPosition, player.getY(), playerWidth, playerHeight);
        rPlayer = new Rectangle(player.getBounds());
        player.setIcon(new ImageIcon(reader(playerImage)));
        player.setLocation(playerPosition, height/2);
    }
    private void initGameOver(int width, int height, String gameOverImage) {
        gameOver = new JLabel();
        mainPanel.add(gameOver);
        gameOver.setVisible(false);
        gameOver.setSize(width, height);
        gameOver.setLocation(0, 0);
        gameOver.setIcon(new ImageIcon(reader(gameOverImage)));
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
    public void generateObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) {
        int minY = ((height * percentage) / 100); // Mindesthöhe des ersten Hindernisses
        int maxY = height - ((height * percentage) / 100); // Maximale Höhe des ersten Hindernisses
        JLabel obstacleTop = new JLabel(), obstacleBottom = new JLabel(); // Erstelle die Hindernisse
        mainPanel.add(obstacleTop); // Füge die Hindernisse dem Fenster hinzu
        mainPanel.add(obstacleBottom); // Füge die Hindernisse dem Fenster hinzu
        obstacleTop.setIcon(new ImageIcon(reader(obstacleTopImage))); // Setze das Bild des Hindernisses
        obstacleBottom.setIcon(new ImageIcon(reader(obstacleBottomImage))); // Setze das Bild des Hindernisses
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
        System.out.println("Obstacles: " + obstacles.size());
        System.out.println("Rectangles: " + rObstacles.size());
    }
    public void moveObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) {
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
            generateObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage);
            obstacleMoveInt = 0;
        }
    }
    public void removeObstacles() {
        Iterator<JLabel> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            JLabel component = iterator.next();
            int x = component.getX();
            if (x < -64) {
                mainPanel.remove(component);
                iterator.remove();
                System.out.println("Obstacle removed at " + x + "x and " + component.getY() + "y");
            }
        }
    }
    public void checkCollision(int width, String dieSound, String hitSound) {
        rPlayer.setLocation(player.getX(), player.getY());
        if (player.getY() > width) GameLogic.instance.handleCollision(dieSound);
        for (Rectangle component : rObstacles) {
            if (component != null) if (rPlayer.intersects(component)) {
                audioPlayer(hitSound);
                GameLogic.instance.handleCollision(dieSound);
            }
        }
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
    public void audioPlayer(String audioFilePath) {
        Thread thread = new Thread(() -> {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL audioFileUrl = classLoader.getResource(audioFilePath);
            if (audioFileUrl == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFileUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            System.out.println("Audio wird abgespielt... " + audioFilePath);
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }});
        thread.start();
    }
}