public class Main {
    public static String Title = ("Flappy Bird");
    public static String Player = ("BirdPlaceHolder.png");
    public static String Obstacle = ("ObstaclePlaceHolder.png");
    public static String Background = ("Images/Background.png");
    public static String GameOver = ("GameOver.png");
    public static int WindowSizeX = 800, WindowsSizeY = 800;
    public static int JumpHeight = 7;

    public static void main(String[] args) {
        new GameLogic();
    }
    public static int getTPS() {
        return 10;
    }
}
