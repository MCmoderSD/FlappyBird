import java.awt.*;

public class ImageLoader extends Canvas {
    private Image backgroundImage = null;

    public ImageLoader(String s) {
        backgroundImage = Toolkit.getDefaultToolkit().getImage(s);
        if (backgroundImage != null) {
            repaint();
        }
    }
    public void paint(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, this);
    }
    public void show(String s){
        backgroundImage.flush();
        backgroundImage = Toolkit.getDefaultToolkit().getImage(s);
        if (backgroundImage != null) {
            repaint();
        }
    }
}
