package spaceinvaders;

import java.awt.*;

public class EnemyBullet implements Movable {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 10;
    public int x, y,w;
    private final int speed; // Elimina 'final' para permitir cambios

    public EnemyBullet(int x, int y, int z,int w) {
        this.x = x;
        this.y = y;
        this.w = w;
        
        this.speed = z + (Game.currentLevel / 2); // Velocidad basada en nivel
        SoundPlayer.playSound("laser.wav");
    }

    @Override
    public void move() {
        y += speed;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
}