package de.MCmoderSD.utilities.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings("unused")
public class WavPlayer {

    // Attributes
    private Clip clip;
    private boolean loop = false;

    // Constructors
    public WavPlayer(String audioPath) {
        loadClip(audioPath);
    }

    public WavPlayer(String audioPath, boolean isAbsolutePath) {
        if (isAbsolutePath) loadClipFromAbsolutePath(audioPath);
        else loadClip(audioPath);
    }

    public WavPlayer(String audioPath, boolean isAbsolutePath, boolean loop) {
        this.loop = loop;
        if (isAbsolutePath) loadClipFromAbsolutePath(audioPath);
        else loadClip(audioPath);
    }

    // Methods
    private void loadClip(String audioPath) {
        new Thread(() -> {
            try {
                if (audioPath.startsWith("http://") || audioPath.startsWith("https://")) {
                    URL url = new URL(audioPath);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                    AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
                    AudioInputStream convertedInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                    clip = AudioSystem.getClip();
                    clip.open(convertedInputStream);
                } else {
                    InputStream resourceStream = getClass().getResourceAsStream(audioPath);
                    if (resourceStream != null) {
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(resourceStream));
                        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
                        AudioInputStream convertedInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                        clip = AudioSystem.getClip();
                        clip.open(convertedInputStream);
                    } else System.err.println("File not found: " + audioPath);
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }

    private void loadClipFromAbsolutePath(String absolutePath) {
        new Thread(() -> {
            try {
                File file = new File(absolutePath);
                if (file.exists()) {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
                    AudioInputStream convertedInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                    clip = AudioSystem.getClip();
                    clip.open(convertedInputStream);
                } else System.err.println("File not found: " + absolutePath);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println(e.getMessage());
            }
        }).start();

    }

    public void play() {
        new Thread(() -> {
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
                if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }).start();
    }

    public void play(boolean loop) {
        new Thread(() -> {
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            else clip.start();
        }).start();
    }

    public void pause() {
        if (clip != null && clip.isRunning()) clip.stop();
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) clip.start();
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
        this.loop = loop;
    }
}
