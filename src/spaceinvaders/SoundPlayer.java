package spaceinvaders;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class SoundPlayer {

    /**
     * Reproduce un efecto de sonido desde /resources/audio/.
     * @param fileName Nombre del .wav (por ejemplo, "shoot.wav")
     */
    public static void playSound(String fileName) {
        try {
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream("/resources/audio/" + fileName);
            if (audioSrc == null) {
                System.err.println("No se encontró el recurso de audio: /resources/audio/" + fileName);
                return;
            }
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}