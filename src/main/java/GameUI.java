import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class GameUI extends JFrame {

    public static int xposition = -5;
    private int yposition;
    public JLabel player;
    public static Timer t = new Timer(Main.getTPS(), e -> GameLogic.instance.handleTimerTick());
    ArrayList<JLabel> obstacles = new ArrayList<>();
    private int playerMoveInt;

    public GameUI() {
        this.setTitle("Flappy Bird");
        this.setSize(800, 800);
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
        player.setIcon(new ImageIcon(Main.Player));
        player.setLocation(250, 400);
        generateObstacles();
    }

    public void MovePlayer(){
        if (playerMoveInt == 10) {
            xposition = xposition + 1;
            yposition = (player.getY() - GameLogic.instance.calculateGravity(xposition));
            player.setLocation(250, yposition);
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

    public void generateObstacles() {
        int initialX = 800; // Startposition der Hindernisse (außerhalb des sichtbaren Bereichs)
        int minY = 200; // Mindesthöhe des ersten Hindernisses
        int maxY = 600; // Maximale Höhe des ersten Hindernisses
        int verticalGap = 200; // Vertikaler Abstand zwischen den Hindernissen
        while (obstacles.size() < 20) {
            JLabel obstacleTop = new JLabel();
            JLabel obstacleBottom = new JLabel();
            add(obstacleTop);
            add(obstacleBottom);
            obstacleTop.setIcon(new ImageIcon(Main.Obstacle));
            obstacleBottom.setIcon(new ImageIcon(Main.Obstacle));
            int yTop = (int) (Math.random() * (maxY - minY + 1)) + minY;
            int yBottom = yTop + verticalGap;
            obstacleTop.setSize(64, yTop);
            obstacleBottom.setSize(64, 800 - yBottom); // Gesamthöhe des Fensters abzüglich der Höhe des oberen Hindernisses und des vertikalen Abstands
            int x = initialX + (obstacles.size() * 100);
            obstacleTop.setLocation(x, 0);
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
                System.out.println("Obstacle removed at " + x + " in Position " + obstacles.size());
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