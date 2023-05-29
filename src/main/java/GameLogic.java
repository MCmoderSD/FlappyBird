public class GameLogic { // Die GameLogic-Klasse, die die Spiellogik enthält
    public static GameLogic instance; // Die Instanz der GameLogic-Klasse
    private static GameUI ui; // Die Instanz der GameUI-Klasse
    private boolean gameState = false, gameOver = false; // Die Variablen, die den Spielzustand speichern
    private int debugTimerTick; // Die Variable, die den Timer-Tick zählt
    public GameLogic(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate) { // Der Konstruktor der GameLogic-Klasse
        instance = this; // Setzt die Instanz der GameLogic-Klassenvariable auf die aktuelle Instanz
        ui = new GameUI(width, height, title, icon, resizable, playerPosition, playerWidth, playerHeight, playerImage, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, gameOverImage, dieSound, flapSound, hitSound, pointSound, Tickrate); // Erstellt eine neue Instanz der GameUI-Klasse
    } // Ende des Konstruktors
    public void handleSpaceKeyPress(String flapSound) { // Behandelt das Leertaste-Drücken
        System.out.println("Space pressed"); // Gibt in der Konsole aus, dass die Leertaste gedrückt wurde
        if (!ui.tickrate.isRunning() && !gameState && !gameOver) { // Wenn das Spiel nicht läuft und nicht im Game-Over-Zustand ist
            ui.tickrate.start(); // Startet den Timer
            ui.gameOver.setVisible(false); // Setzt das Game-Over-Bild auf unsichtbar
            gameState = true; // Setzt den Spielzustand auf true
        } // Ende der Bedingung
        if (!ui.tickrate.isRunning() && !gameState && gameOver) { // Wenn das Spiel nicht läuft und im Game-Over-Zustand ist
            //ToDo ui.restart();
            System.exit(0); // Beendet das Spiel
            gameOver = false; // Setzt den Game-Over-Zustand auf false
        } // Ende der Bedingung
        handleBounce(flapSound); // Behandelt das Abprallen des Spielers
    } // Ende der Methode
    public void handleTimerTick(int width, int height , int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String dieSound, String flapSound, String hitSound, String pointSound) { // Behandelt den Timer-Tick
        debugTimerTick(); // Gibt den Timer-Tick in der Konsole aus
        ui.movePlayer(); // Bewegt den Spieler
        ui.moveObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage); // Bewegt die Hindernisse
        ui.removeObstacles(); // Entfernt die Hindernisse
        ui.checkCollision(width, dieSound, hitSound); // Überprüft die Kollision
    } // Ende der Methode
    public void handleCollision(String dieSound) { // Behandelt die Kollision
        System.out.println("Collision"); // Gibt in der Konsole aus, dass eine Kollision stattgefunden hat
        ui.tickrate.stop(); // Stoppt den Timer
        ui.gameOver.setVisible(true); // Setzt das Game-Over-Bild auf sichtbar
        gameState = false; // Setzt den Spielzustand auf false
        gameOver = true; // Setzt den Game-Over-Zustand auf true
        ui.audioPlayer(dieSound); // Spielt den Tod-Sound ab
    } // Ende der Methode
    public void handleBounce(String flapSound) {
        ui.audioPlayer(flapSound); // Spielt den Flap-Sound ab
        if (ui.player.getY() > 32) { // Wenn der Spieler nicht am oberen Rand ist
            ui.xPosition = -Main.JumpHeight;
        } // Ende der Bedingung
    } // Ende der Methode
    private void debugTimerTick() { // Gibt den Timer-Tick in der Konsole aus
        debugTimerTick++; // Erhöht die Variable um 1
        if (debugTimerTick == 100) { // Wenn die Variable 100 ist
            System.out.println("Timer tick 100"); // Gibt in der Konsole aus, dass der Timer-Tick 100 ist
            debugTimerTick = 0; // Setzt die Variable auf 0
        } // Ende der Bedingung
    } // Ende der Methode
} // Ende der Klasse