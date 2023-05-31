import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Methods {
    public static Methods instance;

    // Konstruktor und Instanz
    public Methods() {
        instance = this;
    }

    // Methode zum Berechnen der Schwerkraft
    public int calculateGravity(int x) {
        return -3 * x + 4;
    }

    // Methode zum Berechnen der TPS (Ticks per Second)
    public int getTPS(int TPS) {
        return 1000 / TPS;
    }

    // Methode zum Lesen eines Bildes aus einer Ressource
    public BufferedImage reader(String resource) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Methode zum Abspielen einer Audiodatei
    public void audioPlayer(String audioFilePath, boolean sound) {
        if (sound) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                // Überprüfen, ob die Audiodatei gefunden wurde
                if (audioFileInputStream == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);

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
}