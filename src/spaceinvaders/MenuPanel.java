package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Panel del menú principal del juego Space Invaders.
 * Muestra animaciones de estrellas, logo del juego y botón de inicio.
 */
public class MenuPanel extends JPanel implements ActionListener {

    // Temporizador para controlar la animación de estrellas
    private final Timer timer;

    // Array de 100 estrellas para el fondo animado
    private final Star[] stars = new Star[100];

    // Imágenes del menú
    private BufferedImage btnStartImg;  // Imagen del botón "Start"
    private BufferedImage backgroundImg; // Fondo del menú (opcional)

    // Reproductor de música (única instancia para evitar eco)
    private static MusicPlayer musicPlayer;

    // Constructor: Configura el panel del menú
    public MenuPanel() {
        // Configuración básica del JPanel
        setPreferredSize(new Dimension(800, 600)); // Tamaño de la ventana
        setLayout(null); // Layout absoluto (para posicionar elementos manualmente)
        setDoubleBuffered(true); // Mejor rendimiento gráfico

        // Inicializar música (evitar acumulación con 'stop()' previo)
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        musicPlayer = new MusicPlayer();
        musicPlayer.playMusic("menu.wav", true); // Reproducir en bucle

        // Inicializar estrellas en posiciones aleatorias
        Random rand = new Random();
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(
                rand.nextInt(800), // Posición X aleatoria (0-800)
                rand.nextInt(600), // Posición Y aleatoria (0-600)
                rand.nextInt(3) + 1 // Velocidad aleatoria (1-3)
            );
        }

        // Cargar imagen del botón "Start"
        try {
            btnStartImg = ImageIO.read(getClass().getResource("/resources/btn_start.png"));
        } catch (IOException e) {
            System.err.println("Error cargando botón: " + e.getMessage());
        }

        // Cargar fondo del menú (opcional)
        try {
            backgroundImg = ImageIO.read(getClass().getResource("/resources/menu_background.png"));
        } catch (IOException e) {
            System.out.println("Fondo estático no cargado.");
        }

        // Configurar logo del juego
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
            // Escalar el logo a 500x300 píxeles
            Image scaledLogo = logoIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledLogo);

            JLabel logoLabel = new JLabel(logoIcon);
            // Posicionar el logo centrado horizontalmente
            int logoX = (800 - logoIcon.getIconWidth()) / 2;
            logoLabel.setBounds(logoX, 100, logoIcon.getIconWidth(), logoIcon.getIconHeight());
            add(logoLabel);
        } catch (Exception e) {
            System.err.println("Error cargando logo: " + e.getMessage());
        }

        // Configurar botón de inicio
        JButton startButton = new JButton();
        if (btnStartImg != null) {
            // Botón con imagen personalizada
            startButton.setIcon(new ImageIcon(btnStartImg));
            startButton.setBorderPainted(false); // Sin borde
            startButton.setContentAreaFilled(false); // Sin fondo
            startButton.setFocusPainted(false); // Sin efecto de foco
        } else {
            // Fallback: Botón con texto si no carga la imagen
            startButton.setText("Empezar Juego");
        }

        // Posicionar botón en la parte inferior centrado
        startButton.setBounds(325, 400, 150, 50);
        // Acción al hacer clic: Iniciar el juego
        startButton.addActionListener(e -> Game.startGame());
        add(startButton);

        // Iniciar temporizador para animación (30 FPS)
        timer = new Timer(30, this);
        timer.start();
    }

    /**
     * Detiene la música del menú desde otras clases.
     */
    public static void stopMenuMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    /**
     * Dibuja los elementos gráficos del menú.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar fondo estático si existe
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fondo negro si no hay imagen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Dibujar estrellas animadas (blancas, 2x2 píxeles)
        g.setColor(Color.WHITE);
        for (Star star : stars) {
            g.fillRect(star.x, star.y, 2, 2);
        }
    }

    /**
     * Actualiza la animación de estrellas (se ejecuta cada 30ms).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for (Star star : stars) {
            star.y += star.speed; // Mover estrella hacia abajo
            
            // Reiniciar estrella en la parte superior si sale de la pantalla
            if (star.y > 600) {
                star.y = 0;
                star.x = new Random().nextInt(800);
            }
        }
        repaint(); // Redibujar el panel
    }

    /**
     * Clase interna que representa una estrella en el fondo animado.
     */
    private static class Star {
        int x, y; // Posición
        int speed; // Velocidad de caída

        Star(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
}