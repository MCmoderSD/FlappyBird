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

public class GameUI extends JFrame {
    public int xPosition = -Main.JumpHeight;
    public JLabel player, gameOver, background;
    public Rectangle rPlayer, rTubeTop1, rTubeBottom1,rTubeTop2, rTubeBottom2,rTubeTop3, rTubeBottom3;
    public ArrayList<JLabel> obstacles = new ArrayList<>();
    private int playerMoveInt;
    public GameUI() {
        initFrame(Main.WindowSizeX, Main.WindowsSizeY);
        //initBackground(Main.WindowSizeX, Main.WindowsSizeY);
        initPlayer();
        generateObstacles(Main.WindowSizeX);
        initRectangles();
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
        this.setTitle(Main.Title);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    private void initBackground(int width, int height) {
        background = new JLabel();
        add(background);
        background.setSize(width, height);
        background.setLocation(0, 0);
        background.setIcon(new ImageIcon(reader(Main.Background)));
    }

    private void initPlayer() {
        player = new JLabel();
        add(player);
        player.setSize(32, 32);
        player.setBounds(250, player.getY(), 32, 32);
        player.setIcon(new ImageIcon(reader(Main.Player)));
        player.setLocation(250, 400);
    }
    private void initRectangles() {
        rPlayer =new Rectangle(player.getBounds());
        rTubeTop1 = new Rectangle(obstacles.get(0).getBounds());
        rTubeBottom1 = new Rectangle(obstacles.get(1).getBounds());
        rTubeTop2 = new Rectangle(obstacles.get(2).getBounds());
        rTubeBottom2 = new Rectangle(obstacles.get(3).getBounds());
        rTubeTop3 = new Rectangle(obstacles.get(4).getBounds());
        rTubeBottom3 = new Rectangle(obstacles.get(5).getBounds());
    }
    private void initGameOver() {
        gameOver = new JLabel();
        add(gameOver);
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

    public void moveBackground() {
        /*int backgroundX = background.getX();
        int backgroundWidth = background.getWidth();

        // Hintergrund um 1 Pixel nach links bewegen
        background.setLocation(backgroundX - 1, 0);

        // Überprüfen, ob der Hintergrund das JFrame vollständig verlassen hat
        if (backgroundX + backgroundWidth <= 0) {
            generateNewBackground();
        }*/
    }

    private void generateNewBackground() {
        // Neuen Hintergrund generieren
        JLabel newBackground = new JLabel();
        newBackground.setSize(background.getWidth(), background.getHeight());
        newBackground.setLocation(background.getX() + background.getWidth(), 0);
        newBackground.setIcon(new ImageIcon(reader(Main.Background)));

        // Alten Hintergrund entfernen und neuen Hintergrund hinzufügen
        remove(background);
        background = newBackground;
        add(background);
        refreshLayers();
        repaint();  // JFrame neu zeichnen
    }


    public void generateObstacles(int initial) {
        int minY = 200; // Mindesthöhe des ersten Hindernisses
        int maxY = 600; // Maximale Höhe des ersten Hindernisses
        int verticalGap = 200; // Vertikaler Abstand zwischen den Hindernissen
        while (obstacles.size() <= 20) {
            JLabel obstacleTop = new JLabel();
            JLabel obstacleBottom = new JLabel();
            add(obstacleTop);
            add(obstacleBottom);
            obstacleTop.setIcon(new ImageIcon(reader(Main.Obstacle)));
            obstacleBottom.setIcon(new ImageIcon(reader(Main.Obstacle)));
            int x = initial + (obstacles.size() * 100);
            int yTop = (int) (Math.random() * (maxY - minY + 1)) + minY;
            obstacleTop.setSize(32, yTop);
            obstacleTop.setLocation(x, 0);
            obstacleTop.setBounds(x, 0,32, yTop);
            int yBottom = yTop + verticalGap;
            obstacleBottom.setSize(32, 800 - yBottom);
            obstacleBottom.setLocation(x, yBottom);
            obstacleBottom.setBounds(x, yBottom,32, 800 - yBottom);
            obstacles.add(obstacleTop);
            obstacles.add(obstacleBottom);
            refreshLayers();
            System.out.println("Obstacle generated at " + x + " " + yTop + " " + yBottom + " in Position " + obstacles.size());
        }
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
    public void removeObstacles() {
        Iterator<JLabel> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            JLabel component = iterator.next();
            int x = component.getX();
            if (x < -10) {
                remove(component);
                iterator.remove();
                System.out.println("Obstacle removed at " + x);
            }
        }
    }
    public void checkCollision() {
        rPlayer.setLocation(player.getX(), player.getY());
        rTubeTop1.setLocation(obstacles.get(0).getX(), obstacles.get(0).getY());
        rTubeBottom1.setLocation(obstacles.get(1).getX(), obstacles.get(1).getY());
        rTubeTop2.setLocation(obstacles.get(2).getX(), obstacles.get(2).getY());
        rTubeBottom2.setLocation(obstacles.get(3).getX(), obstacles.get(3).getY());
        rTubeTop3.setLocation(obstacles.get(4).getX(), obstacles.get(4).getY());
        rTubeBottom3.setLocation(obstacles.get(5).getX(), obstacles.get(5).getY());
        if (player.getY() > 800 ||
                rPlayer.intersects(rTubeTop1) || rPlayer.intersects(rTubeBottom1) ||
                rPlayer.intersects(rTubeTop2) || rPlayer.intersects(rTubeBottom2) ||
                rPlayer.intersects(rTubeTop3) || rPlayer.intersects(rTubeBottom3)) GameLogic.instance.handleCollision();
    }

    public void refreshLayers() {
        /*setComponentZOrder(player, 2);
        setComponentZOrder(gameOver, 0);
        setComponentZOrder(background, 3);
        for (JLabel obstacle : obstacles) {
            setComponentZOrder(obstacle, 1);
        }*/
    }

    private BufferedImage reader(String ressource) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(ressource)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int calculateGravity(int x) {
        //return (int) (0.5 * 9.81 * Math.pow(x, 2));
        return -3*x+4;
    }public Timer t = new Timer(Main.getTPS(), e -> {
        GameLogic.instance.handleTimerTick();
        if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync();
    });
}