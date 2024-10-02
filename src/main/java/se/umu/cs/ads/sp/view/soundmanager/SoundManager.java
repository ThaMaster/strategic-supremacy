package se.umu.cs.ads.sp.view.soundmanager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.HashMap;

public class SoundManager {

    private static final HashMap<String, String> sounds = new HashMap<>(); //Name, Path
    private int nextMoveSoundFx = 1;
    public SoundManager(){
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
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Sound not found: " + soundFx);
        }
    }

    public void playMove(){
        switch (nextMoveSoundFx){
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
        if(nextMoveSoundFx > 6){
            nextMoveSoundFx = 1;
        }
    }

    private void init(){
        try{
            URL audioSrc = getClass().getClassLoader().getResource("audio/chestOpen.wav");
            sounds.put(SoundFX.OPEN_CHEST, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move1.wav");
            sounds.put(SoundFX.MOVE_1, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move2.wav");
            sounds.put(SoundFX.MOVE_2, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move3.wav");
            sounds.put(SoundFX.MOVE_3, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move4.wav");
            sounds.put(SoundFX.MOVE_4, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move5.wav");
            sounds.put(SoundFX.MOVE_5, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/move6.wav");
            sounds.put(SoundFX.MOVE_6, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/gold.wav");
            sounds.put(SoundFX.GOLD, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/attack.wav");
            sounds.put(SoundFX.ATTACK, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/death.wav");
            sounds.put(SoundFX.DEATH, audioSrc != null ? audioSrc.toExternalForm() : null);

            audioSrc = getClass().getClassLoader().getResource("audio/takeDmg.wav");
            sounds.put(SoundFX.TAKE_DMG, audioSrc != null ? audioSrc.toExternalForm() : null);
        }catch(NullPointerException e){
            System.out.println("Could not load audio file");
        }
    }
}
