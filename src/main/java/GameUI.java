import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameUI extends JFrame {

    public static Timer t = new Timer(Main.getTPS(), e -> GameLogic.handleTimerTick());
    public GameUI(){
        this.setTitle("Flappy Bird");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == ' ') GameLogic.handleSpaceKeyPress();
            }
        });
        JLabel player = new JLabel();
        this.add(player);
        player.setSize(32, 32);
        player.setIcon(new ImageIcon(Main.Player));
        player.setLocation(250, 400);

    }

    public void movePlayer() {

    }

    private void generateObstacle() {
        for (int i = 0; i <4; i++) {
            JLabel obstacle = new JLabel();
            this.add(obstacle);
        }
    }
}