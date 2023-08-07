public class Main {
    public static boolean isRunning;

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {

        String defaultConfig = "lena"; // Default Config
        int jumpHeight = 10; // Jump Height
        int percentage = 25; // Percentage representing the obstacle size relative to the window height
        int gap = 200; // Vertical gap between the obstacles
        int FPS = 360; // Frames per second (updated frames per second) Maximum: 100

        Utils utils = new Utils();

        new Config(utils, defaultConfig, jumpHeight, percentage, gap, FPS, args);
    }
}