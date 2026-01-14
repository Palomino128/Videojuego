package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class VictoryPanel extends JPanel {
    private BufferedImage backgroundImg;
    private BufferedImage victoryImg;
    private final MusicPlayer musicPlayer = new MusicPlayer();
    private MusicPlayer player;

    public VictoryPanel() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null); // 👈 Igual que GameOverPanel
        player = new MusicPlayer();
        musicPlayer.playMusic("win.wav", false);

        // Cargar fondo (puedes usar el mismo que Game Over si prefieres)
        try {
            backgroundImg = ImageIO.read(getClass().getResource("/resources/background_victory.png"));
        } catch (IOException e) {
            System.err.println("Error cargando fondo de Victory");
        }

        // Cargar imagen Victory
        try {
            victoryImg = ImageIO.read(getClass().getResource("/resources/victory.png"));
        } catch (IOException e) {
            System.err.println("Error cargando imagen de Victory");
        }

        // Cargar imagen del botón
        BufferedImage menuImg = null;
        try {
            menuImg = ImageIO.read(getClass().getResource("/resources/btn_restart.png"));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen del botón de Victory: " + e.getMessage());
        }

        JButton retryButton;
        if (menuImg != null) {
            retryButton = new JButton(new ImageIcon(menuImg));
            retryButton.setBorderPainted(false);
            retryButton.setContentAreaFilled(false);
            retryButton.setFocusPainted(false);
        } else {
            retryButton = new JButton("Volver al Menú");
        }

        retryButton.setBounds(325, 450, 150, 50);
        retryButton.addActionListener(e -> {
            // Forzar cambio correcto de panel
            Game.showMenu();
        });

        add(retryButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Pintar fondo
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Pintar imagen Victory
        if (victoryImg != null) {
            int scaledWidth = victoryImg.getWidth() / 2;
            int scaledHeight = victoryImg.getHeight() / 2;
            int x = (getWidth() - scaledWidth) / 2;
            int y = 100;

            g.drawImage(victoryImg, x, y, scaledWidth, scaledHeight, this);
        }
    }
}
