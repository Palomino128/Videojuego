package spaceinvaders;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal que controla la ventana del juego y la navegación entre pantallas.
 * Configura el JFrame principal y maneja las transiciones entre:
 * - Menú principal
 * - Juego
 * - Pantalla de Game Over
 * - Pantalla de Victoria
 */
public class Game {
    
    // Ventana principal del juego (static para acceso global)
    public static JFrame frame;
    public static int currentLevel;

    /**
     * Punto de entrada principal del juego.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Mostrar el menú principal al iniciar la aplicación
        showMenu();
    }

    /**
     * Muestra la pantalla del menú principal con configuración fija.
     * - Crea la ventana si no existe
     * - Configura propiedades de visualización
     * - Inicia la música de fondo
     */
    public static void showMenu() {
        // Inicializar ventana solo una vez
        if (frame == null) {
            frame = new JFrame("Space Invaders");
            
            // Configuración de ventana no redimensionable
            frame.setResizable(false); // Bloquea cambiar tamaño
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra proceso al salir
            
            // Tamaño fijo (800x600 es el estándar para este juego)
            frame.setSize(800, 600);
            frame.setPreferredSize(new Dimension(800, 600));
            
            // Centrar ventana en la pantalla
            frame.setLocationRelativeTo(null);
            
            // Bloquear tamaño mínimo/máximo (evita cambios)
            frame.setMinimumSize(new Dimension(800, 600));
            frame.setMaximumSize(new Dimension(800, 600));
        }

        // Detener música de otras pantallas si está sonando
        MenuPanel.stopMenuMusic();
        
        // Configurar contenido del menú
        frame.getContentPane().removeAll(); // Limpiar contenido anterior
        MenuPanel menu = new MenuPanel(); // Crear nuevo panel de menú
        frame.setContentPane(menu); // Establecer como contenido principal
        
        // Ajustar ventana al contenido
        frame.pack();
        
        // Hacer visible y enfocar
        frame.setVisible(true);
        menu.requestFocusInWindow(); // Permite interacción con teclado
    }

    /**
     * Inicia el juego principal.
     * - Detiene música del menú
     * - Crea nueva instancia del juego
     * - Configura el panel de juego
     */
    public static void startGame() {
        // Detener música del menú
        MenuPanel.stopMenuMusic();
        
        // Configurar panel del juego
        frame.getContentPane().removeAll();
        SpaceInvaders game = new SpaceInvaders(); // Lógica principal del juego
        frame.setContentPane(game);
        
        // Actualizar interfaz
        frame.revalidate();
        frame.repaint();
        
        // Enfocar el juego para controles
        game.requestFocusInWindow();
    }

    /**
     * Muestra la pantalla de Game Over.
     * - Detiene música del juego
     * - Muestra pantalla de derrota
     */
    public static void showGameOver() {
        frame.getContentPane().removeAll();
        GameOverPanel gameOverPanel = new GameOverPanel();
        frame.setContentPane(gameOverPanel);
        
        // Ajustar tamaño y centrar
        frame.pack();
        frame.setLocationRelativeTo(null);
        
        // Enfocar el panel
        gameOverPanel.requestFocusInWindow();
    }

    /**
     * Muestra la pantalla de victoria.
     * - Detiene música del juego
     * - Muestra pantalla de éxito
     */
    public static void showVictory() {
        frame.getContentPane().removeAll();
        VictoryPanel victory = new VictoryPanel();
        frame.setContentPane(victory);
        
        // Actualizar interfaz
        frame.revalidate();
        frame.repaint();
        
        // Enfocar el panel
        victory.requestFocusInWindow();
    }
}