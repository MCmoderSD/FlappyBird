package de.MCmoderSD.utilities.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class WavPlayer {

    // Attributes
    private boolean loop;
    private Clip clip;

    // Constructor
    public WavPlayer(String audioPath, boolean loop) {
        this.loop = loop;
        loadClip(audioPath);
    }

    // Loader
    private void loadClip(String audioPath) {
        try {
            InputStream audioSrc;
            if (audioPath.startsWith("http") || new File(audioPath).isAbsolute())
                audioSrc = new URL(audioPath).openStream();
            else audioSrc = getClass().getResourceAsStream(audioPath); // Relative path

            // Add buffer for mark/reset support
            InputStream bufferedIn = new BufferedInputStream(Objects.requireNonNull(audioSrc));
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            AudioFormat baseFormat = audioStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream decodedAudioStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);

            clip = AudioSystem.getClip();
            clip.open(decodedAudioStream);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println(e.getMessage());
        }
    }

    // Play clip
    public void play() {
        if (clip == null) return;
        if (clip.isRunning()) {
            clip.stop(); // Stop the clip before resetting it
        }
        clip.setFramePosition(0);
        clip.start();
    }

    // Pause clip
    public void pause() {
        if (clip == null) return;
        clip.stop();
    }

    // Resume clip
    public void resume() {
        if (clip == null) return;
        clip.start();
    }

    // Stop clip
    public void stop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
    }

    // Close clip
    public void close() {
        stop();
        clip.close();
    }

    // Toggle loop
    public void toggleLoop() {
        loop = !loop;
    }

    // Check if clip is running
    public boolean isPlaying() {
        if (clip == null) return false;
        return clip.isRunning();
    }

    // Check if clip is looping
    public boolean isLoop() {
        return loop;
    }

    // Set loop
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}