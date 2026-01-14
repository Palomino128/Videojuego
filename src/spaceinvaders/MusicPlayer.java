package spaceinvaders;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.BufferedInputStream;

/**
 * Clase para reproducir efectos de sonido y música de fondo.
 * Maneja la carga y reproducción de archivos WAV desde /resources/audio/
 */
public class MusicPlayer {
    
    // Objeto Clip para controlar la reproducción de audio
    private Clip clip;
    
    /**
     * Reproduce un archivo de audio con opción de loop.
     * @param fileName Nombre del archivo en /resources/audio/ (ej: "menu.wav")
     * @param loop true para reproducción continua, false para una sola vez
     */
    public void playMusic(String fileName, boolean loop) {
        // 1. Detener reproducción actual si existe
        stop();
        
        try {
            // 2. Cargar archivo de audio desde recursos
            InputStream audioSrc = getClass().getResourceAsStream("/resources/audio/" + fileName);
            
            // 3. Verificar si el archivo existe
            if (audioSrc == null) {
                System.err.println("Audio no encontrado: /resources/audio/" + fileName);
                return;
            }
            
            // 4. Crear flujo de audio con buffer para mejor rendimiento
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            // 5. Obtener y abrir recurso de audio
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // 6. Configurar reproducción (con o sin loop)
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir infinitamente
            } else {
                clip.start(); // Reproducir una vez
            }
            
        } catch (Exception e) {
            // 7. Manejo de errores durante la carga/reproducción
            System.err.println("Error al reproducir " + fileName + ": ");
            e.printStackTrace();
        }
    }
    
    /**
     * Detiene la reproducción actual y libera recursos.
     */
    public void stop() {
        // 1. Verificar si hay clip activo
        if (clip != null) {
            
            // 2. Si está sonando, detenerlo
            if (clip.isRunning()) {
                clip.stop();
            }
            
            // 3. Liberar recursos del clip
            clip.close();
            
            // 4. Eliminar referencia
            clip = null;
        }
    }
    
    /**
     * Ajusta el volumen del audio (opcional).
     * @param decibels Nivel de volumen (negativo para reducir)
     */
    public void setVolume(float decibels) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decibels); // Ej: -10.0f para reducir volumen
        }
    }
    
    /**
     * Verifica si hay audio reproduciéndose.
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}