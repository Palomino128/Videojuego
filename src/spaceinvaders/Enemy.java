package spaceinvaders;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Enemy implements Movable {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    public int x, y;
    private int dx = 1;
    boolean movingRight = true;
    int strength;
    int veloz;
    public static float globalSpeedModifier = 1.0f;
    private static Timer slowdownTimer;

    private static BufferedImage weakImage;
    private static BufferedImage mediumImage;
    private static BufferedImage strongImage;

    static {
        try {
            weakImage = ImageIO.read(Enemy.class.getResource("/resources/enemy_1.png"));
            mediumImage = ImageIO.read(Enemy.class.getResource("/resources/enemy_2.png"));
            strongImage = ImageIO.read(Enemy.class.getResource("/resources/enemy_3.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading enemy images: " + e.getMessage());
        }
    }

    public Enemy(int x, int y, int strength, int veloz) {
        this.x = x;
        this.y = y;
        this.strength = strength;
        this.veloz = veloz;
    }

    public static void globalSlowDown(float modifier) {
        globalSpeedModifier = modifier;
        
        if (slowdownTimer != null) {
            slowdownTimer.cancel();
        }
        
        slowdownTimer = new Timer();
        slowdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                globalSpeedModifier = 1.0f;
            }
        }, 5000); // 5 segundos
    }

    @Override
    public void move() {
        move(1); // Velocidad base
    }

    public void move(int speed) {
        x += (movingRight ? speed : -speed) * globalSpeedModifier;

        if (x <= 0 || x + WIDTH >= 800) {
            movingRight = !movingRight;
            y += 20;
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void draw(Graphics g, Component c) {
        BufferedImage img = switch (strength) {
            case 3 -> strongImage;
            case 2 -> mediumImage;
            default -> weakImage;
        };

        if (img != null) {
            g.drawImage(img, x, y, WIDTH, HEIGHT, c);
        } else {
            g.setColor(getColorByStrength());
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }

    private Color getColorByStrength() {
        return switch (strength) {
            case 3 -> Color.RED;
            case 2 -> Color.ORANGE;
            default -> Color.GREEN;
        };
    }

    public int getStrength() {
        return strength;
    }
}