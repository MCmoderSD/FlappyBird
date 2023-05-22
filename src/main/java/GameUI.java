import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameUI extends JFrame {

    public static Timer t = new Timer(Main.getTPS(), e -> GameLogic.instance.handleTimerTick());
    public GameUI(){
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
        JLabel player = new JLabel();
        this.add(player);
        player.setSize(32, 32);
        player.setIcon(new ImageIcon(Main.Player));
        player.setLocation(250, 400);
        generateObstacles();
    }

    public void moveObstacles() {
        Component[] components = this.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && ((JLabel) component).getIcon() != null) {
                JLabel obstacle = (JLabel) component;
                int x = obstacle.getX();
                int newX = x - 1;
                obstacle.setLocation(newX, obstacle.getY());
            }
        }
    }

    public void movePlayer() {
        //TODO Player Movement
    }

    private void generateObstacles() {
        int initialX = 800; // Startposition der Hindernisse (außerhalb des sichtbaren Bereichs)
        int minY = 200; // Mindesthöhe des ersten Hindernisses
        int maxY = 600; // Maximale Höhe des ersten Hindernisses
        int verticalGap = 200; // Vertikaler Abstand zwischen den Hindernissen

        for (int i = 0; i < 8; i++) {
            JLabel obstacleTop = new JLabel();
            JLabel obstacleBottom = new JLabel();

            this.add(obstacleTop);
            this.add(obstacleBottom);

            obstacleTop.setIcon(new ImageIcon(Main.Obstacle));
            obstacleBottom.setIcon(new ImageIcon(Main.Obstacle));

            int yTop = (int) (Math.random() * (maxY - minY + 1)) + minY;
            int yBottom = yTop + verticalGap;

            obstacleTop.setSize(64, yTop);
            obstacleBottom.setSize(64, 800 - yBottom); // Gesamthöhe des Fensters abzüglich der Höhe des oberen Hindernisses und des vertikalen Abstands

            int x = initialX + i * 400;
            obstacleTop.setLocation(x, 0);
            obstacleBottom.setLocation(x, yBottom);
        }
    }



}