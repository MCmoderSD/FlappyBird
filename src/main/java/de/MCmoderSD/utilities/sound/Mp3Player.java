package de.MCmoderSD.utilities.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Mp3Player {
    private Clip clip;
    private boolean isLooping = false;

    public Mp3Player(String audioPath) {
        loadClip(audioPath);
    }

    public Mp3Player(String audioPath, boolean isAbsolutePath) {
        if (isAbsolutePath) {
            loadClipFromAbsolutePath(audioPath);
        } else {
            loadClip(audioPath);
        }
    }

    public Mp3Player(String audioPath, boolean isAbsolutePath, boolean loop) {
        this(audioPath, isAbsolutePath);
        isLooping = loop;
    }

    private void loadClip(String audioPath) {
        new Thread(() -> {
            try {
                // Erstellen Sie die URL zur Ressourcen-Datei
                URL url = getClass().getResource("/" + audioPath); // Beachten Sie das "/" am Anfang

                if (url != null) {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                } else {
                    System.err.println("Die Datei " + audioPath + " wurde nicht gefunden.");
                }
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }


    private void loadClipFromAbsolutePath(String absolutePath) {
        new Thread(() -> {
            try {
                File file = new File(absolutePath);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.err.println(e.getMessage());
            }
        }).start();

    }

    public void play() {
        new Thread(() -> {
            if (clip != null) {
                if (isLooping) {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    clip.setFramePosition(0);
                    clip.start();
                }
            }
        }).start();
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void setLoop(boolean loop) {
        isLooping = loop;
        if (clip != null && isLooping) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}