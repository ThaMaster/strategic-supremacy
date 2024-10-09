package se.umu.cs.ads.sp.view.soundmanager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import java.util.HashMap;

public class SoundManager {

    private static SoundManager instance;
    private static final HashMap<String, String> sounds = new HashMap<>(); //Name, Path
    private float globalVolume = 75.0f;
    private float musicVolume = 75.0f;
    private float sfxVolume = 100.0f;
    private int nextMoveSoundFx = 1;

    // Background music
    private Clip musicClip;

    public SoundManager() {
        init();
    }

    public void play(String soundFx) {
        String soundPath = sounds.get(soundFx);
        if (soundPath != null) {
            try {
                // Play sound using Clip from AudioSystem
                URL soundURL = new URL(soundPath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Adjust the volume of the sound
                adjustVolume(clip, globalVolume, sfxVolume);

                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Sound not found: " + soundFx);
        }
    }

    // Play background music that loops indefinitely
    public void playMusic() {
        try {
            if (musicClip != null && musicClip.isRunning()) {
                musicClip.stop(); // Stop current music if any
            }

            // Play music using Clip from AudioSystem
            URL musicURL = getClass().getClassLoader().getResource("audio/music/backgroundMusic.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicURL);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);

            // Set the global volume for music
            adjustVolume(musicClip, globalVolume, musicVolume);

            // Loop the music indefinitely
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playMove() {
        switch (nextMoveSoundFx) {
            case 1:
                play(SoundFX.MOVE_1);
                break;
            case 2:
                play(SoundFX.MOVE_2);
                break;
            case 3:
                play(SoundFX.MOVE_3);
                break;

            case 4:
                play(SoundFX.MOVE_4);
                break;

            case 5:
                play(SoundFX.MOVE_5);
                break;

            case 6:
                play(SoundFX.MOVE_6);
                break;
        }

        nextMoveSoundFx++;
        if (nextMoveSoundFx > 6) {
            nextMoveSoundFx = 1;
        }
    }

    // Adjust the volume of the current clip
    private void adjustVolume(Clip clip, float global, float additional) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float newVolume;
        if (global == 0 || additional == 0) {
            newVolume = volumeControl.getMinimum(); // Mute
        } else {
            // Convert from percentage to dB
            newVolume = (float) ((float) (global / 100.0) * (additional / 100.0) * (6.0 - (-80.0)) - 80.0);
        }
        volumeControl.setValue(newVolume);
    }

    // Set the global volume (0 to 100)
    public void setVolume(float globalVolume, float musicVolume, float sfxVolume) {
        this.globalVolume = clamp(globalVolume, 0, 100);
        this.musicVolume = clamp(musicVolume, 0, 100);
        this.sfxVolume = clamp(sfxVolume, 0, 100);

        // Adjust the volume for currently playing music
        if (musicClip != null && musicClip.isRunning()) {
            adjustVolume(musicClip, globalVolume, musicVolume);
        }
    }

    // Helper method to clamp values between min and max
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private void init() {
        try {
            URL audioSrc = getClass().getClassLoader().getResource("audio/sfx/chestOpen.wav");
            sounds.put(SoundFX.OPEN_CHEST, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move1.wav");
            sounds.put(SoundFX.MOVE_1, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move2.wav");
            sounds.put(SoundFX.MOVE_2, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move3.wav");
            sounds.put(SoundFX.MOVE_3, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move4.wav");
            sounds.put(SoundFX.MOVE_4, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move5.wav");
            sounds.put(SoundFX.MOVE_5, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/move6.wav");
            sounds.put(SoundFX.MOVE_6, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/gold.wav");
            sounds.put(SoundFX.GOLD, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/attack.wav");
            sounds.put(SoundFX.ATTACK, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/death.wav");
            sounds.put(SoundFX.DEATH, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/sfx/takeDmg.wav");
            sounds.put(SoundFX.TAKE_DMG, audioSrc != null ? audioSrc.toExternalForm() : null);

        } catch (NullPointerException e) {
            System.out.println("Could not load audio file");
        }
    }

    public int getGlobalVolume() {
        return (int) globalVolume;
    }

    public int getMusicVolume() {
        return (int) musicVolume;
    }

    public int getSFXVolume() {
        return (int) sfxVolume;
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
}
