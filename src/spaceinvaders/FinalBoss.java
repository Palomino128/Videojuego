/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spaceinvaders;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FinalBoss extends Enemy {
    public static final int FINAL_BOSS_WIDTH = 80;
    public static final int FINAL_BOSS_HEIGHT = 60;
    
    private static final int MAX_HITS = 30;
    private int hitsTaken = 0;
    
    private static BufferedImage finalBossImage;
    private static boolean imagesLoaded = false;
    
    public FinalBoss(int x, int y) {
        super(x, y, 1,30); // La resistencia real se maneja con hitsTaken
        loadImages();
    }
    
    private void loadImages() {
        if (!imagesLoaded) {
            try {
                InputStream imgStream = getClass().getResourceAsStream("/resources/final_boss.png");
                if (imgStream != null) {
                    finalBossImage = ImageIO.read(imgStream);
                }
                imagesLoaded = true;
            } catch (IOException e) {
                System.err.println("Error cargando imagen del jefe final: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void move(int speed) {
        // Movimiento más lento pero con cambios de dirección más frecuentes
        double effectiveSpeed = speed * 1.3 * globalSpeedModifier;
        x += (movingRight ? effectiveSpeed : -effectiveSpeed);
        
        // Cambia de dirección más a menudo que los enemigos normales
        if (x <= 0 || x + FINAL_BOSS_WIDTH >= 750) {
            movingRight = !movingRight;
            y += 10; // Desciende menos que los otros enemigos
        }
    }
    
    @Override
    public void draw(Graphics g, Component c) {
        // Dibujar el jefe final
        if (finalBossImage != null) {
            g.drawImage(finalBossImage, x, y, FINAL_BOSS_WIDTH, FINAL_BOSS_HEIGHT, c);
        } else {
            g.setColor(new Color(150, 0, 0)); // Rojo muy oscuro
            g.fillRect(x, y, FINAL_BOSS_WIDTH, FINAL_BOSS_HEIGHT);
        }
        
        // Dibujar barra de vida más detallada
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        int barWidth = FINAL_BOSS_WIDTH;
        int barHeight = 8;
        int segmentWidth = barWidth / MAX_HITS;
        
        // Fondo de la barra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y - 15, barWidth, barHeight);
        
        // Segmentos de vida restante con degradado de color
        for (int i = 0; i < MAX_HITS - hitsTaken; i++) {
            float hue = (float)i / MAX_HITS * 0.4f; // Verde (0.4 en HSB) a Rojo (0.0)
            g.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
            g.fillRect(x + i * segmentWidth, y - 15, segmentWidth - 1, barHeight);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, FINAL_BOSS_WIDTH, FINAL_BOSS_HEIGHT);
    }
    
    public boolean takeHit() {
        hitsTaken++;
        boolean destroyed = hitsTaken >= MAX_HITS;
        return destroyed;
    }
    
    public void fireAtPlayer(int playerX, ArrayList<EnemyBullet> enemyBullets) {
        // Disparo preciso hacia la posición actual del jugador
        int bulletX = this.x + FINAL_BOSS_WIDTH / 2;
        int bulletY = this.y + FINAL_BOSS_HEIGHT;
        
        // Calcular dirección hacia el jugador
        int deltaX = playerX - bulletX;
        int deltaY = 600 - bulletY; // Asumiendo altura de pantalla 600
        
        // Crear bala con dirección calculada
        EnemyBullet bullet = new EnemyBullet(bulletX, bulletY, 10,this.veloz) {
            @Override
            public void move() {
                // Movimiento dirigido al jugador
                x += deltaX / this.w; // Divisor para suavizar el movimiento
                y += deltaY / this.w;
            }
        };
        
        enemyBullets.add(bullet);
        SoundPlayer.playSound("laser.wav");
    }
}
