package systems;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AudioSystem {
    private static AudioSystem instance;

    // Sound effects
    private final Map<String, Clip> soundEffects = new HashMap<>();

    // Music
    private Clip currentMusic;
    private String currentMusicName;

    // Volume (0.0 - 1.0)
    private float musicVolume = 0.7f;
    private float sfxVolume = 0.8f;

    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

    private AudioSystem() {
        loadAllAudio();
    }

    public static AudioSystem getInstance() {
        if (instance == null) {
            instance = new AudioSystem();
        }
        return instance;
    }

    // ---------------------------------------------------------
    // LOAD AUDIO FILES (WAV ONLY)
    // ---------------------------------------------------------
    private void loadAllAudio() {
        loadSound("interact", "sounds/Interact.wav");
        loadSound("machine_complete", "sounds/MachineComplete.wav");
        loadSound("printing", "sounds/Printing.wav");
        loadSound("cooking", "sounds/Cooking.wav");
        loadSound("filling", "sounds/Filling.wav");
        loadSound("customer", "sounds/Customer.wav");
        loadSound("day_clear", "sounds/DayClear.wav");
        loadSound("day_start", "sounds/DayStart.wav");
        loadSound("intro", "sounds/IntroScene.wav");
        loadSound("game_incomplete", "sounds/GameIncomplete.wav");
        loadSound("game_complete", "sounds/GameComplete.wav");
    }

    private void loadSound(String name, String path) {
        try {
            File soundFile = new File("assets/" + path);

            if (!soundFile.exists()) {
                System.out.println("Sound not found: " + path);
                return;
            }

            AudioInputStream stream =
                    javax.sound.sampled.AudioSystem.getAudioInputStream(soundFile);

            Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(stream);

            soundEffects.put(name, clip);

        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
        }
    }

    // ---------------------------------------------------------
    // SOUND EFFECTS
    // ---------------------------------------------------------
    public void playSoundEffect(String soundName) {
        if (!sfxEnabled) return;

        Clip clip = soundEffects.get(soundName);
        if (clip == null) return;

        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        setVolume(clip, sfxVolume);
        clip.start();
    }

    public void playLoopingSound(String soundName) {
        if (!sfxEnabled) return;

        Clip clip = soundEffects.get(soundName);
        if (clip == null) return;

        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        setVolume(clip, sfxVolume);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopLoopingSound(String soundName) {
        Clip clip = soundEffects.get(soundName);
        if (clip != null && clip.isRunning()) clip.stop();
    }

    // ---------------------------------------------------------
    // MUSIC (WAV ONLY)
    // ---------------------------------------------------------
    public void playMusic(String musicName) {
        if (!musicEnabled) return;

        if (musicName.equals(currentMusicName) &&
                currentMusic != null && currentMusic.isRunning()) {
            return; // Already playing
        }

        stopMusic();

        try {
            File musicFile = new File("assets/music/" + musicName + ".wav");
            if (!musicFile.exists()) {
                System.out.println("Music not found: " + musicName);
                return;
            }

            AudioInputStream stream =
                    javax.sound.sampled.AudioSystem.getAudioInputStream(musicFile);

            currentMusic = javax.sound.sampled.AudioSystem.getClip();
            currentMusic.open(stream);

            setVolume(currentMusic, musicVolume);
            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);

            currentMusicName = musicName;

        } catch (Exception e) {
            System.err.println("Failed to play music: " + musicName);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.close();
            currentMusic = null;
        }
        currentMusicName = null;
    }

    // ---------------------------------------------------------
    // VOLUME CONTROL
    // ---------------------------------------------------------
    private void setVolume(Clip clip, float volume) {
        if (clip == null || !clip.isOpen()) return;

        try {
            FloatControl ctrl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float dB = (float) (Math.log(volume) / Math.log(10) * 20);
            ctrl.setValue(dB);

        } catch (Exception ignored) {}
    }

    public void setMusicVolume(float v) {
        musicVolume = Math.max(0f, Math.min(1f, v));
        if (currentMusic != null) setVolume(currentMusic, musicVolume);
    }

    public void setSFXVolume(float v) {
        sfxVolume = Math.max(0f, Math.min(1f, v));
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) stopMusic();
    }

    public void setSFXEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    // ---------------------------------------------------------
    // SHORTCUT METHODS
    // ---------------------------------------------------------
    public void playInteract()           { playSoundEffect("interact"); }
    public void playMachineComplete()    { playSoundEffect("machine_complete"); }
    public void playCustomerArrive()     { playSoundEffect("customer"); }
    public void playDayClear()           { playSoundEffect("day_clear"); }
    public void playDayStart()           { playSoundEffect("day_start"); }
    public void playIntro()              { playSoundEffect("intro"); }
    public void playGameIncomplete()     { playSoundEffect("game_incomplete"); }
    public void playGameComplete()       { playSoundEffect("game_complete"); }

    public void startPrinting()          { playLoopingSound("printing"); }
    public void stopPrinting()           { stopLoopingSound("printing"); }

    public void startCooking()           { playLoopingSound("cooking"); }
    public void stopCooking()            { stopLoopingSound("cooking"); }

    public void startFilling()           { playLoopingSound("filling"); }
    public void stopFilling()            { stopLoopingSound("filling"); }

    public void playMenuMusic()          { playMusic("MenuScene"); }
    public void playLobbyMusic()         { playMusic("LobbyScene"); }
    public void playGameplay1Music()     { playMusic("Gameplay1"); }
    public void playGameplay2Music()     { playMusic("Gameplay2"); }
    public void playGameplay3Music()     { playMusic("Gameplay3"); }
    public void playGameCompletedMusic() { playMusic("GameCompleted"); }

    // ---------------------------------------------------------
    // CLEANUP
    // ---------------------------------------------------------
    public void cleanup() {
        stopMusic();
        for (Clip clip : soundEffects.values()) {
            try {
                clip.stop();
                clip.close();
            } catch (Exception ignored) {}
        }
        soundEffects.clear();
    }
}
