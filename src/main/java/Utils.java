import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Klasse für alle Utensiien
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Utils {
    private final double osMultiplier;
    private final HashMap<String, Clip> AudioClips = new HashMap<>(); // Speichert alle AudioClips
    private final HashMap<String, BufferedImage> BufferedImages = new HashMap<>(); // Speichert alle BufferedImages
    private final HashMap<String, ImageIcon> ImageIcons = new HashMap<>(); // Speichert alle ImageIcons
    private final HashMap<String, BufferedInputStream> BufferedInputStreams = new HashMap<>(); // Speichert alle BufferedInputStreams
    private final HashMap<String, AudioInputStream> AudioInputStreams = new HashMap<>(); // Speichert alle AudioInputStreams
    private long startTime = System.currentTimeMillis();

    // Konstruktor und Multiplikator für die Tickrate
    public Utils(double osMultiplier) {
        this.osMultiplier = osMultiplier;
    }

    // Berechnet die Flugbahn des Spielers
    public int calculateGravity(int x) {
        return -2 * x + 4;
    }

    // Läd Bilddateien
    public BufferedImage reader(String resource) {
        if (!BufferedImages.containsKey(resource)) { // Überprüft, ob das Bild bereits geladen wurde
            try {
                if (resource.endsWith(".png")) {
                    BufferedImages.put(resource, ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)))); // Läd das Bild
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return BufferedImages.get(resource); // Gibt das Bild zurück
    }

    // Erstellt ein ImageIcon aus Bildern
    public ImageIcon createImageIcon(String resource) {
        if (!ImageIcons.containsKey(resource)) { // Überprüft, ob der Pfad bereits geladen wurde
            if (resource.endsWith(".png")) {
                ImageIcons.put(resource, new ImageIcon(reader(resource))); // Erstellt ein ImageIcon
            }
            if (resource.endsWith(".gif")) {
                URL imageUrl = getClass().getClassLoader().getResource(resource);
                ImageIcons.put(resource, new ImageIcon(Objects.requireNonNull(imageUrl))); // Erstellt ein ImageIcon
            }
        }
        return ImageIcons.get(resource); // Gibt das ImageIcon zurück
    }

    // Zentriert ein Bild mittig
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        return new Point((width -  img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    // Läd Musikdateien und spielt sie ab
    public void audioPlayer(String audioFilePath, boolean sound, boolean loop) {
        if (sound && !Logic.instance.gamePaused) {
            CompletableFuture.runAsync(() -> {
                try {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                // Überprüfen, ob die Audiodatei gefunden wurde
                if (audioFileInputStream == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);

                BufferedInputStreams.put(audioFilePath, new BufferedInputStream(audioFileInputStream));
                AudioInputStreams.put(audioFilePath, AudioSystem.getAudioInputStream(BufferedInputStreams.get(audioFilePath)));

                Clip clip = AudioSystem.getClip();
                clip.open(AudioInputStreams.get(audioFilePath));

                // Hinzufügen eines LineListeners, um die Ressourcen freizugeben, wenn die Wiedergabe beendet ist
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        AudioClips.remove(audioFilePath);
                        clip.close();

                        if (loop && Objects.equals(audioFilePath, "error/emtpy.wav")) audioPlayer(audioFilePath, true, true);
                    }
                });

                AudioClips.put(audioFilePath, clip);
                clip.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Stopt alle AudioClips
    public void stopAudio() {

        // Kopiert die HashMap, um ConcurrentModificationException zu vermeiden
        HashMap<String, Clip> audioClipsCopy = new HashMap<>(AudioClips);
        HashMap<String, BufferedInputStream> bufferedInputStreamsCopy = new HashMap<>(BufferedInputStreams);
        HashMap<String, AudioInputStream> audioInputStreamsCopy = new HashMap<>(AudioInputStreams);

        // Schleife, um alle AudioClips zu stoppen
        try {
            for (String audioClip : audioClipsCopy.keySet()) {
                AudioClips.get(audioClip).stop();
            }

            for (String bufferedInputStream : bufferedInputStreamsCopy.keySet()) {
                BufferedInputStreams.get(bufferedInputStream).close();
            }

            for (String audioInputStream : audioInputStreamsCopy.keySet()) {
                AudioInputStreams.get(audioInputStream).close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Leert die HashMaps
        AudioClips.clear();
        BufferedInputStreams.clear();
        AudioInputStreams.clear();
    }

    // Berechnet die Breite des Hintergrunds
    public int getBackgroundWidth(String path) {
        return BufferedImages.get(path).getWidth();
    }

    // Placeholder für Textfelder (den Username)
    public void setPlaceholder(JTextField textField, String placeholder) {

        Color foregroundColor = textField.getForeground();
        Font originalFont = textField.getFont();

        textField.setForeground(Color.GRAY);
        textField.setFont(originalFont);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(foregroundColor);
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

    // Überprüft die Internetverbindung zum SQL Server
    public boolean checkSQLConnection(String ip, String port) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket()) {
                // Verbindungsversuch zum Server
                socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000); // Timeout von 1 Sekunde
                return true;
            } catch (IOException e) {
                return false;
            }
        });

        try {
            return future.get(); // Warten auf das Ergebnis des asynchronen Aufrufs
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Überprüft, ob der Username blockierte Begriffe enthält
    public boolean checkUserName(String userName) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> { // Asynchroner Aufruf
            try (InputStream inputStream = getClass().getResourceAsStream("data/blockedTerms.txt")) { // Läd die blockierten Begriffe
                assert inputStream != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (userName.contains(line)) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });

        try {
            return future.get(); // Warten auf das Ergebnis des asynchronen Aufrufs
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Zentrirt das Fenster mittig auf dem Bildschirm
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Bildschirmgröße
        return new Point(((screenSize.width - frame.getWidth()) / 2), ((screenSize.height - frame.getHeight()) / 2));
    }

    // Zentriert das Fenster mittig auf dem Bildschirm
    public int xPlayerPosition(JPanel frame) {
        int x = frame.getWidth() / 4;
        return Math.min(x, 200);
    }

    // Berechnet die Tickrate je nach Betriebssystem
    public double calculateOSspecifcTickrate(double Tickrate) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) return Tickrate;
        if (os.contains("linux")) return (Tickrate * osMultiplier); // Windows Lag Compensation
        return Tickrate;
    }

    // Berechnet die Latenz des Systems
    public long calculateSystemLatency() {
        long currentTime = System.currentTimeMillis(); // Aktuelle Zeit
        long latency = currentTime - startTime; // Latenz
        startTime = currentTime;
        soutLogger("latency-log.txt", String.valueOf(latency));
        return latency;
    }

    // Schreibt Strings in eine Log-Datei
    public void soutLogger(String file, String message) {
        if (Logic.instance.developerMode) {
            CompletableFuture.runAsync(() -> { // Asynchroner Aufruf
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.append(message);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // Liest eine JSON-Datei aus
    public JsonNode readJson(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Die Ressource konnte nicht gefunden werden: " + path);
            }
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Überprüft ob der 11. September ist
    public JsonNode checkDate(String Default) {
        JsonNode config;
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() == 9 && date.getDayOfMonth() == 11 ) config = readJson("config/911beta.json");
        else config = readJson("config/" + Default + ".json");
        return config;
    }
}
