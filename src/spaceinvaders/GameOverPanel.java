package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameOverPanel extends JPanel {
    private BufferedImage backgroundImg;
    private final MusicPlayer musicPlayer = new MusicPlayer();
    private MusicPlayer player;
    
public GameOverPanel() {
    setPreferredSize(new Dimension(800, 600));
    setLayout(null);
    player = new MusicPlayer();
    
    // Detener música previa y reproducir "Game Over" una vez
    player.stop(); // 👈 Detiene cualquier música activa
    if(Game.currentLevel == 10) {
            SoundPlayer.playSound("laugh_finalBoss.wav");
        } else {
            musicPlayer.playMusic("Game_Over.wav", false);
        } // Suena una vez
        try {
            backgroundImg = ImageIO.read(getClass().getResource("/resources/Game_over.png"));
        } catch (IOException e) {
            System.err.println("Error cargando imagen de Game Over");
        }

        BufferedImage menuImg = null;
        try {
            menuImg = ImageIO.read(getClass().getResource("/resources/btn_restart.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen del botón: " + e.getMessage());
        }

        JButton retryButton;
        if (menuImg != null) {
            retryButton = new JButton(new ImageIcon(menuImg));
            retryButton.setBorderPainted(false);
            retryButton.setContentAreaFilled(false);
            retryButton.setFocusPainted(false);
        } else {
            retryButton = new JButton("Volver al Menú"); // Fallback si no carga la imagen
        }

        retryButton.setBounds(325, 450, 150, 50);
        retryButton.addActionListener(e -> Game.showMenu());
        add(retryButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("GAME OVER", 200, 300);
        }
    }
}