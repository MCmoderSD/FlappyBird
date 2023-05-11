import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class UI extends JFrame {
    public ImageLoader BirdImage = new ImageLoader("Bird.png");
    int XPosition = -5, YPosition;
    private Canvas BirdCanvas = new Canvas();

    public UI(String title){
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 524, frameHeight = 503, width = 600, height = 600;
        setSize(width, height);
        setBackground(Color.WHITE);
        BirdImage.setBounds(100, 300, 51, 36);
        add(BirdImage);
        add(BirdCanvas);
        BirdCanvas.setBounds(-8, 24, 585, 449);
        BirdCanvas.setSize(width, height);
        BirdCanvas.setLocation(0, 0);
    }
    public int parabola(int x) {
        int y = -3*x+4;
        return y;
    }
    Timer t;

    ActionListener move = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            XPosition = XPosition + 1;
            YPosition = parabola(BirdImage.getY() - parabola(XPosition));
            BirdImage.setLocation(100, YPosition);
            t = new Timer (100, move);
            t.start();
            if (YCollisionCheck()) {
                t.stop();
            }
        }
    };

    public void BirdImage_MouseClicked() {
        XPosition = -5;
    }

    public void BirdImage_KeyPressed(KeyEvent evt) {
        XPosition = -5;
    }

    public boolean YCollisionCheck() {
        return BirdImage.getY() < 0 | BirdImage.getY() + BirdImage.getHeight() > BirdCanvas.getHeight() - 20;
    }



    public static void main(String[] args) {
        UI ui = new UI("Flappy Bird");
        ui.setVisible(true);
    }
}