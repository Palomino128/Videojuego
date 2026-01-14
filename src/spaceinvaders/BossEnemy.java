package spaceinvaders;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class BossEnemy extends Enemy {
    // Tamaño aumentado para los jefes
    public static final int BOSS_WIDTH = 60;
    public static final int BOSS_HEIGHT = 40;
    
    // Configuración de resistencia
    private static final int MAX_HITS = 5; // 5 disparos para destruirlo
    private int hitsTaken = 0;
    
    // Imagen del jefe
    private static BufferedImage bossImage;
    private static boolean imagesLoaded = false;
    
    public BossEnemy(int x, int y) {
        super(x, y, 1,40); // La resistencia real se maneja con hitsTaken
        loadImages();
    }
    
    private void loadImages() {
        if (!imagesLoaded) {
            try {
                InputStream imgStream = getClass().getResourceAsStream("/resources/boss_enemy.png");
                if (imgStream != null) {
                    bossImage = ImageIO.read(imgStream);
                }
                imagesLoaded = true;
            } catch (IOException e) {
                System.err.println("Error cargando imagen de jefe: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void move(int speed) {
        // Movimiento igual que los enemigos normales
        x += (movingRight ? speed : -speed) * globalSpeedModifier;

        if (x <= 0 || x + BOSS_WIDTH >= 800) {
            movingRight = !movingRight;
            y += 20; // Mover hacia abajo al cambiar de dirección
        }
    }
    
    @Override
    public void draw(Graphics g, Component c) {
        // Dibujar el jefe
        if (bossImage != null) {
            g.drawImage(bossImage, x, y, BOSS_WIDTH, BOSS_HEIGHT, c);
        } else {
            g.setColor(new Color(200, 50, 50)); // Rojo oscuro
            g.fillRect(x, y, BOSS_WIDTH, BOSS_HEIGHT);
        }
        
        // Dibujar barra de vida (3 segmentos)
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        int barWidth = BOSS_WIDTH;
        int barHeight = 6;
        int segmentWidth = barWidth / MAX_HITS;
        
        // Fondo de la barra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y - 10, barWidth, barHeight);
        
        // Segmentos de vida restante
        g.setColor(Color.GREEN);
        int remainingSegments = MAX_HITS - hitsTaken;
        for (int i = 0; i < remainingSegments; i++) {
            g.fillRect(x + i * segmentWidth, y - 10, segmentWidth - 2, barHeight);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, BOSS_WIDTH, BOSS_HEIGHT);
    }
    
    public boolean takeHit() {
        hitsTaken++;
        return hitsTaken >= MAX_HITS;
    }
}