import java.awt.*;

public class Main {
    public static Image Player = Toolkit.getDefaultToolkit().getImage("src/main/resources/BirdPlaceHolder.png");
    public static Image Obstacle = Toolkit.getDefaultToolkit().getImage("src/main/resources/ObstaclePlaceHolder.png");
    public static Image Background = Toolkit.getDefaultToolkit().getImage("src/main/resources/background.png");


    public static void main(String[] args) {
        new GameLogic();
    }

    public static int getTPS() {
        return 10;
    }

}
