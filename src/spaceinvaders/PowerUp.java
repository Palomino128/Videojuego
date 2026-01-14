package spaceinvaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class PowerUp {
    public enum Type {
        DOUBLE_SHOT,  // Dispara 3 balas a la vez
        SHIELD,       // Protección contra 1 golpe
        LIFE_UP       // +1 vida extra
    }

    private int x;
    int y;
    private final Type type;
    private boolean active = true;
    private static final int SIZE = 20;
    private static final int SPEED_Y = 2;

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
    }

    public void move() {
        y += SPEED_Y;
    }

    public void draw(Graphics g) {
        if (!active) return;
        g.setColor(getColor());
        g.fillOval(x, y, SIZE, SIZE);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, SIZE, SIZE);
    }

    private Color getColor() {
        return switch (type) {
            case DOUBLE_SHOT -> Color.YELLOW;
            case SHIELD -> Color.BLUE;
            case LIFE_UP -> Color.GREEN;
        };
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

public void applyEffect(SpaceInvaders game) {
    // Verificar si el power-up ya fue activado
    if (!active) return;
    
    // Reproducir efecto de sonido en un hilo separado para evitar lag
    new Thread(() -> {
        try {
            switch (type) {
                case DOUBLE_SHOT -> 
                    SoundPlayer.playSound("powerup_double.wav");
                case SHIELD -> 
                    SoundPlayer.playSound("powerup_shield.wav");
                case LIFE_UP -> 
                    SoundPlayer.playSound("powerup_life.wav");
            }
        } catch (Exception e) {
            System.out.println("Error al reproducir sonido de power-up");
        }
    }).start();
    
    // Aplicar el efecto correspondiente al tipo de power-up
    switch (type) {
        case DOUBLE_SHOT -> 
            game.activateDoubleShot(); // Activa disparo doble/triple
        case SHIELD -> 
            game.addShield(); // Añade escudo al jugador
        case LIFE_UP -> 
            game.increaseLives(); // Incrementa vidas del jugador
    }
    
    // Marcar el power-up como inactivo para que desaparezca
    active = false;
}

    public int getX() { return x; }
    public int getY() { return y; }
}