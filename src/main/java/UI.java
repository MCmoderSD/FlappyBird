import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class UI extends JFrame {
    public ImageLoader BirdImage = new ImageLoader("Bird.png");
    int XPosition = -5, YPosition;
    private Canvas BirdCanvas = new Canvas();

    public UI(String title) {
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

        for (int i = 0; i < 4; i++) {
            Pipes[i] = new ImageLoader("Pipe.png");
        }

        Pipes[0].setBounds(300, 0, 90, 200);
        add(Pipes[0]);
        Pipes[1].setBounds(300, 400, 90, 200);
        add(Pipes[1]);
        Pipes[2].setBounds(600, 0, 90, 200);
        add(Pipes[2]);
        Pipes[3].setBounds(600, 400, 90, 200);
        add(Pipes[3]);
    }

    public ImageLoader[] Pipes = new ImageLoader[4];

    public void obstaclesToRight() {
        for (int i = 0; i < 3; i = i + 2) {
            if (Pipes[i].getX() < 0) {
                Pipes[i].setLocation(BirdCanvas.getWidth(), BirdCanvas.getHeight() / 2 + 50 + 20 - (int) (Math.random() * 150));
                Pipes[i + 1].setLocation(BirdCanvas.getWidth(), Pipes[i].getY() - 150 - Pipes[i].getHeight());
            }
        }
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
            t = new Timer(100, move);
            t.start();
            if (YCollisionCheck()) {
                t.stop();
            }
            for (int i = 0; i < 4; i++) {
                Pipes[i].setLocation(Pipes[i].getX() - 10, Pipes[i].getY());
            }
            obstaclesToRight();
            if (checkCollisionWithObstacle()) {
                t.stop();
            }
        }
    };

        ;

        public void BirdImage_MouseClicked() {
            XPosition = -5;
        }

        public void BirdImage_KeyPressed(KeyEvent evt) {
            XPosition = -5;
        }

        public boolean YCollisionCheck() {
            return BirdImage.getY() < 0 | BirdImage.getY() + BirdImage.getHeight() > BirdCanvas.getHeight() - 20;
        }

        public boolean checkCollisionWithObstacle() {
            boolean result = false;
            for (int i = 0; i < 4; i++) {
                if ((BirdImage.getY() < (Pipes[i].getY() + Pipes[i].getHeight()))
                    & ((BirdImage.getY() + BirdImage.getHeight()) > Pipes[i].getY())
                    & (BirdImage.getX() < (Pipes[i].getX() + Pipes[i].getWidth()))
                    & ((BirdImage.getX() + BirdImage.getWidth()) > Pipes[i].getX())) {
                    result = true;
                }
            }
            return result;
        }
}