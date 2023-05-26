public class Main {
    private static final int TPS = 100;
    // Definiert den Titel des Spiels
    public static String Title = ("Flappy Bird");
    // Dateiname des Bildes für den Spieler
    public static String Player = ("BirdPlaceHolder.png");
    // Dateiname des Bildes für das Hindernis
    public static String Obstacle = ("ObstaclePlaceHolder.png");
    // Dateiname des Hintergrundbildes
    public static String Background = ("Images/Background.png");
    // Dateiname des Game-Over-Bildes
    public static String GameOver = ("GameOver.png");
    // Größe des Spielfensters in der horizontalen Richtung
    public static int WindowSizeX = 800;
    // Größe des Spielfensters in der vertikalen Richtung
    public static int WindowsSizeY = 800;
    // Höhe, um die der Spieler springt
    public static int JumpHeight = 7;

    // Die main-Methode, die das Spiel startet
    public static void main(String[] args) {
        // Erstellt eine neue Instanz der GameLogic-Klasse, um das Spiel zu starten
        new GameLogic();
    }

    // Gibt die Anzahl der Aktualisierungen pro Sekunde (TPS) zurück
    public static int getTPS() {
        return 1000 / TPS;
    }
}
