public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {

        String defaultConfig = "lena"; // Standardkonfiguration
        double osMultiplier = 0.936745818; // Multiplikator für die Tickrate, um die Tickrate auf dem Betriebssystem anzupassen
        int JumpHeight = 7; // Die Sprunghöhe des Spielers
        int Percentage = 25; // Prozentzahl, die die Größe des Hindernisses von der Fensterhöhe ausmacht
        int Gap = 200; // Vertikaler Abstand zwischen den Hindernissen
        int TPS = 100; // Ticks pro Sekunde (aktualisierte Frames pro Sekunde) Maximum: 100

        Utils utils = new Utils(osMultiplier);

        new Config(utils, defaultConfig, JumpHeight, Percentage, Gap, TPS, args);
    }
}