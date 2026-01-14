package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import javax.imageio.ImageIO;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    // Variables de gráficos
    private javax.swing.Timer timer;
    private int playerX = 300;
    private final int playerY = 530;
    private final int playerWidth = 40;
    private final int playerHeight = 20;
    private static final double BASE_SPEED_INCREMENT = 0.25;
    private static final int BASE_ATTACK_DECREMENT = 70; // Reducción en ms por nivel
    private static final int MIN_ATTACK_SPEED = 200; // Límite mínimo entre disparos
    private boolean shieldActive = false;
    private static final int MAX_LIVES = 6;
    private long shieldStartTime;
    private BufferedImage shieldImage;
    
    // Imágenes del juego
    private BufferedImage backgroundImage;
    private BufferedImage playerImage;
    private BufferedImage victoryImage;
    private BufferedImage levelClearedImage;
    private BufferedImage gameOverImage;
    
    // Listas de entidades del juego
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    private final ArrayList<PowerUp> powerUps = new ArrayList<>();
    
    // Estados de control
    private boolean leftPressed = false, rightPressed = false, spacePressed = false;
    private boolean canShoot = true;
    
    // Estadísticas del juego
    private int score = 0;
    private int lives = 3;
    private int currentLevel = 1;
    private final int maxLevel = 10;
    
    // Configuración de enemigos
    private double enemyBaseSpeed = 1.0;
    private int enemyAttackSpeed = 1000;
    private final double SPEED_INCREMENT_PER_LEVEL = 0.75;
    private final int enemiesPerRow = 8;
    private final int initialEnemyRows = 2;
    
    // Estados del nivel
    private boolean levelCompleted = false;
    private boolean showLevelCleared = false;
    private long levelClearedTime;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Carga de imágenes
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/resources/background.png"));
            playerImage = ImageIO.read(getClass().getResource("/resources/player.png"));
            levelClearedImage = ImageIO.read(getClass().getResource("/resources/level_cleared.png"));
            gameOverImage = ImageIO.read(getClass().getResource("/resources/game_over.png"));
            victoryImage = ImageIO.read(getClass().getResource("/resources/victory.png"));
            shieldImage = ImageIO.read(getClass().getResource("/resources/shield.png"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando imágenes: " + ex.getMessage());
            System.exit(1);
        }

        initEnemies();
        timer = new javax.swing.Timer(15, this);
        timer.start();
    }

    private void initEnemies() {
        enemies.clear();
        if (currentLevel == 5) {
            // Configuración original para 3 filas de bosses en el nivel 5
            int rows = 4; // 4 filas de bosses
            int cols = 5; // 5 bosses por fila
            int startY = 50;
            int rowSpacing = 80; // Espacio vertical entre filas
            int colSpacing = 130; // Espacio horizontal entre bosses

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    enemies.add(new BossEnemy(
                        50 + col * colSpacing,
                        startY + row * rowSpacing
                    ));
                }
            }
        
            enemyBaseSpeed = Math.min(4.0, 1.0 + (currentLevel * SPEED_INCREMENT_PER_LEVEL));
            enemyAttackSpeed = 400;
        
        } else if (currentLevel == maxLevel) { // NIVEL FINAL (10)
            // Jefe final en la parte superior
            enemies.add(new FinalBoss(360, 30));
        
        // Filas intercaladas de enemigos normales y bosses
        int rows = 4;
        int cols = 6;
        int startY = 100;
        int rowSpacing = 50;
        int colSpacing = 120;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row % 2 == 0) { // Filas pares: enemigos normales
                    enemies.add(new Enemy(
                        50 + col * colSpacing,
                        startY + row * rowSpacing,
                        row + 1 ,50// Aumenta la fuerza por fila
                    ));
                } else { // Filas impares: bosses
                    enemies.add(new BossEnemy(
                        50 + col * colSpacing,
                        startY + row * rowSpacing
                    ));
                }
            }
        }
        
        enemyBaseSpeed = 5.0; // Velocidad base muy alta
        enemyAttackSpeed = 200; // Disparan más rápido
        
        } else {
            // CONFIGURACIÓN NORMAL CON DIFICULTAD PROGRESIVA
            int rows = initialEnemyRows + (currentLevel / 2);
            int cols = enemiesPerRow - 1; // Un poco menos de enemigos por fila para mejor espaciado
            int startY = 50;
            int rowSpacing = 45;
            int colSpacing = 90; // Aumentamos espacio entre columnas (antes era 80)

            for (int row = 0; row < rows; row++) {
                int strength = Math.max(1, 4 - row);
                for (int col = 0; col < cols; col++) {
                    enemies.add(new Enemy(
                        50 + col * colSpacing, // Más espacio horizontal
                        startY + row * rowSpacing, 
                        strength,50
                    ));
                }
            }

            // DIFICULTAD PROGRESIVA POR NIVEL
            enemyBaseSpeed = 1.0 + (currentLevel * SPEED_INCREMENT_PER_LEVEL);
            enemyAttackSpeed = Math.max(MIN_ATTACK_SPEED, 1000 - (currentLevel * BASE_ATTACK_DECREMENT));
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Dibujar fondo
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Dibujar jugador
        if (playerImage != null) {
            g2d.drawImage(playerImage, playerX, playerY, playerWidth, playerHeight, this);
        }
        
        // Dibujar mensaje de nivel completado
        if (showLevelCleared && levelClearedImage != null) {
            g2d.drawImage(
                levelClearedImage, 
                getWidth()/2 - levelClearedImage.getWidth()/2, 
                getHeight()/2 - levelClearedImage.getHeight()/2, 
                this
            );
        }
        
        //Dibujar escudo
        if (shieldActive && shieldImage != null) {
            int shieldX = playerX + playerWidth/2 - 25; // Centrar (50px/2)
            int shieldY = playerY + playerHeight/2 - 25;
            g2d.drawImage(shieldImage, shieldX, shieldY, 50, 50, this);
        }

        
        // Dibujar entidades del juego
        bullets.forEach(b -> b.draw(g2d));
        enemyBullets.forEach(eb -> eb.draw(g2d));
        enemies.forEach(e -> e.draw(g2d, this));
        powerUps.forEach(pu -> pu.draw(g2d));
        
        // Dibujar HUD
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Score: " + score, 10, 20);
        g2d.drawString("Lives: " + lives, 700, 20);
        g2d.drawString("Level: " + currentLevel, 400, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Movimiento del jugador
        if (leftPressed && playerX > 0) playerX -= 5;
        if (rightPressed && playerX < getWidth() - playerWidth) playerX += 5;

        // Disparo del jugador
        if (spacePressed && canShoot) {
            fireBullet();
            spacePressed = false;
            canShoot = false;
        }
    
        // Actualizar balas del jugador
        bullets.removeIf(b -> {
            b.move();
            return b.y < 0;
        });

        //Gestión de tiempo
        if (shieldActive && System.currentTimeMillis() - shieldStartTime > 5000) {
            shieldActive = false;
        }
    
        // Comportamiento según el nivel actual
        if (currentLevel == 5) {
            // Comportamiento para el nivel 5 (solo bosses)
            for (Enemy enemy : enemies) {
                enemy.move((int)Math.round(enemyBaseSpeed));

                if (enemy.y + BossEnemy.BOSS_HEIGHT >= playerY - 10) {
                    gameOver();
                    break;
                }

                // Bosses disparan con frecuencia media
                if (Math.random() < 0.008) {
                    enemyBullets.add(new EnemyBullet(
                        enemy.x + BossEnemy.BOSS_WIDTH/2, 
                        enemy.y + BossEnemy.BOSS_HEIGHT,
                        5,enemy.veloz
                    ));
                }
            }
        } else if (currentLevel == maxLevel) {
            // Comportamiento para el nivel final (Final Boss + mezcla)
            for (Enemy enemy : enemies) {
                enemy.move((int)Math.round(enemyBaseSpeed));

                if ((enemy instanceof FinalBoss && enemy.y + FinalBoss.FINAL_BOSS_HEIGHT >= playerY - 10) ||
                    (!(enemy instanceof FinalBoss) && enemy.y + Enemy.HEIGHT >= playerY - 10)) {
                    gameOver();
                    break;
                }

                if (enemy instanceof FinalBoss) {
                    FinalBoss finalBoss = (FinalBoss)enemy;
                    if (Math.random() < 0.02) {
                        finalBoss.fireAtPlayer(playerX, enemyBullets);
                    }
                } else if (enemy instanceof BossEnemy) {
                    if (Math.random() < 0.005) {
                        enemyBullets.add(new EnemyBullet(
                            enemy.x + BossEnemy.BOSS_WIDTH/2, 
                            enemy.y + BossEnemy.BOSS_HEIGHT,
                                5,enemy.veloz
                        ));
                    }
                } else {
                    if (Math.random() < 0.001) {
                        enemyBullets.add(new EnemyBullet(
                            enemy.x + Enemy.WIDTH/2, 
                            enemy.y + Enemy.HEIGHT,
                                5,enemy.veloz
                        ));
                    }
                }
            }
        } else {
            // Comportamiento normal para otros niveles
            for (Enemy enemy : enemies) {
                enemy.move((int)Math.round(enemyBaseSpeed));

                if (enemy.y + Enemy.HEIGHT >= playerY - 10) {
                    gameOver();
                    break;
                }

                if (Math.random() < 0.001 * (1 + currentLevel * 0.5)) {
                    enemyBullets.add(new EnemyBullet(
                        enemy.x + Enemy.WIDTH/2, 
                        enemy.y + Enemy.HEIGHT,
                            5,enemy.veloz
                    ));
                }
            }
        }
    
        // Actualizar balas enemigas (común para todos los niveles)
        enemyBullets.removeIf(eb -> {
            eb.move();
            if (eb.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                // Solo afecta si el escudo NO está activo
                if (!shieldActive) {
                    lives--;
                    if (lives <= 0) gameOver();
                }
                return true;
            }
            return eb.y > getHeight();
        });
    
        // Actualizar power-ups (común para todos los niveles)
        powerUps.removeIf(pu -> {
            pu.move();
            if (pu.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                pu.applyEffect(this);
                return true;
            }
            return pu.y > getHeight();
        });
    
        // Verificar colisiones (común para todos los niveles)
        checkCollisions();
    
        // Redibujar el juego
        repaint();
    
        // Cambiar de nivel si está completado (común para todos los niveles)
        if (levelCompleted && System.currentTimeMillis() - levelClearedTime > 2000) {
            currentLevel++;

            if (currentLevel > maxLevel) {
                timer.stop();
                Game.showVictory();
            } else {
                enemyBaseSpeed = 1.0 + (currentLevel * SPEED_INCREMENT_PER_LEVEL);
                enemyAttackSpeed = Math.max(MIN_ATTACK_SPEED, 1000 - (currentLevel * BASE_ATTACK_DECREMENT));

                levelCompleted = false;
                showLevelCleared = false;
                initEnemies();
                timer.start();
            }
        }
    }

    private void fireBullet() {
        bullets.add(new Bullet(
            playerX + playerWidth/2 - Bullet.WIDTH/2, 
            playerY - Bullet.HEIGHT
        ));
        SoundPlayer.playSound("laser.wav");
    }

    private void checkCollisions() {
    // Iterador para las balas del jugador
    Iterator<Bullet> bulletIt = bullets.iterator();
    
    // Recorrer todas las balas del jugador
    while (bulletIt.hasNext()) {
        Bullet bullet = bulletIt.next();
        // Iterador para los enemigos
        Iterator<Enemy> enemyIt = enemies.iterator();
        
        // Verificar colisión con cada enemigo
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            
            // Si hay colisión entre bala y enemigo
            if (bullet.getBounds().intersects(enemy.getBounds())) {
                // Eliminar la bala
                bulletIt.remove();
                
                // Manejar diferentes tipos de enemigos
                if (enemy instanceof FinalBoss) {
                    FinalBoss boss = (FinalBoss) enemy;
                    if (boss.takeHit()) { // Si el jefe final es destruido
                        enemyIt.remove();
                        score += 500; // Puntos por jefe final
                    }
                } else if (enemy instanceof BossEnemy) {
                    BossEnemy boss = (BossEnemy) enemy;
                    if (boss.takeHit()) { // Si un jefe normal es destruido
                        enemyIt.remove();
                        score += 100; // Puntos por jefe normal
                    }
                } else {
                    // Enemigo normal
                    enemyIt.remove();
                    score += 10; // Puntos por enemigo normal
                }

                // Generar power-up aleatorio en niveles >= 2 (25% de probabilidad)
                if (currentLevel >= 2 && new Random().nextFloat() < 0.25f) {
                    powerUps.add(new PowerUp(enemy.x, enemy.y));
                }

                // Verificar si se completó el nivel (no quedan enemigos)
                if (enemies.isEmpty()) {
                    levelCompleted = true;
                    showLevelCleared = true;
                    levelClearedTime = System.currentTimeMillis();
                    
                    // Reproducir sonido de nivel completado en hilo separado
                    new Thread(() -> {
                        SoundPlayer.playSound("Level_up.wav");
                    }).start();
                }
                break; // Salir del bucle de enemigos tras colisión
            }
        }
    }
    
    // Verificar colisiones entre jugador y balas enemigas
    Iterator<EnemyBullet> enemyBulletIt = enemyBullets.iterator();
    while (enemyBulletIt.hasNext()) {
        EnemyBullet eb = enemyBulletIt.next();
        if (eb.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
            enemyBulletIt.remove();
            if (!shieldActive) {
                lives--;
                if (lives <= 0) gameOver();
            }
        }
    }
    
    // Verificar colisiones entre jugador y power-ups
    Iterator<PowerUp> powerUpIt = powerUps.iterator();
    while (powerUpIt.hasNext()) {
        PowerUp pu = powerUpIt.next();
        if (pu.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
            pu.applyEffect(this);
            powerUpIt.remove();
        }
    }
}

    private void gameOver() {
        Game.currentLevel = this.currentLevel;
        timer.stop();
        Game.showGameOver();
    }

    // Métodos para power-ups
    public void activateDoubleShot() {
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                fireBullet();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void addShield() {
        shieldActive = true;
        shieldStartTime = System.currentTimeMillis();
        SoundPlayer.playSound("powerup_shield.wav");
    }

    public void increaseLives() {
        if (lives < MAX_LIVES) {
        lives++;
        SoundPlayer.playSound("life_up.wav");
    }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> leftPressed = true;
            case KeyEvent.VK_RIGHT -> rightPressed = true;
            case KeyEvent.VK_SPACE -> spacePressed = true;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> leftPressed = false;
            case KeyEvent.VK_RIGHT -> rightPressed = false;
            case KeyEvent.VK_SPACE -> {
                spacePressed = false;
                canShoot = true;
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
}