import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
public class GameUI extends JFrame { // Klasse für die Benutzeroberfläche
    private final ArrayList<JLabel> obstacles = new ArrayList<>(); // Liste der Hindernisse
    private final ArrayList<Rectangle> rObstacles = new ArrayList<>(); // Liste der Rechtecke der Hindernisse
    public int xPosition = - Main.JumpHeight;
    public JLabel player, gameOver; // JLabels für den Spieler und das Game-Over-Bild
    public Timer tickrate; // Timer für die Aktualisierungen
    private JPanel mainPanel; // JPanel für das Spiel
    private Rectangle rPlayer; // Rechteck für den Spieler
    private int playerMoveInt = 0, obstacleMoveInt = 200;

    public GameUI(int width, int height, String title, String icon, boolean resizable, int playerPosition, int playerWidth, int playerHeight, String backgroundImage, String playerImage, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage, String gameOverImage, String dieSound, String flapSound, String hitSound, String pointSound, int Tickrate) { // Konstruktor
        initFrame(width, height, title, icon, resizable); // Initialisiert das Fenster
        initMainPanel(width, height, backgroundImage); // Initialisiert das Hauptpanel
        initPlayer(height, playerPosition, playerWidth, playerHeight, playerImage); // Initialisiert den Spieler
        initGameOver(width, height, gameOverImage); // Initialisiert das Game-Over-Bild
        tickrate = new Timer(Tickrate, e -> { // Initialisiert den Timer
            if (System.getProperty("os.name").equals("linux")) Toolkit.getDefaultToolkit().sync(); // Synchronisiert die Animationen auf Linux
            GameLogic.instance.handleTimerTick(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage, dieSound, flapSound, hitSound, pointSound); // Ruft die Methode handleTimerTick() in der GameLogic-Klasse auf
        }); // Ende des Timer-Blocks
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { // Wenn eine Taste gedrückt wird
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) GameLogic.instance.handleSpaceKeyPress(flapSound); // Wenn die Leertaste gedrückt wird, wird die Methode handleSpaceKeyPress() in der GameLogic-Klasse aufgerufen
            }
        }); // Ende des KeyListener-Blocks
        addMouseListener(new MouseListener() { // Wenn die Maus gedrückt wird
            @Override
            public void mouseClicked(MouseEvent e) { GameLogic.instance.handleSpaceKeyPress(flapSound); } // Wenn die Maus gedrückt wird, wird die Methode handleSpaceKeyPress() in der GameLogic-Klasse aufgerufen
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        }); // Ende des MouseListener-Blocks
    } // Ende des Konstruktors

    public void audioPlayer(String audioFilePath) { // Methode zum Abspielen von Audiodateien
        Thread thread = new Thread(() -> { // Erstellt einen neuen Thread
            try { // Versucht die Audiodatei abzuspielen
                ClassLoader classLoader = getClass().getClassLoader(); // Erstellt einen neuen ClassLoader
                InputStream audioFileInputStream = classLoader.getResourceAsStream(audioFilePath); // Erstellt einen neuen InputStream
                if (audioFileInputStream == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath); // Wenn die Audiodatei nicht gefunden wurde, wird eine Fehlermeldung ausgegeben
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFileInputStream);  // Erstellt einen neuen AudioInputStream
                Clip clip = AudioSystem.getClip(); // Erstellt einen neuen Clip
                clip.open(audioInputStream); // Öffnet den Clip
                clip.start(); // Startet den Clip
                System.out.println("Audio wird abgespielt... " + audioFilePath); // Gibt eine Nachricht in der Konsole aus
                Thread.sleep(clip.getMicrosecondLength() / 1000); // Wartet bis der Clip zu Ende ist
                clip.close(); // Schließt den Clip
                audioInputStream.close(); // Schließt den AudioInputStream
                audioFileInputStream.close(); // Schließt den InputStream
            } catch (Exception e) { // Wenn ein Fehler auftritt, wird eine Fehlermeldung ausgegeben
                e.printStackTrace(); // Gibt den Fehler in der Konsole aus
            } // Ende des try-catch-Blocks
        }); // Ende des Thread-Blocks
        thread.start(); // Startet den Thread
    } // Ende der Methode audioPlayer()

    private void initFrame(int width, int height, String title, String icon, boolean resizable) { // Initialisiert das Fenster
        setTitle(title); // Setzt den Titel des Fensters
        setSize(width, height); // Setzt die Größe des Fensters
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Setzt die Standardoperation beim Schließen des Fensters
        setLayout(null); // Setzt das Layout des Fensters
        setVisible(true); // Setzt die Sichtbarkeit des Fensters
        setResizable(resizable); // Setzt die Größe des Fensters
        setIconImage((reader(icon))); // Setzt das Icon des Fensters
    } // Ende der Methode initFrame()

    private void initMainPanel(int width, int height, String backgroundImage) { // Initialisiert das Hauptpanel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                    g.drawImage(reader(backgroundImage), 0, 0, getWidth(), getHeight(), this);

            }
        };
        mainPanel.setSize(width, height); // Setzt die Größe des Hauptpanels
        mainPanel.setLayout(null); // Setzt das Layout des Hauptpanels
        mainPanel.setOpaque(false); // Setzt die Deckkraft des Hauptpanels
        add(mainPanel); // Fügt das Hauptpanel zum Fenster hinzu
    } // Ende der Methode initMainPanel()

    private void initPlayer(int height, int playerPosition, int playerWidth, int playerHeight, String playerImage) { // Initialisiert den Spieler
        player = new JLabel(); // Erstellt einen neuen JLabel
        mainPanel.add(player); // Fügt den JLabel zum Hauptpanel hinzu
        player.setSize(playerWidth, playerHeight); // Setzt die Größe des JLabels
        player.setBounds(playerPosition, player.getY(), playerWidth, playerHeight); // Setzt die Position des JLabels
        rPlayer = new Rectangle(player.getBounds()); // Erstellt ein neues Rechteck
        player.setIcon(new ImageIcon(reader(playerImage))); // Setzt das Bild des JLabels
        player.setLocation(playerPosition, height/2); // Setzt die Position des JLabels
    } // Ende der Methode initPlayer()

    private void initGameOver(int width, int height, String gameOverImage) { // Initialisiert das GameOver-Label
        gameOver = new JLabel(); // Erstellt einen neuen JLabel
        mainPanel.add(gameOver); // Fügt den JLabel zum Hauptpanel hinzu
        gameOver.setVisible(false); // Setzt die Sichtbarkeit des JLabels
        gameOver.setSize(width, height); // Setzt die Größe des JLabels
        gameOver.setLocation(0, 0); // Setzt die Position des JLabels
        gameOver.setIcon(new ImageIcon(reader(gameOverImage))); // Setzt das Bild des JLabels
    } // Ende der Methode initGameOver()

    public void movePlayer() { // Bewegt den Spieler
        if (playerMoveInt == 3) {
            xPosition = xPosition + 1;
            int yPosition = (player.getY() - calculateGravity(xPosition));
            player.setLocation(250, yPosition);
            playerMoveInt = 0; // Setzt den Zähler zurück
        } // Ende der if-Abfrage
        playerMoveInt = playerMoveInt + 1; // Zählt den Zähler hoch
    } // Ende der Methode movePlayer()

    public void generateObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) { // Generiert die Hindernisse
        int minY = ((height * percentage) / 100); // Mindesthöhe des ersten Hindernisses
        int maxY = height - ((height * percentage) / 100); // Maximale Höhe des ersten Hindernisses
        JLabel obstacleTop = new JLabel(), obstacleBottom = new JLabel(); // Erstelle die Hindernisse
        mainPanel.add(obstacleTop); // Füge die Hindernisse dem Fenster hinzu
        mainPanel.add(obstacleBottom); // Füge die Hindernisse dem Fenster hinzu
        obstacleTop.setIcon(new ImageIcon(reader(obstacleTopImage))); // Setze das Bild des Hindernisses
        obstacleBottom.setIcon(new ImageIcon(reader(obstacleBottomImage))); // Setze das Bild des Hindernisses
        int yTop = (int) (Math.random() * (maxY - minY + 1) + minY) - obstacleHeight; // Zufällige Höhe des ersten Hindernisses
        int yBottom = yTop + verticalGap + obstacleHeight; // Berechne die Höhe des zweiten Hindernisses
        obstacleTop.setBounds(width, yTop, obstacleWidth, obstacleHeight); // Setze die Position des ersten Hindernisses
        obstacleTop.setLocation(width, yTop); // Setze die Position des ersten Hindernisses
        obstacleBottom.setSize(obstacleWidth, obstacleHeight); // Setze die Größe des zweiten Hindernisses
        obstacleBottom.setBounds(width, yBottom, obstacleWidth, obstacleHeight); // Setze die Position des zweiten Hindernisses
        obstacleBottom.setLocation(width, yBottom); // Setze die Position des zweiten Hindernisses
        obstacles.add(obstacleTop); // Füge das erste Hindernis der Liste hinzu
        obstacles.add(obstacleBottom); // Füge das zweite Hindernis der Liste hinzu
        Rectangle rObstacleTop = new Rectangle(obstacleTop.getBounds()); // Erstelle ein Rechteck für das erste Hindernis
        rObstacleTop.setBounds(obstacleTop.getBounds()); // Erstelle ein Rechteck für das erste Hindernis
        Rectangle rObstacleBottom = new Rectangle(obstacleBottom.getBounds()); // Erstelle ein Rechteck für das zweite Hindernis
        rObstacleBottom.setBounds(obstacleBottom.getBounds()); // Erstelle ein Rechteck für das zweite Hindernis
        rObstacles.add(rObstacleTop); // Füge das erste Rechteck der Liste hinzu
        rObstacles.add(rObstacleBottom); // Füge das zweite Rechteck der Liste hinzu
        System.out.println("Obstacles: " + obstacles.size()); // Gibt die Anzahl der Hindernisse aus
        System.out.println("Rectangles: " + rObstacles.size()); // Gibt die Anzahl der Rechtecke aus
    } // Ende der Methode generateObstacles()

    public void moveObstacles(int width, int height, int percentage, int verticalGap, int obstacleWidth, int obstacleHeight, String obstacleTopImage, String obstacleBottomImage) { // Bewegt die Hindernisse
        for (JLabel component : obstacles) { // Geht alle Hindernisse durch
            if (component != null && component.getIcon() != null) { // Wenn das Hindernis nicht null ist
                int x = component.getX(); // Speichert die X-Position des Hindernisses
                int newX = x - 1; // Berechnet die neue X-Position des Hindernisses
                component.setLocation(newX, component.getY()); // Setzt die neue X-Position des Hindernisses
            } // Ende der if-Abfrage
        } // Ende der for-Schleife
        for (Rectangle component : rObstacles) { // Geht alle Rechtecke durch
            if (component != null) { // Wenn das Rechteck nicht null ist
                component.getBounds(); // Speichert die Position des Rechtecks
                int x =  (int) component.getX(); // Speichert die X-Position des Rechtecks
                int newX = x - 1; // Berechnet die neue X-Position des Rechtecks
                component.setLocation(newX, (int) component.getY()); // Setzt die neue X-Position des Rechtecks
            } // Ende der if-Abfrage
        } // Ende der for-Schleife
        obstacleMoveInt = obstacleMoveInt + 1; // Zählt den Zähler hoch
        if (obstacleMoveInt >= 200) { // Wenn der Zähler 200 erreicht hat
            generateObstacles(width, height, percentage, verticalGap, obstacleWidth, obstacleHeight, obstacleTopImage, obstacleBottomImage); // Generiere neue Hindernisse
            obstacleMoveInt = 0; // Setze den Zähler zurück
        } // Ende der if-Abfrage
    } // Ende der Methode moveObstacles()

    public void removeObstacles() { // Entfernt die Hindernisse
        Iterator<JLabel> iterator = obstacles.iterator(); // Erstellt einen Iterator für die Hindernisse
        while (iterator.hasNext()) { // Geht alle Hindernisse durch
            JLabel component = iterator.next(); // Speichert das aktuelle Hindernis
            int x = component.getX(); // Speichert die X-Position des Hindernisses
            if (x < -64) { // Wenn das Hindernis außerhalb des Fensters ist
                mainPanel.remove(component); // Entferne das Hindernis aus dem Fenster
                iterator.remove(); // Entferne das Hindernis aus der Liste
                System.out.println("Obstacle removed at " + x + "x and " + component.getY() + "y"); // Gibt die Position des entfernten Hindernisses aus
            } // Ende der if-Abfrage
        } // Ende der while-Schleife
    } // Ende der Methode removeObstacles()

    public void checkCollision(int width, String dieSound, String hitSound) { // Überprüft, ob der Spieler mit einem Hindernis kollidiert
        rPlayer.setLocation(player.getX(), player.getY()); // Setzt die Position des Rechtecks auf die Position des Spielers
        if (player.getY() > width) GameLogic.instance.handleCollision(dieSound); // Wenn der Spieler außerhalb des Fensters ist
        for (Rectangle component : rObstacles) { // Geht alle Rechtecke durch
            if (component != null) if (rPlayer.intersects(component)) { // Wenn der Spieler mit einem Rechteck kollidiert
                audioPlayer(hitSound); // Spiele den Sound ab
                GameLogic.instance.handleCollision(dieSound);
            } // Ende der if-Abfrage
        } // Ende der for-Schleife
    } // Ende der Methode checkCollision()

    private BufferedImage reader(String resource) { // Liest ein Bild aus einer Datei
        try { // Versuche
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(resource))); // Gibt das Bild zurück
        } catch (IOException e) { // Wenn ein Fehler auftritt
            throw new RuntimeException(e); // Gibt den Fehler zurück
        } // Ende der try-catch-Abfrage
    } // Ende der Methode reader()

    public int calculateGravity(int x) { // Berechnet die Schwerkraft
        //return (int) (0.5 * 9.81 * Math.pow(x, 2));
        return -3*x+4; // Berechnet die Schwerkraft
    } // Ende der Methode calculateGravity()
} // Ende der Klasse GameUI