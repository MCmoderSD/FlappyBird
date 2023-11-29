package de.MCmoderSD.utilities.sound;

import java.util.HashMap;

@SuppressWarnings("unused")
public class AudioPlayer {

    // Attributes
    private final HashMap<String, WavPlayer> wavPlayers;
    private final HashMap<String, Mp3Player> mp3Players;

    // Constructor
    public AudioPlayer() {
        wavPlayers = new HashMap<>();
        mp3Players = new HashMap<>();
    }


    // Instant Play
    public void instantPlay(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            if (isPlaying(audioPath)) new WavPlayer(audioPath).play();
            else {
                WavPlayer wavPlayer = new WavPlayer(audioPath);
                wavPlayer.play();
                wavPlayers.put(audioPath, wavPlayer);
            }
        } else if (audioPath.endsWith(".mp3")) {
            if (isPlaying(audioPath)) new Mp3Player(audioPath).play();
            else {
                Mp3Player mp3Player = new Mp3Player(audioPath);
                mp3Player.play();
                mp3Players.put(audioPath, mp3Player);
            }
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    public void instantPlay(String audioPath, boolean isAbsolutePath) {
        if (audioPath.endsWith(".wav")) {
            if (isPlaying(audioPath)) new WavPlayer(audioPath).play();
            else {
                WavPlayer wavPlayer = new WavPlayer(audioPath, isAbsolutePath);
                wavPlayer.play();
                wavPlayers.put(audioPath, wavPlayer);
            }
        } else if (audioPath.endsWith(".mp3")) {
            if (isPlaying(audioPath)) new Mp3Player(audioPath).play();
            else {
                Mp3Player mp3Player = new Mp3Player(audioPath, isAbsolutePath);
                mp3Player.play();
                mp3Players.put(audioPath, mp3Player);
            }
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    public void instantPlay(String audioPath, boolean isAbsolutePath, boolean loop) {
        if (audioPath.endsWith(".wav")) {
            if (isPlaying(audioPath)) new WavPlayer(audioPath).play();
            else {
                WavPlayer wavPlayer = new WavPlayer(audioPath, isAbsolutePath, loop);
                wavPlayer.play();
                wavPlayers.put(audioPath, wavPlayer);
            }
        } else if (audioPath.endsWith(".mp3")) {
            if (isPlaying(audioPath)) new Mp3Player(audioPath).play();
            else {
                Mp3Player mp3Player = new Mp3Player(audioPath, isAbsolutePath, loop);
                mp3Player.play();
                mp3Players.put(audioPath, mp3Player);
            }
        } else System.err.println("Unsupported file format: " + audioPath);
    }


    // Load Audio
    public void loadAudio(String audioPath) {
        if (audioPath.endsWith(".wav")) {
            WavPlayer wavPlayer = new WavPlayer(audioPath);
            wavPlayers.put(audioPath, wavPlayer);
        } else if (audioPath.endsWith(".mp3")) {
            Mp3Player mp3Player = new Mp3Player(audioPath);
            mp3Players.put(audioPath, mp3Player);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    public void loadAudio(String audioPath, boolean isAbsolutePath) {
        if (audioPath.endsWith(".wav")) {
            WavPlayer wavPlayer = new WavPlayer(audioPath, isAbsolutePath);
            wavPlayers.put(audioPath, wavPlayer);
        } else if (audioPath.endsWith(".mp3")) {
            Mp3Player mp3Player = new Mp3Player(audioPath, isAbsolutePath);
            mp3Players.put(audioPath, mp3Player);
        } else System.err.println("Unsupported file format: " + audioPath);
    }

    public void loadAudio(String audioPath, boolean isAbsolutePath, boolean loop) {
        if (audioPath.endsWith(".wav")) {
            WavPlayer wavPlayer = new WavPlayer(audioPath, isAbsolutePath, loop);
            wavPlayers.put(audioPath, wavPlayer);
        } else if (audioPath.endsWith(".mp3")) {
            Mp3Player mp3Player = new Mp3Player(audioPath, isAbsolutePath, loop);
            mp3Players.put(audioPath, mp3Player);
        } else System.err.println("Unsupported file format: " + audioPath);
    }


    // Play Audio
    public void playAudio(String audioPath) {
        if (isPlaying(audioPath)) instantPlay(audioPath);
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).play();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).play();
        else System.err.println("Audio not found: " + audioPath);
    }

    public void playAudio(String audioPath, boolean isLooping) {
        if (isPlaying(audioPath)) instantPlay(audioPath);
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).play();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).play();
        else System.err.println("Audio not found: " + audioPath);
    }

    public void playAudio(String audioPath, boolean isLooping, boolean isAbsolutePath) {
        if (isPlaying(audioPath)) instantPlay(audioPath);
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).play();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).play();
        else System.err.println("Audio not found: " + audioPath);
    }


    // Pause Audio
    public void pauseAudio(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).pause();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).pause();
        else System.err.println("Audio not found: " + audioPath);
    }


    // Resume Audio
    public void resumeAudio(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).resume();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).resume();
        else System.err.println("Audio not found: " + audioPath);
    }


    // Stop Audio
    public void stopAudio(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).stop();
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).stop();
        else System.err.println("Audio not found: " + audioPath);
    }


    // Set Loop
    public void setLoop(String audioPath, boolean isLooping) {
        if (wavPlayers.containsKey(audioPath)) wavPlayers.get(audioPath).setLoop(isLooping);
        else if (mp3Players.containsKey(audioPath)) mp3Players.get(audioPath).setLoop(isLooping);
        else System.err.println("Audio not found: " + audioPath);
    }


    // Is Playing
    public boolean isPlaying(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) return wavPlayers.get(audioPath).isPlaying();
        else if (mp3Players.containsKey(audioPath)) return mp3Players.get(audioPath).isPlaying();
        else {
            System.err.println("Audio not found: " + audioPath);
            return false;
        }
    }

    // Getter
    public HashMap<String, WavPlayer> getWavPlayers() {
        return wavPlayers;
    }

    public HashMap<String, Mp3Player> getMp3Players() {
        return mp3Players;
    }

    public WavPlayer getWavPlayer(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) {
            return wavPlayers.get(audioPath);
        } else {
            WavPlayer wavPlayer = new WavPlayer(audioPath);
            wavPlayers.put(audioPath, wavPlayer);
            return wavPlayer;
        }
    }

    public Mp3Player getMp3Player(String audioPath) {
        if (mp3Players.containsKey(audioPath)) {
            return mp3Players.get(audioPath);
        } else {
            Mp3Player mp3Player = new Mp3Player(audioPath);
            mp3Players.put(audioPath, mp3Player);
            return mp3Player;
        }
    }

    public boolean containsWavPlayer(String audioPath) {
        return wavPlayers.containsKey(audioPath);
    }

    public boolean containsMp3Player(String audioPath) {
        return mp3Players.containsKey(audioPath);
    }

    public boolean containsPlayer(String audioPath) {
        return wavPlayers.containsKey(audioPath) || mp3Players.containsKey(audioPath);
    }

    // Setter
    public void setWavPlayer(String audioPath, WavPlayer wavPlayer) {
        wavPlayers.put(audioPath, wavPlayer);
    }

    public void setMp3Player(String audioPath, Mp3Player mp3Player) {
        mp3Players.put(audioPath, mp3Player);
    }

    // Remove
    public void removePlayer(String audioPath) {
        if (wavPlayers.containsKey(audioPath)) removeWavPlayer(audioPath);
        else if (mp3Players.containsKey(audioPath)) removeMp3Player(audioPath);
        else System.err.println("Audio not found: " + audioPath);
    }

    public void removeWavPlayer(String audioPath) {
        wavPlayers.get(audioPath).stop();
        wavPlayers.remove(audioPath);
    }

    public void removeMp3Player(String audioPath) {
        mp3Players.get(audioPath).stop();
        mp3Players.remove(audioPath);
    }

    public void removeAllWavPlayers() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.stop();
        wavPlayers.clear();
    }

    public void removeAllMp3Players() {
        for (Mp3Player mp3Player : mp3Players.values()) mp3Player.stop();
        mp3Players.clear();
    }

    public void removeAllPlayers() {
        removeAllWavPlayers();
        removeAllMp3Players();
    }

    // Pause
    public void pauseAllWav() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.pause();
    }

    public void pauseAllMp3() {
        for (Mp3Player mp3Player : mp3Players.values()) mp3Player.pause();
    }

    public void pauseAll() {
        pauseAllWav();
        pauseAllMp3();
    }

    // Resume
    public void resumeWav() {
        for (WavPlayer wavPlayer : wavPlayers.values()) wavPlayer.pause();
    }

    public void resumeMp3() {
        for (Mp3Player mp3Player : mp3Players.values()) mp3Player.pause();
    }

    public void resumeAll() {
        resumeWav();
        resumeMp3();
    }
}
