package de.MCmoderSD.utilities.sound;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class AudioPlayer {

    // Attributes
    private final HashMap<String, WavPlayer> wavPlayers; // AudioPath, WavPlayer
    private final ArrayList<WavPlayer> instantWavPlayers; // WavPlayer

    // Variables
    public String url;

    // Constructor
    public AudioPlayer() {
        wavPlayers = new HashMap<>();
        instantWavPlayers = new ArrayList<>();
    }

    // Constructor with url
    public AudioPlayer(String url) {
        this.url = url;
        wavPlayers = new HashMap<>();
        instantWavPlayers = new ArrayList<>();
    }

    // Load audio with loop
    public void loadAudio(String audioPath, boolean loop) {
        if (audioPath.endsWith("empty.wav")) return; // Skip empty sound
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            WavPlayer wavPlayer = new WavPlayer(audioPath, loop);
            wavPlayers.put(audioPath, wavPlayer);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Load audio
    public void loadAudio(String audioPath) {
        loadAudio(audioPath, false);
    }

    // Play audio with loop
    public void play(String audioPath, boolean loop) {
        if (audioPath.endsWith("empty.wav")) return; // Skip empty sound
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) {
                wavPlayers.get(audioPath).play();
                if (wavPlayers.get(audioPath).isPlaying()) {
                    WavPlayer wavPlayer = new WavPlayer(audioPath, loop);
                    instantWavPlayers.add(wavPlayer);
                    wavPlayer.play();
                }
            } else {
                loadAudio(audioPath, false);
                play(audioPath, loop);
            }
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Play audio
    public void play(String audioPath) {
        if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
        play(audioPath, false);
    }

    // Pause audio
    public void pause(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).pause();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Resume audio
    public void resume(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).resume();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Stop audio
    public void stop(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).stop();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Close audio
    public void close(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).close();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Toggle loop
    public void toggleLoop(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).toggleLoop();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Set loop
    public void toggleLoop(int index) {
        if (index < instantWavPlayers.size()) instantWavPlayers.get(index).toggleLoop();
        else System.err.println("Index out of bounds: " + index);
    }

    // Set loop
    public void setLoop(String audioPath, boolean loop) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).setLoop(loop);
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Set loop
    public void setLoop(int index, boolean loop) {
        if (index < instantWavPlayers.size()) instantWavPlayers.get(index).setLoop(loop);
        else System.err.println("Index out of bounds: " + index);
    }

    // Close all audio
    public void closeAll() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.close();
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.close();
    }

    // Stop all playing audio
    public void stopAll() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.stop();
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.stop();
    }

    // Pause all playing audio
    public void pauseAll() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.pause();
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.pause();
    }

    // Resume all paused audio
    public void resumeAll() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.resume();
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.resume();
    }

    public void toggleLoopAll() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.toggleLoop();
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.toggleLoop();
    }

    public void setLoopAll(boolean loop) {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.setLoop(loop);
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.setLoop(loop);
    }

    // Clear all audio
    public void clearAll() {
        wavPlayers.clear();
        instantWavPlayers.clear();
    }

    // Play all loaded audio
    public void playAllLoaded() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.play();
    }

    // Play all instant audio
    public void playAllInstant() {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.play();
    }

    // Stop all playing audio
    public void stopAllLoaded() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.stop();
    }

    // Stop all playing audio
    public void stopAllInstant() {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.stop();
    }

    // Pause all playing audio
    public void pauseAllLoaded() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.pause();
    }

    // Pause all playing audio
    public void pauseAllInstant() {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.pause();
    }

    // Resume all paused audio
    public void resumeAllLoaded() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.resume();
    }

    // Resume all paused audio
    public void resumeAllInstant() {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.resume();
    }

    // Toggle loop for all loaded audio
    public void toggleLoopAllLoaded() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.toggleLoop();
    }

    // Toggle loop for all instant audio
    public void toggleLoopAllInstant() {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.toggleLoop();
    }

    // Set loop for all loaded audio
    public void setLoopAllLoaded(boolean loop) {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.setLoop(loop);
    }

    // Set loop for all instant audio
    public void setLoopAllInstant(boolean loop) {
        for (WavPlayer wavPlayer : instantWavPlayers) wavPlayer.setLoop(loop);
    }

    // Clear all instant audio
    public void clearInstant() {
        instantWavPlayers.clear();
    }

    // Clear all loaded audio
    public void clearLoaded() {
        wavPlayers.clear();
    }

    // Clear audio from wavPlayers
    public void clear(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) wavPlayers.remove(audioPath);
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Adds wavPlayer to wavPlayers
    public void add(String audioPath, WavPlayer wavPlayer) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (!wavPlayers.containsKey(audioPath)) wavPlayers.put(audioPath, wavPlayer);
            else System.err.println("Audio already loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    // Adds wavPlayer to instantWavPlayers
    public void add(WavPlayer wavPlayer) {
        instantWavPlayers.add(wavPlayer);
    }

    // Removes wavPlayer from wavPlayers
    public HashMap<String, WavPlayer> getWavPlayers() {
        return wavPlayers;
    }

    // Returns instantWavPlayers
    public ArrayList<WavPlayer> getInstantWavPlayers() {
        return instantWavPlayers;
    }

    // Returns wavPlayer from wavPlayers
    public WavPlayer getWavPlayer(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) return wavPlayers.get(audioPath);
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
        return null;
    }

    // Returns wavPlayer from instantWavPlayers
    public WavPlayer getWavPlayer(int index) {
        if (index < instantWavPlayers.size()) return instantWavPlayers.get(index);
        else System.err.println("Index out of bounds: " + index);
        return null;
    }

    // Returns true if wavPlayer is playing
    public boolean isPlaying(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) return wavPlayers.get(audioPath).isPlaying();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
        return false;
    }

    // Returns true if wavPlayer is playing
    public boolean isPlaying(int index) {
        if (index < instantWavPlayers.size()) return instantWavPlayers.get(index).isPlaying();
        else System.err.println("Index out of bounds: " + index);
        return false;
    }

    public boolean isLoop(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) return wavPlayers.get(audioPath).isLoop();
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
        return false;
    }

    public boolean isLoop(int index) {
        if (index < instantWavPlayers.size()) return instantWavPlayers.get(index).isLoop();
        else System.err.println("Index out of bounds: " + index);
        return false;
    }

    // Returns true if wavPlayers contains wavPlayer
    public boolean isContained(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (url != null && !audioPath.startsWith(url)) audioPath = url + audioPath;
            if (wavPlayers.containsKey(audioPath)) return true;
            else System.err.println("Audio not loaded: " + audioPath);
        } else System.err.println("Unsupported file format: " + audioPath);
        return false;
    }

    // Returns true if instantWavPlayers contains wavPlayer
    public boolean isContained(int index) {
        if (index < instantWavPlayers.size()) return true;
        else System.err.println("Index out of bounds: " + index);
        return false;
    }

    // Returns the size of wavPlayers + instantWavPlayers
    public int size() {
        return wavPlayers.size() + instantWavPlayers.size();
    }

    // Returns the size of wavPlayers
    public int sizeLoaded() {
        return wavPlayers.size();
    }

    // Returns the size of instantWavPlayers
    public int sizeInstant() {
        return instantWavPlayers.size();
    }

    // Returns the url
    public String getUrl() {
        return url;
    }

    // Set url
    public void setUrl(String url) {
        this.url = url;
    }
}