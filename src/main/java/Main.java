public class Main {
    public static boolean isRunning;

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {

        String defaultConfig = "lena"; // Standardkonfiguration
        int jumpHeight = 10; // Die Sprunghöhe des Spielers
        int percentange = 25; // Prozentzahl, die die Größe des Hindernisses von der Fensterhöhe ausmacht
        int gap = 200; // Vertikaler Abstand zwischen den Hindernissen
        int FPS = 360; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100

        Utils utils = new Utils();

        new Config(utils, defaultConfig, jumpHeight, percentange, gap, FPS, args);
    }
}