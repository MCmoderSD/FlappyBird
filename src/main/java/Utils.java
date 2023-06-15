import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;

/**
 * Diese Klasse enthält Methoden, die zum Berechnen von Werten oder zum Lesen von Dateien benötigt werden.
 */
public class Utils {
    private final Movement movement;
    public Utils(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, boolean sound , String[] args, int points) {
        movement = new Movement(this, width, height, title, icon, resizable, backgroundImage, Tickrate, sound, args, points);
    }
    private long startTime = System.currentTimeMillis();
    /**
     * Methode zum Berechnen der Schwerkraft.
     *
     * @param x der Eingabewert für die Berechnung der Schwerkraft
     * @return den berechneten Schwerkraftwert
     */
    public int calculateGravity(int x) {
        return -2 * x + 4;
    }

    /**
     * Methode zum Überprüfen, ob ein String in einem String-Array enthalten ist.
     *
     * @param array  das String-Array, in dem gesucht wird
     * @param target der zu suchende String
     * @return true, wenn der String im Array enthalten ist, sonst false
     */
    @SuppressWarnings("unused")
    public boolean containsString(String[] array, String target) {
        for (String element : array) {
            return element.equals(target);
        }
        return false;
    }

    /**
     * Methode zum Berechnen der TPS (Ticks per Second).
     *
     * @param tickrate die Tickrate, um die TPS zu berechnen
     * @return den berechneten TPS-Wert
     */
    public int getTPS(int tickrate) {
        return 1000 / tickrate;
    }

    /**
     * Methode zum Lesen eines Bildes aus einer Ressource.
     *
     * @param resource der Pfad zur Bildressource
     * @return das gelesene BufferedImage
     */
    public BufferedImage reader(String resource) {
        try {
            if (resource.endsWith(".png")) {
                return ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
            }
            return ImageIO.read(Objects.requireNonNull(getClass().getResource("error/error.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Methode zum Erstellen eines ImageIcon aus einer Ressource.
     *
     * @param resource der Pfad zur Bildressource
     * @return das erstellte ImageIcon
     */
    public ImageIcon createImageIcon(String resource) {
        URL imageUrl = getClass().getClassLoader().getResource(resource);
        if (resource.endsWith(".png")) {
            return new ImageIcon(reader(resource));
        }
        if (resource.endsWith(".gif")) {
            return new ImageIcon(Objects.requireNonNull(imageUrl));
        } else {
            return new ImageIcon(reader("error/error.png"));
        }
    }

    /**
     * Methode zum Lokalisieren eines Punktes basierend auf Bildgröße.
     *
     * @param image  der Pfad zur Bildressource
     * @param width  die Breite des Rahmens
     * @param height die Höhe des Rahmens
     * @return der lokalisierte Punkt als Point-Objekt
     */
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        return new Point((width -  img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    /**
     * Methode zum Abspielen einer Audiodatei.
     *
     * @param audioFilePath der Pfad zur Audiodatei
     * @param sound         true, um den Ton abzuspielen, false, um den Ton stummzuschalten
     */
    public void audioPlayer(String audioFilePath, boolean sound) {
        if (sound && !Logic.instance.gamePaused) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                // Überprüfen, ob die Audiodatei gefunden wurde
                if (audioFileInputStream == null) {
                    throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);
                }

                BufferedInputStream bufferedInputStream = new BufferedInputStream(audioFileInputStream);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Hinzufügen eines LineListeners, um die Ressourcen freizugeben, wenn die Wiedergabe beendet ist
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        try {
                            clip.close();
                            audioInputStream.close();
                            bufferedInputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Methode zur Rückgabe der Breite des Hintergrunds.
     *
     * @return die Breite des Hintergrunds
     */
    public int getBackgroundWidth() {
        return reader("Images/Background.png").getWidth();
    }

    /**
     * Methode zum Setzen eines Platzhalters für ein JTextField.
     *
     * @param textField   das JTextField, für das der Platzhalter gesetzt wird
     * @param placeholder der Platzhalter-Text
     */
    public void setPlaceholder(JTextField textField, String placeholder) {
        Font originalFont = textField.getFont();

        textField.setForeground(Color.GRAY);
        textField.setFont(originalFont);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    textField.setFont(originalFont);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setFont(originalFont);
                    textField.setText(placeholder);
                }
            }
        });
    }

    /**
     * Methode zum Überprüfen, ob eine Verbindung zu einem Server hergestellt werden kann.
     *
     * @param ip   die IP-Adresse des Servers
     * @param port der Port des Servers
     * @return true, wenn eine Verbindung hergestellt werden kann, sonst false
     */
    public boolean checkSQLConnection(String ip, String port) {
        try (Socket socket = new Socket()) {
            // Verbindungsversuch zum Server
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout von 1 Sekunde
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Methode zum Überprüfen, ob ein Nutzername blockiert ist.
     *
     * @param userName der zu überprüfende Nutzername
     * @return true, wenn der Nutzername blockiert ist, sonst false
     */
    public boolean checkUserName(String userName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(UI.class.getResourceAsStream("data/blockedTerms.txt"))))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Konvertiere sowohl den Nutzernamen als auch die Wörter in Kleinbuchstaben
                String lowercaseUsername = userName.toLowerCase(), lowercaseWord = line.toLowerCase();
                if (lowercaseUsername.contains(lowercaseWord)) {
                    return true; // Wort gefunden
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Wort nicht gefunden
    }

    /**
     * Methode zum Zentrieren eines JFrames.
     *
     * @param frame das JFrame, das zentriert werden soll
     * @return die zentrierte Position als Point-Objekt
     */
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(((screenSize.width - frame.getWidth()) / 2), ((screenSize.height - frame.getHeight()) / 2));
    }

    /**
     * Berechnet die Position des Spielers auf der X-Achse.
     *
     * @param frame das JPanel, das den Spieler enthält
     * @return die X-Position des Spielers
     */
    public int xPlayerPosition(JPanel frame) {
        int x = frame.getWidth() / 4;
        return Math.min(x, 200);
    }

    public long calculateSystemLatency() {
        long currentTime = System.currentTimeMillis();
        long latency = currentTime - startTime;
        startTime = currentTime;
        return latency;
    }
}
