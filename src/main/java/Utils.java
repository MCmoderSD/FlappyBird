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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Klasse für alle Utensiien
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class Utils {
    private final double osMultiplier;
    private final HashMap<String, Clip> HeavyClipCache = new HashMap<>(); // Cache für AudioClips
    private final HashMap<String, BufferedImage> bufferedImageCache = new HashMap<>(); // Cache für BufferedImages
    private final HashMap<String, ImageIcon> imageIconCache = new HashMap<>(); // Cache für ImageIcons
    private final ArrayList<BufferedInputStream> HeavyBufferedInputStreamCache = new ArrayList<>(); // Cache für BufferedInputStreams
    private final ArrayList<AudioInputStream> HeavyAudioInputStreamCache = new ArrayList<>(); // Cache für AudioInputStreams
    private long startTime = System.currentTimeMillis();
    private boolean audioIsStopped, customConfig = false;

    // Konstruktor und Multiplikator für die Tickrate
    public Utils(double osMultiplier) { this.osMultiplier = osMultiplier; }

    // Berechnet die Flugbahn des Spielers
    public int calculateGravity(int x) { return -2 * x + 4; }

    // Läd Bilddateien
    public BufferedImage reader(String resource) {
        if (!bufferedImageCache.containsKey(resource)) { // Überprüft, ob das Bild bereits geladen wurde
            try {
                if (resource.endsWith(".png")) {
                    if (customConfig & !resource.startsWith("error")) bufferedImageCache.put(resource, ImageIO.read(Files.newInputStream(Paths.get(resource))));
                         else bufferedImageCache.put(resource, ImageIO.read(Objects.requireNonNull(getClass().getResource(resource))));
                     } else throw new IllegalArgumentException("Das Bildformat wird nicht unterstützt: " + resource);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             return bufferedImageCache.get(resource); // Gibt das Bild zurück
         }

    // Erstellt ein ImageIcon aus Bildern
    public ImageIcon createImageIcon(String resource) {
        if (!imageIconCache.containsKey(resource)) { // Überprüft, ob der Pfad bereits geladen wurde
            if (resource.endsWith(".png")) {
                imageIconCache.put(resource, new ImageIcon(reader(resource))); // Erstellt ein ImageIcon
            } else if (resource.endsWith(".gif")) {
                URL imageUrl = getClass().getClassLoader().getResource(resource);
                imageIconCache.put(resource, new ImageIcon(Objects.requireNonNull(imageUrl)));
            } else throw new IllegalArgumentException("Das Bildformat wird nicht unterstützt: " + resource);
        }
        return imageIconCache.get(resource);
    }

    // Zentriert ein Bild mittig
    public Point locatePoint(String image, int width, int height) {
        BufferedImage img = reader(image);
        return new Point((width -  img.getWidth()) / 2, (height - img.getHeight()) / 2);
    }

    // Läd Musikdateien und spielt sie ab
    public void audioPlayer(String audioFilePath, boolean sound, boolean loop, Logic logic) {
        if (sound && !logic.gamePaused && !Objects.equals(audioFilePath, "error/empty.wav")) {
            if (!loop) audioIsStopped = false;
            CompletableFuture.runAsync(() -> {
                try {
                    if (HeavyClipCache.get(audioFilePath) != null) {
                        Clip clip = HeavyClipCache.get(audioFilePath);
                        clip.setFramePosition(0);
                        clip.start();
                        return;
                    }

                    ClassLoader classLoader = getClass().getClassLoader();
                    InputStream audioFileInputStream;

                    if (customConfig) audioFileInputStream = Files.newInputStream(Paths.get(audioFilePath));
                    else audioFileInputStream = classLoader.getResourceAsStream(audioFilePath);

                    // Überprüfen, ob die Audiodatei gefunden wurde
                    if (audioFileInputStream == null) throw new IllegalArgumentException("Die Audiodatei wurde nicht gefunden: " + audioFilePath);

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(audioFileInputStream);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);

                    // Lange Audiodateien werden in den Cache geladen, um die Ressourcen freizugeben
                    if (clip.getMicrosecondLength() > 1000000 || loop) {
                        HeavyBufferedInputStreamCache.add(bufferedInputStream);
                        HeavyAudioInputStreamCache.add(audioInputStream);
                        HeavyClipCache.put(audioFilePath, clip);
                    }

                    // Hinzufügen eines LineListeners, um die Ressourcen freizugeben, wenn die Wiedergabe beendet ist
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            try {
                                if (loop && logic.gameOver && !audioIsStopped) audioPlayer(audioFilePath, true, true ,logic);
                                if (!HeavyClipCache.containsKey(audioFilePath)) {
                                    clip.close();
                                    audioInputStream.close();
                                    bufferedInputStream.close();
                                }


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    clip.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Stoppt die Musik
    public void stopHeavyAudio() {
        CompletableFuture.runAsync(() -> {
            try {
                audioIsStopped = true;
                for (Clip clip : HeavyClipCache.values()) clip.stop();
                for (AudioInputStream audioInputStream : HeavyAudioInputStreamCache) audioInputStream.close();
                for (BufferedInputStream bufferedInputStream : HeavyBufferedInputStreamCache) bufferedInputStream.close();

                HeavyBufferedInputStreamCache.clear();
                HeavyAudioInputStreamCache.clear();
                HeavyClipCache.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Berechnet die Breite des Hintergrunds
    public int getBackgroundWidth(String path) {
        return bufferedImageCache.get(path).getWidth();
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

    // Zentriert das Fenster mittig auf dem Bildschirm
    public Point centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Bildschirmgröße
        return new Point(((screenSize.width - frame.getWidth()) / 2), ((screenSize.height - frame.getHeight()) / 2));
    }

    // Berechent die Breite des Fensters
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
    public long calculateSystemLatency(Logic logic) {
        long currentTime = System.currentTimeMillis(); // Aktuelle Zeit
        long latency = currentTime - startTime; // Latenz
        startTime = currentTime;
        soutLogger("latency-log.txt", String.valueOf(latency), logic);
        return latency;
    }

    // Schreibt Strings in eine Log-Datei
    public void soutLogger(String file, String message, Logic logic) {
        if (logic.developerMode) {
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
    public JsonNode readJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream;
            if (json.endsWith(".json")) {
                inputStream = Files.newInputStream(Paths.get(json));
                customConfig = true;
            } else inputStream = getClass().getResourceAsStream("config/" + json + ".json");
            if (inputStream == null) throw new IllegalArgumentException("Die Config Datei konnte nicht gefunden werden: " + json);
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Überprüft ob der 11. September ist
    public JsonNode checkDate(String Default) {
        JsonNode config;
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() == 9 && date.getDayOfMonth() == 11 ) config = readJson("911");
        else config = readJson(Default);
        return config;
    }
}