public class Main {
    // Definiert den Titel des Spiels
    public static final String Title = ("Flappy Bird");
    // Dateiname des Bildes für den Spieler
    public static final String Player = ("BirdPlaceHolder.png");
    // Dateiname des Bildes für das Hindernis
    public static final String ObstacleTop = ("ObstaclePlaceHolderGreen.png"), ObstacleBottom = ("ObstaclePlaceHolderRed.png");
    // Dateiname des Hintergrundbildes
    public static final String Background = ("Images/Background.png");

    public static final String Icon =("Images/Icon.png");
    // Dateiname des Game-Over-Bildes
    public static final String GameOver = ("GameOver.png");
    // Größe des Spielfensters in der horizontalen Richtung
    public static final int WindowSizeX = 800;
    // Größe des Spielfensters in der vertikalen Richtung
    public static final int WindowsSizeY = 800;
    // Höhe, um die der Spieler springt
    public static final int JumpHeight = 7;
    private static final int TPS = 100;

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
