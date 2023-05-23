import java.io.IOException;

public class Main {
    public static String Player = ("BirdPlaceHolder.png");
    public static String Obstacle = ("ObstaclePlaceHolder.png");
    public static String Background = ("background.png");

    public static int JumpHeight = 7;


    public static void main(String[] args) throws IOException {
        new GameLogic();
    }

    public static int getTPS() {
        return 10;
    }

}
