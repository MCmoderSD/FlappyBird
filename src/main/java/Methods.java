import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class Methods {
    public static Methods instance;

    // Konstruktor und Instanz
    public Methods() {
        instance = this;
    }

    // Methode zum Berechnen der Schwerkraft
    public int calculateGravity(int x) {
        return -2 * x + 4;
    }

    // Methode zum Überprüfen, ob ein String in einem String-Array enthalten ist
    public boolean containsString(String[] array, String target) {
        for (String element : array) {
            if (element.equals(target)) {
                return true;
            }
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
        if (resource.endsWith(".png")) return new ImageIcon(reader(resource));
        if (resource.endsWith(".gif")) return new ImageIcon(Objects.requireNonNull(imageUrl));
        else return new ImageIcon(reader("error/error.png"));
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
                if (audioFileInputStream == null)
                    throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);

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
}
