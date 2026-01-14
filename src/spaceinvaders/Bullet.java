package spaceinvaders;

import java.awt.*;

public class Bullet implements Movable {

    public static final int WIDTH = 5;
    public static final int HEIGHT = 10;

    public int x, y;
    private final int speed = 10;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        SoundPlayer.playSound("laser.wav");
    }

    @Override
    public void move() {
        y -= speed;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
}
