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

public class Methods {
    // Methode zum Berechnen der Schwerkraft
    public int calculateGravity(int x) {
        return -2 * x + 4;
    }

    // Methode zum Überprüfen, ob ein String in einem String-Array enthalten ist
    public boolean containsString(String[] array, String target) {
        for (String element : array) {
            return element.equals(target);
        }
        return false;
    }

    // Methode zum Berechnen der TPS (Ticks per Second)
    public int getTPS(int Tickrate) {
        return 1000 / Tickrate;
    }

    // Methode zum Lesen eines Bildes aus einer Ressource
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

    // Methode zum Erstellen eines ImageIcon aus einer Ressource
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

    // Methode zum Lokalisieren eines Punktes basierend auf Bildgröße
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        return new Point(x, y);
    }

    // Methode zum Abspielen einer Audiodatei
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

    // Methode zur Rückgabe der Breite des Hintergrunds
    public int getBackgroundWidth() {
        return reader("Images/Background.png").getWidth();
    }

    // Methode zum Setzen eines Platzhalters für ein JTextField
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

    // Methode zum Überprüfen, ob eine Verbindung zu einem Server hergestellt werden kann
    public boolean checkSQLConnection(String ip, String port) {
        try (Socket socket = new Socket()) {
            // Verbindungsversuch zum Server
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout von 1 Sekunde

            // Wenn die Verbindung erfolgreich war
            return true;
        } catch (IOException e) {
            // Bei einem Fehler während der Verbindung
            return false;
        }
    }

    // Methode zum Überprüfen, ob ein Nutzername blockiert ist
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

    // Methode zum Zentrieren eines JFrames
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;

        return new Point(x, y);
    }

    // Berechnet die Position des Spielers auf der X-Achse
    public int xPlayerPosition(JPanel frame) {
        int x = frame.getWidth()/4;
        return Math.min(x, 200);
    }
}
