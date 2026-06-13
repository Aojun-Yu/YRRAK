import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GamePanel game = new GamePanel();
            JFrame frame = new JFrame("霓虹逃亡：超载");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @SuppressWarnings("serial")
    private static final class GamePanel extends JPanel {
        private static final int WIDTH = 900;
        private static final int HEIGHT = 620;
        private static final int FPS_DELAY = 16;

        private final Random random = new Random();
        private final boolean[] keys = new boolean[512];
        private final List<Enemy> enemies = new ArrayList<>();
        private final List<Particle> particles = new ArrayList<>();
        private final List<Star> stars = new ArrayList<>();
        private final List<Upgrade> upgradeChoices = new ArrayList<>();
        private final AudioEngine audio = new AudioEngine();
        private final Timer timer;

        private double playerX;
        private double playerY;
        private double velocityX;
        private double velocityY;
        private double crystalX;
        private double crystalY;

        private double moveSpeed;
        private double dashPower;
        private double enemySlow;
        private double scoreMultiplier;
        private double magnetRadius;
        private int dashCooldownMax;
        private int shockwaveRadius;
        private int drones;
        private int shieldCharges;
        private int maxLives;

        private int score;
        private int lives;
        private int combo;
        private int bestCombo;
        private int frames;
        private int lastCollectFrame;
        private int dashCooldown;
        private int dashFrames;
        private int invincibleFrames;
        private int screenShake;
        private int level;
        private int experience;
        private int experienceNeeded;
        private int kills;
        private int crystals;
        private String lastUpgrade = "";
        private int upgradeToastFrames;

        private boolean started;
        private boolean gameOver;
        private boolean paused;
        private boolean choosingUpgrade;

        GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(5, 8, 22));
            setFocusable(true);
            setDoubleBuffered(true);
            createStarField();
            installControls();
            resetGame();
            timer = new Timer(FPS_DELAY, this::tick);
            timer.start();
        }

        private void installControls() {
            int[] controls = {
                    KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D,
                    KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT
            };
            for (int keyCode : controls) {
                getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                        KeyStroke.getKeyStroke(keyCode, 0, false), "press-" + keyCode);
                getActionMap().put("press-" + keyCode, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        keys[keyCode] = true;
                        if (!choosingUpgrade) started = true;
                    }
                });
                getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                        KeyStroke.getKeyStroke(keyCode, 0, true), "release-" + keyCode);
                getActionMap().put("release-" + keyCode, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        keys[keyCode] = false;
                    }
                });
            }

            bindPress(KeyEvent.VK_SPACE, "dash", () -> {
                if (!choosingUpgrade) {
                    started = true;
                    dash();
                }
            });
            bindPress(KeyEvent.VK_R, "restart", () -> {
                if (gameOver) {
                    resetGame();
                    started = true;
                }
            });
            bindPress(KeyEvent.VK_P, "pause", () -> {
                if (started && !gameOver && !choosingUpgrade) paused = !paused;
            });
            bindPress(KeyEvent.VK_M, "mute", audio::toggleMute);
            bindPress(KeyEvent.VK_1, "upgrade-1", () -> chooseUpgrade(0));
            bindPress(KeyEvent.VK_2, "upgrade-2", () -> chooseUpgrade(1));
            bindPress(KeyEvent.VK_3, "upgrade-3", () -> chooseUpgrade(2));
        }

        private void bindPress(int keyCode, String name, Runnable action) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keyCode, 0, false), name);
            getActionMap().put(name, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action.run();
                }
            });
        }

        private void resetGame() {
            playerX = WIDTH / 2.0;
            playerY = HEIGHT / 2.0;
            velocityX = 0;
            velocityY = 0;
            moveSpeed = 4.4;
            dashPower = 11.5;
            enemySlow = 1.0;
            scoreMultiplier = 1.0;
            magnetRadius = 0;
            dashCooldownMax = 90;
            shockwaveRadius = 0;
            drones = 0;
            shieldCharges = 0;
            maxLives = 3;
            score = 0;
            lives = 3;
            combo = 1;
            bestCombo = 1;
            frames = 0;
            lastCollectFrame = 0;
            dashCooldown = 0;
            dashFrames = 0;
            invincibleFrames = 0;
            screenShake = 0;
            level = 1;
            experience = 0;
            experienceNeeded = 3;
            kills = 0;
            crystals = 0;
            upgradeToastFrames = 0;
            started = false;
            gameOver = false;
            paused = false;
            choosingUpgrade = false;
            enemies.clear();
            particles.clear();
            upgradeChoices.clear();
            placeCrystal();
        }

        private void createStarField() {
            for (int i = 0; i < 100; i++) {
                stars.add(new Star(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                        0.4 + random.nextDouble() * 1.8,
                        random.nextDouble() * Math.PI * 2));
            }
        }

        private void tick(ActionEvent ignored) {
            if (started && !gameOver && !paused && !choosingUpgrade) updateGame();
            repaint();
        }

        private void updateGame() {
            frames++;
            if (dashCooldown > 0) dashCooldown--;
            if (dashFrames > 0) dashFrames--;
            if (invincibleFrames > 0) invincibleFrames--;
            if (screenShake > 0) screenShake--;
            if (upgradeToastFrames > 0) upgradeToastFrames--;

            updatePlayer();
            updateEnemies();
            updateParticles();
            updateCrystal();

            int spawnInterval = Math.max(24, 94 - frames / 175);
            if (frames % spawnInterval == 0 && enemies.size() < 34) spawnEnemy();
            if (frames - lastCollectFrame > 240) combo = 1;
            score += Math.max(1, (int) scoreMultiplier);
        }

        private void updatePlayer() {
            double inputX = 0;
            double inputY = 0;
            if (isDown(KeyEvent.VK_A) || isDown(KeyEvent.VK_LEFT)) inputX--;
            if (isDown(KeyEvent.VK_D) || isDown(KeyEvent.VK_RIGHT)) inputX++;
            if (isDown(KeyEvent.VK_W) || isDown(KeyEvent.VK_UP)) inputY--;
            if (isDown(KeyEvent.VK_S) || isDown(KeyEvent.VK_DOWN)) inputY++;

            double length = Math.hypot(inputX, inputY);
            if (length > 0) {
                velocityX += inputX / length * 0.75;
                velocityY += inputY / length * 0.75;
            }

            double speedLimit = dashFrames > 0 ? dashPower : moveSpeed;
            double speed = Math.hypot(velocityX, velocityY);
            if (speed > speedLimit) {
                velocityX = velocityX / speed * speedLimit;
                velocityY = velocityY / speed * speedLimit;
            }

            playerX += velocityX;
            playerY += velocityY;
            velocityX *= dashFrames > 0 ? 0.97 : 0.82;
            velocityY *= dashFrames > 0 ? 0.97 : 0.82;

            if (playerX < 18) {
                playerX = 18;
                velocityX = Math.abs(velocityX) * 0.4;
            } else if (playerX > WIDTH - 18) {
                playerX = WIDTH - 18;
                velocityX = -Math.abs(velocityX) * 0.4;
            }
            if (playerY < 18) {
                playerY = 18;
                velocityY = Math.abs(velocityY) * 0.4;
            } else if (playerY > HEIGHT - 18) {
                playerY = HEIGHT - 18;
                velocityY = -Math.abs(velocityY) * 0.4;
            }

            if (Math.hypot(velocityX, velocityY) > 1.2 && frames % 2 == 0) {
                particles.add(new Particle(playerX - velocityX * 2, playerY - velocityY * 2,
                        -velocityX * 0.15 + random.nextGaussian() * 0.4,
                        -velocityY * 0.15 + random.nextGaussian() * 0.4,
                        dashFrames > 0 ? new Color(255, 240, 120) : new Color(50, 225, 255),
                        18, 4));
            }
        }

        private boolean isDown(int keyCode) {
            return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
        }

        private void dash() {
            if (gameOver || paused || dashCooldown > 0) return;
            double directionX = 0;
            double directionY = 0;
            if (isDown(KeyEvent.VK_A) || isDown(KeyEvent.VK_LEFT)) directionX--;
            if (isDown(KeyEvent.VK_D) || isDown(KeyEvent.VK_RIGHT)) directionX++;
            if (isDown(KeyEvent.VK_W) || isDown(KeyEvent.VK_UP)) directionY--;
            if (isDown(KeyEvent.VK_S) || isDown(KeyEvent.VK_DOWN)) directionY++;
            if (directionX == 0 && directionY == 0) {
                directionX = velocityX;
                directionY = velocityY;
            }
            if (directionX == 0 && directionY == 0) directionY = -1;

            double length = Math.hypot(directionX, directionY);
            velocityX = directionX / length * dashPower;
            velocityY = directionY / length * dashPower;
            dashFrames = 16;
            invincibleFrames = Math.max(invincibleFrames, 20);
            dashCooldown = dashCooldownMax;
            screenShake = 5;
            burst(playerX, playerY, new Color(255, 230, 90), 20, 3.5);
            audio.play(Sound.DASH);
        }

        private void updateEnemies() {
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                double dx = playerX - enemy.x;
                double dy = playerY - enemy.y;
                double distance = Math.max(0.01, Math.hypot(dx, dy));
                double wobble = Math.sin((frames + enemy.phase) * 0.045) * enemy.wobble;
                enemy.vx += (dx / distance * enemy.acceleration
                        + -dy / distance * wobble) * enemySlow;
                enemy.vy += (dy / distance * enemy.acceleration
                        + dx / distance * wobble) * enemySlow;
                double enemySpeed = Math.hypot(enemy.vx, enemy.vy);
                double speedCap = enemy.maxSpeed * enemySlow;
                if (enemySpeed > speedCap) {
                    enemy.vx = enemy.vx / enemySpeed * speedCap;
                    enemy.vy = enemy.vy / enemySpeed * speedCap;
                }
                enemy.x += enemy.vx;
                enemy.y += enemy.vy;

                boolean destroyed = false;
                if (drones > 0) {
                    for (int i = 0; i < drones; i++) {
                        Point drone = dronePosition(i);
                        if (drone.distance(enemy.x, enemy.y) < enemy.radius + 9) {
                            destroyed = true;
                            break;
                        }
                    }
                }

                if (destroyed || (distance < enemy.radius + 13 && dashFrames > 0)) {
                    destroyEnemy(iterator, enemy, true);
                } else if (distance < enemy.radius + 13 && invincibleFrames == 0) {
                    takeDamage(enemy);
                    iterator.remove();
                }
            }
        }

        private void destroyEnemy(Iterator<Enemy> iterator, Enemy enemy, boolean reward) {
            if (reward) {
                score += (int) (300 * combo * scoreMultiplier);
                kills++;
                gainExperience(1);
            }
            burst(enemy.x, enemy.y, enemy.color, 15, 4.5);
            iterator.remove();
            audio.play(Sound.HIT);
        }

        private Point dronePosition(int index) {
            double angle = frames * 0.055 + Math.PI * 2 * index / drones;
            double radius = 48 + drones * 3;
            return new Point((int) (playerX + Math.cos(angle) * radius),
                    (int) (playerY + Math.sin(angle) * radius));
        }

        private void takeDamage(Enemy enemy) {
            if (shieldCharges > 0) {
                shieldCharges--;
                invincibleFrames = 70;
                screenShake = 9;
                burst(playerX, playerY, new Color(80, 210, 255), 28, 5);
                audio.play(Sound.SHIELD);
                return;
            }

            lives--;
            combo = 1;
            invincibleFrames = 110;
            screenShake = 18;
            velocityX = -enemy.vx * 3;
            velocityY = -enemy.vy * 3;
            burst(playerX, playerY, new Color(255, 60, 110), 32, 6);
            audio.play(Sound.DAMAGE);
            if (lives <= 0) {
                gameOver = true;
                burst(playerX, playerY, Color.WHITE, 50, 8);
                audio.play(Sound.GAME_OVER);
            }
        }

        private void updateParticles() {
            Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                particle.x += particle.vx;
                particle.y += particle.vy;
                particle.vx *= 0.96;
                particle.vy *= 0.96;
                particle.life--;
                if (particle.life <= 0) iterator.remove();
            }
        }

        private void updateCrystal() {
            double dx = playerX - crystalX;
            double dy = playerY - crystalY;
            double distance = Math.hypot(dx, dy);
            if (magnetRadius > 0 && distance < magnetRadius && distance > 1) {
                double pull = 2.2 + (magnetRadius - distance) / magnetRadius * 4.5;
                crystalX += dx / distance * pull;
                crystalY += dy / distance * pull;
                distance = Math.hypot(playerX - crystalX, playerY - crystalY);
            }
            if (distance < 27) collectCrystal();
        }

        private void collectCrystal() {
            score += (int) (500 * combo * scoreMultiplier);
            combo = Math.min(12, combo + 1);
            bestCombo = Math.max(bestCombo, combo);
            crystals++;
            lastCollectFrame = frames;
            dashCooldown = Math.max(0, dashCooldown - 28);
            burst(crystalX, crystalY, new Color(190, 90, 255), 24, 4.5);
            if (shockwaveRadius > 0) shockwave();
            placeCrystal();
            audio.play(Sound.PICKUP);
            gainExperience(2);

            if (combo % 3 == 0) {
                spawnEnemy();
                spawnEnemy();
            }
        }

        private void shockwave() {
            burst(playerX, playerY, new Color(130, 110, 255), 36, 6);
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                if (Math.hypot(enemy.x - playerX, enemy.y - playerY) < shockwaveRadius) {
                    score += (int) (180 * combo * scoreMultiplier);
                    kills++;
                    burst(enemy.x, enemy.y, enemy.color, 10, 3.5);
                    iterator.remove();
                }
            }
            screenShake = 7;
            audio.play(Sound.SHOCKWAVE);
        }

        private void gainExperience(int amount) {
            experience += amount;
            if (experience >= experienceNeeded && !choosingUpgrade) {
                experience -= experienceNeeded;
                level++;
                experienceNeeded = 3 + level * 2;
                openUpgradeChoice();
            }
        }

        private void openUpgradeChoice() {
            choosingUpgrade = true;
            upgradeChoices.clear();
            List<Upgrade> pool = new ArrayList<>();
            Collections.addAll(pool, Upgrade.values());
            if (drones >= 4) pool.remove(Upgrade.ORBIT_DRONE);
            if (shieldCharges >= 3) pool.remove(Upgrade.PHASE_SHIELD);
            if (lives >= maxLives) pool.remove(Upgrade.REPAIR);
            Collections.shuffle(pool, random);
            upgradeChoices.addAll(pool.subList(0, Math.min(3, pool.size())));
            audio.play(Sound.LEVEL_UP);
        }

        private void chooseUpgrade(int index) {
            if (!choosingUpgrade || index < 0 || index >= upgradeChoices.size()) return;
            Upgrade upgrade = upgradeChoices.get(index);
            applyUpgrade(upgrade);
            lastUpgrade = upgrade.title;
            upgradeToastFrames = 150;
            choosingUpgrade = false;
            upgradeChoices.clear();
            audio.play(Sound.SELECT);
            if (experience >= experienceNeeded) {
                experience -= experienceNeeded;
                level++;
                experienceNeeded = 3 + level * 2;
                openUpgradeChoice();
            }
        }

        private void applyUpgrade(Upgrade upgrade) {
            switch (upgrade) {
                case OVERDRIVE -> moveSpeed *= 1.13;
                case CAPACITOR -> dashCooldownMax = Math.max(38, (int) (dashCooldownMax * 0.82));
                case IMPACT_CORE -> dashPower += 1.8;
                case GRAVITY_WELL -> magnetRadius += 95;
                case SHOCKWAVE -> shockwaveRadius += 75;
                case ORBIT_DRONE -> drones++;
                case PHASE_SHIELD -> shieldCharges = Math.min(3, shieldCharges + 1);
                case REPAIR -> lives = Math.min(maxLives, lives + 1);
                case REINFORCED_HULL -> {
                    maxLives++;
                    lives++;
                }
                case FORTUNE -> scoreMultiplier += 0.30;
                case TIME_DILATION -> enemySlow = Math.max(0.62, enemySlow * 0.91);
            }
        }

        private void placeCrystal() {
            do {
                crystalX = 60 + random.nextDouble() * (WIDTH - 120);
                crystalY = 75 + random.nextDouble() * (HEIGHT - 140);
            } while (Math.hypot(playerX - crystalX, playerY - crystalY) < 140);
        }

        private void spawnEnemy() {
            int side = random.nextInt(4);
            double x;
            double y;
            if (side == 0) {
                x = -30;
                y = random.nextDouble() * HEIGHT;
            } else if (side == 1) {
                x = WIDTH + 30;
                y = random.nextDouble() * HEIGHT;
            } else if (side == 2) {
                x = random.nextDouble() * WIDTH;
                y = -30;
            } else {
                x = random.nextDouble() * WIDTH;
                y = HEIGHT + 30;
            }

            double difficulty = Math.min(1.65, frames / 3400.0);
            boolean hunter = random.nextDouble() < Math.min(0.38, frames / 4800.0);
            if (hunter) {
                enemies.add(new Enemy(x, y, 15, 0.035 + difficulty * 0.012,
                        2.5 + difficulty, 0.012, new Color(255, 70, 150), random.nextInt(500)));
            } else {
                enemies.add(new Enemy(x, y, 11, 0.018 + difficulty * 0.01,
                        1.8 + difficulty, 0.004, new Color(255, 125, 60), random.nextInt(500)));
            }
        }

        private void burst(double x, double y, Color color, int count, double power) {
            for (int i = 0; i < count; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double speed = random.nextDouble() * power + 0.5;
                particles.add(new Particle(x, y, Math.cos(angle) * speed,
                        Math.sin(angle) * speed, color, 20 + random.nextInt(24),
                        2 + random.nextDouble() * 5));
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int shakeX = screenShake > 0 ? random.nextInt(screenShake + 1) - screenShake / 2 : 0;
            int shakeY = screenShake > 0 ? random.nextInt(screenShake + 1) - screenShake / 2 : 0;
            g.translate(shakeX, shakeY);
            drawBackground(g);
            drawShockwaveRange(g);
            drawCrystal(g);
            drawParticles(g);
            drawEnemies(g);
            drawPlayer(g);
            drawDrones(g);
            g.translate(-shakeX, -shakeY);
            drawHud(g);
            if (upgradeToastFrames > 0 && !choosingUpgrade) drawUpgradeToast(g);
            if (!started) drawStartScreen(g);
            if (paused) drawCenteredMessage(g, "暂停", "按 P 继续");
            if (choosingUpgrade) drawUpgradeScreen(g);
            if (gameOver) drawGameOver(g);
            g.dispose();
        }

        private void drawBackground(Graphics2D g) {
            g.setPaint(new GradientPaint(0, 0, new Color(8, 12, 35),
                    WIDTH, HEIGHT, new Color(24, 8, 39)));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setStroke(new BasicStroke(1f));
            g.setColor(new Color(90, 115, 190, 18));
            int offset = (frames / 2) % 40;
            for (int x = -offset; x < WIDTH; x += 40) g.drawLine(x, 0, x, HEIGHT);
            for (int y = -offset; y < HEIGHT; y += 40) g.drawLine(0, y, WIDTH, y);
            for (Star star : stars) {
                double pulse = 0.45 + Math.sin(frames * 0.025 + star.phase) * 0.35;
                g.setColor(new Color(150, 205, 255, (int) (90 + pulse * 100)));
                g.fill(new Ellipse2D.Double(star.x, star.y, star.size, star.size));
            }
        }

        private void drawShockwaveRange(Graphics2D g) {
            if (shockwaveRadius <= 0) return;
            g.setColor(new Color(135, 110, 255, 13));
            g.fillOval((int) playerX - shockwaveRadius, (int) playerY - shockwaveRadius,
                    shockwaveRadius * 2, shockwaveRadius * 2);
        }

        private void drawCrystal(Graphics2D g) {
            double pulse = 1 + Math.sin(frames * 0.11) * 0.13;
            int radius = (int) (16 * pulse);
            g.setColor(new Color(185, 80, 255, 45));
            g.fillOval((int) crystalX - radius * 2, (int) crystalY - radius * 2,
                    radius * 4, radius * 4);
            g.translate(crystalX, crystalY);
            g.rotate(frames * 0.025);
            Polygon diamond = new Polygon(new int[]{0, radius, 0, -radius},
                    new int[]{-radius, 0, radius, 0}, 4);
            g.setColor(new Color(205, 95, 255));
            g.fillPolygon(diamond);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawPolygon(diamond);
            g.rotate(-frames * 0.025);
            g.translate(-crystalX, -crystalY);
        }

        private void drawParticles(Graphics2D g) {
            for (Particle particle : particles) {
                float alpha = Math.min(1f, particle.life / 20f);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.setColor(particle.color);
                double size = particle.size * alpha;
                g.fill(new Ellipse2D.Double(particle.x - size / 2,
                        particle.y - size / 2, size, size));
            }
            g.setComposite(AlphaComposite.SrcOver);
        }

        private void drawEnemies(Graphics2D g) {
            for (Enemy enemy : enemies) {
                g.setColor(new Color(enemy.color.getRed(), enemy.color.getGreen(),
                        enemy.color.getBlue(), 42));
                g.fillOval((int) (enemy.x - enemy.radius * 1.7),
                        (int) (enemy.y - enemy.radius * 1.7),
                        (int) (enemy.radius * 3.4), (int) (enemy.radius * 3.4));
                g.setColor(enemy.color);
                g.fillOval((int) enemy.x - enemy.radius, (int) enemy.y - enemy.radius,
                        enemy.radius * 2, enemy.radius * 2);
                g.setColor(new Color(30, 8, 25));
                g.fillOval((int) enemy.x - 4, (int) enemy.y - 4, 8, 8);
            }
        }

        private void drawPlayer(Graphics2D g) {
            if (invincibleFrames > 0 && invincibleFrames % 8 < 4) return;
            Color color = dashFrames > 0 ? new Color(255, 235, 100) : new Color(55, 230, 255);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
            g.fillOval((int) playerX - 25, (int) playerY - 25, 50, 50);
            if (shieldCharges > 0) {
                g.setColor(new Color(80, 205, 255, 100));
                g.setStroke(new BasicStroke(2f));
                g.drawOval((int) playerX - 20, (int) playerY - 20, 40, 40);
            }
            g.setColor(color);
            g.fillOval((int) playerX - 13, (int) playerY - 13, 26, 26);
            g.setColor(Color.WHITE);
            g.fillOval((int) playerX - 5, (int) playerY - 5, 10, 10);
            if (dashFrames > 0) {
                g.setColor(new Color(255, 240, 120, 130));
                g.setStroke(new BasicStroke(3f));
                g.drawOval((int) playerX - 21, (int) playerY - 21, 42, 42);
            }
        }

        private void drawDrones(Graphics2D g) {
            for (int i = 0; i < drones; i++) {
                Point drone = dronePosition(i);
                g.setColor(new Color(255, 210, 70, 55));
                g.fillOval(drone.x - 15, drone.y - 15, 30, 30);
                g.setColor(new Color(255, 225, 90));
                g.fillOval(drone.x - 7, drone.y - 7, 14, 14);
                g.setColor(Color.WHITE);
                g.fillOval(drone.x - 2, drone.y - 2, 4, 4);
            }
        }

        private void drawHud(Graphics2D g) {
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            g.drawString(String.format("分数  %,d", score), 24, 34);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.setColor(new Color(220, 115, 255));
            g.drawString("连击 x" + combo, 25, 62);
            g.setColor(new Color(130, 215, 255));
            g.drawString("等级 " + level, 25, 89);

            int xpWidth = 180;
            g.setColor(new Color(255, 255, 255, 32));
            g.fillRoundRect(25, 100, xpWidth, 8, 8, 8);
            g.setColor(new Color(180, 95, 255));
            g.fillRoundRect(25, 100, (int) (xpWidth * experience / (double) experienceNeeded),
                    8, 8, 8);

            for (int i = 0; i < lives; i++) {
                int x = WIDTH - 36 - i * 28;
                g.setColor(new Color(255, 70, 125));
                g.fillOval(x - 8, 22, 16, 16);
            }
            if (shieldCharges > 0) {
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.setColor(new Color(90, 215, 255));
                g.drawString("护盾 x" + shieldCharges, WIDTH - 88, 57);
            }

            int barWidth = 150;
            int cooldownWidth = dashCooldown == 0 ? barWidth
                    : (int) (barWidth * (1 - dashCooldown / (double) dashCooldownMax));
            g.setColor(new Color(255, 255, 255, 35));
            g.fillRoundRect(WIDTH - 175, 72, barWidth, 9, 9, 9);
            g.setColor(dashCooldown == 0 ? new Color(255, 230, 90) : new Color(70, 180, 220));
            g.fillRoundRect(WIDTH - 175, 72, cooldownWidth, 9, 9, 9);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(205, 220, 245));
            g.drawString(dashCooldown == 0 ? "冲刺就绪 [空格]" : "冲刺充能中", WIDTH - 175, 99);
            g.drawString(audio.isMuted() ? "M: 开启声音" : "M: 静音", WIDTH - 88, HEIGHT - 18);
        }

        private void drawUpgradeToast(Graphics2D g) {
            int alpha = Math.min(220, upgradeToastFrames * 4);
            g.setColor(new Color(14, 20, 48, alpha));
            g.fillRoundRect(WIDTH / 2 - 145, 20, 290, 42, 18, 18);
            drawCenteredText(g, "获得强化：" + lastUpgrade, 48,
                    new Font("SansSerif", Font.BOLD, 16), new Color(255, 235, 120));
        }

        private void drawStartScreen(Graphics2D g) {
            g.setColor(new Color(3, 6, 18, 205));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            drawCenteredText(g, "霓 虹 逃 亡：超 载", 190,
                    new Font("SansSerif", Font.BOLD, 46), new Color(85, 235, 255));
            drawCenteredText(g, "收集晶体、升级构筑、冲穿追踪者", 252,
                    new Font("SansSerif", Font.PLAIN, 21), Color.WHITE);
            drawCenteredText(g, "WASD / 方向键移动    空格冲刺    P 暂停    M 静音", 315,
                    new Font("SansSerif", Font.BOLD, 16), new Color(215, 185, 255));
            drawCenteredText(g, "升级时按 1 / 2 / 3 选择强化", 354,
                    new Font("SansSerif", Font.PLAIN, 16), new Color(170, 205, 255));
            drawCenteredText(g, "按任意移动键开始", 430,
                    new Font("SansSerif", Font.BOLD, 23), new Color(255, 225, 95));
        }

        private void drawUpgradeScreen(Graphics2D g) {
            g.setColor(new Color(3, 5, 17, 225));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            drawCenteredText(g, "升 级 · 选 择 强 化", 105,
                    new Font("SansSerif", Font.BOLD, 34), new Color(115, 225, 255));
            drawCenteredText(g, "LEVEL " + level, 138,
                    new Font("SansSerif", Font.BOLD, 15), new Color(200, 140, 255));

            int cardWidth = 240;
            int cardHeight = 290;
            int gap = 24;
            int startX = (WIDTH - (cardWidth * 3 + gap * 2)) / 2;
            for (int i = 0; i < upgradeChoices.size(); i++) {
                Upgrade upgrade = upgradeChoices.get(i);
                int x = startX + i * (cardWidth + gap);
                g.setColor(new Color(20, 28, 65));
                g.fillRoundRect(x, 175, cardWidth, cardHeight, 24, 24);
                g.setColor(new Color(upgrade.color.getRed(), upgrade.color.getGreen(),
                        upgrade.color.getBlue(), 150));
                g.setStroke(new BasicStroke(3f));
                g.drawRoundRect(x, 175, cardWidth, cardHeight, 24, 24);
                g.setColor(upgrade.color);
                g.fillOval(x + cardWidth / 2 - 31, 205, 62, 62);
                drawTextAtCenter(g, String.valueOf(i + 1), x + cardWidth / 2, 249,
                        new Font("SansSerif", Font.BOLD, 28), new Color(12, 18, 40));
                drawTextAtCenter(g, upgrade.title, x + cardWidth / 2, 310,
                        new Font("SansSerif", Font.BOLD, 21), Color.WHITE);
                drawWrappedText(g, upgrade.description, x + 24, 348,
                        cardWidth - 48, 25, new Font("SansSerif", Font.PLAIN, 16),
                        new Color(195, 210, 235));
                drawTextAtCenter(g, "按 " + (i + 1), x + cardWidth / 2, 438,
                        new Font("SansSerif", Font.BOLD, 15), upgrade.color);
            }
            drawCenteredText(g, "时间已冻结，慢慢选", 520,
                    new Font("SansSerif", Font.PLAIN, 15), new Color(145, 160, 195));
        }

        private void drawGameOver(Graphics2D g) {
            g.setColor(new Color(4, 5, 16, 220));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            drawCenteredText(g, "信号中断", 190,
                    new Font("SansSerif", Font.BOLD, 48), new Color(255, 75, 130));
            drawCenteredText(g, String.format("最终分数  %,d", score), 255,
                    new Font("SansSerif", Font.BOLD, 27), Color.WHITE);
            drawCenteredText(g, "等级 " + level + "    击毁 " + kills
                            + "    晶体 " + crystals + "    最高连击 x" + bestCombo, 307,
                    new Font("SansSerif", Font.PLAIN, 18), new Color(210, 175, 255));
            drawCenteredText(g, "按 R 再来一局", 400,
                    new Font("SansSerif", Font.BOLD, 23), new Color(255, 225, 95));
        }

        private void drawCenteredMessage(Graphics2D g, String title, String subtitle) {
            g.setColor(new Color(4, 5, 16, 200));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            drawCenteredText(g, title, 285,
                    new Font("SansSerif", Font.BOLD, 46), Color.WHITE);
            drawCenteredText(g, subtitle, 340,
                    new Font("SansSerif", Font.PLAIN, 20), new Color(180, 210, 255));
        }

        private void drawCenteredText(Graphics2D g, String text, int y, Font font, Color color) {
            drawTextAtCenter(g, text, WIDTH / 2, y, font, color);
        }

        private void drawTextAtCenter(Graphics2D g, String text, int centerX,
                                      int y, Font font, Color color) {
            g.setFont(font);
            g.setColor(color);
            g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, y);
        }

        private void drawWrappedText(Graphics2D g, String text, int x, int y,
                                     int width, int lineHeight, Font font, Color color) {
            g.setFont(font);
            g.setColor(color);
            FontMetrics metrics = g.getFontMetrics();
            StringBuilder line = new StringBuilder();
            for (char character : text.toCharArray()) {
                if (metrics.stringWidth(line.toString() + character) > width) {
                    g.drawString(line.toString(), x, y);
                    line = new StringBuilder();
                    y += lineHeight;
                }
                line.append(character);
            }
            if (!line.isEmpty()) g.drawString(line.toString(), x, y);
        }

        private record Star(double x, double y, double size, double phase) {
        }

        private enum Upgrade {
            OVERDRIVE("超频引擎", "移动速度提高 13%，让走位更加灵活。",
                    new Color(75, 225, 255)),
            CAPACITOR("闪电电容", "冲刺冷却时间缩短 18%。",
                    new Color(255, 225, 75)),
            IMPACT_CORE("撞击核心", "冲刺距离和爆发速度大幅提升。",
                    new Color(255, 140, 65)),
            GRAVITY_WELL("引力井", "远距离吸引晶体，可重复强化。",
                    new Color(195, 90, 255)),
            SHOCKWAVE("湮灭脉冲", "拾取晶体时摧毁附近敌人，范围可叠加。",
                    new Color(135, 105, 255)),
            ORBIT_DRONE("轨道无人机", "增加一台环绕你的攻击无人机，最多四台。",
                    new Color(255, 190, 65)),
            PHASE_SHIELD("相位护盾", "获得一层抵挡伤害的护盾，最多储存三层。",
                    new Color(75, 195, 255)),
            REPAIR("紧急维修", "立即恢复一点生命值。",
                    new Color(255, 95, 135)),
            REINFORCED_HULL("强化机体", "生命上限与当前生命值各增加一点。",
                    new Color(255, 115, 115)),
            FORTUNE("财富协议", "所有得分提高 30%，可重复叠加。",
                    new Color(105, 235, 155)),
            TIME_DILATION("时间膨胀", "所有敌人的移动速度降低 9%。",
                    new Color(90, 155, 255));

            final String title;
            final String description;
            final Color color;

            Upgrade(String title, String description, Color color) {
                this.title = title;
                this.description = description;
                this.color = color;
            }
        }

        private static final class Enemy {
            double x;
            double y;
            double vx;
            double vy;
            final int radius;
            final double acceleration;
            final double maxSpeed;
            final double wobble;
            final Color color;
            final int phase;

            Enemy(double x, double y, int radius, double acceleration,
                  double maxSpeed, double wobble, Color color, int phase) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.acceleration = acceleration;
                this.maxSpeed = maxSpeed;
                this.wobble = wobble;
                this.color = color;
                this.phase = phase;
            }
        }

        private static final class Particle {
            double x;
            double y;
            double vx;
            double vy;
            final Color color;
            int life;
            final double size;

            Particle(double x, double y, double vx, double vy,
                     Color color, int life, double size) {
                this.x = x;
                this.y = y;
                this.vx = vx;
                this.vy = vy;
                this.color = color;
                this.life = life;
                this.size = size;
            }
        }
    }

    private enum Sound {
        DASH, HIT, PICKUP, DAMAGE, SHIELD, SHOCKWAVE, LEVEL_UP, SELECT, GAME_OVER
    }

    private static final class AudioEngine {
        private static final float SAMPLE_RATE = 22_050f;
        private volatile boolean muted;

        AudioEngine() {
            Thread musicThread = new Thread(this::musicLoop, "neon-music");
            musicThread.setDaemon(true);
            musicThread.start();
        }

        void toggleMute() {
            muted = !muted;
            if (!muted) play(Sound.SELECT);
        }

        boolean isMuted() {
            return muted;
        }

        void play(Sound sound) {
            if (muted) return;
            Thread effectThread = new Thread(() -> playEffect(sound), "neon-sfx");
            effectThread.setDaemon(true);
            effectThread.start();
        }

        private void playEffect(Sound sound) {
            try {
                double start;
                double end;
                double duration;
                double volume;
                boolean noise = false;
                switch (sound) {
                    case DASH -> {
                        start = 180;
                        end = 720;
                        duration = 0.12;
                        volume = 0.20;
                    }
                    case HIT -> {
                        start = 260;
                        end = 80;
                        duration = 0.09;
                        volume = 0.16;
                        noise = true;
                    }
                    case PICKUP -> {
                        start = 520;
                        end = 1040;
                        duration = 0.16;
                        volume = 0.18;
                    }
                    case DAMAGE -> {
                        start = 150;
                        end = 45;
                        duration = 0.28;
                        volume = 0.28;
                        noise = true;
                    }
                    case SHIELD -> {
                        start = 900;
                        end = 320;
                        duration = 0.22;
                        volume = 0.18;
                    }
                    case SHOCKWAVE -> {
                        start = 110;
                        end = 35;
                        duration = 0.32;
                        volume = 0.24;
                        noise = true;
                    }
                    case LEVEL_UP -> {
                        playNotes(new double[]{523.25, 659.25, 783.99, 1046.5},
                                0.11, 0.16);
                        return;
                    }
                    case SELECT -> {
                        start = 660;
                        end = 880;
                        duration = 0.10;
                        volume = 0.14;
                    }
                    case GAME_OVER -> {
                        playNotes(new double[]{392, 311.13, 246.94, 196},
                                0.20, 0.20);
                        return;
                    }
                    default -> throw new IllegalStateException();
                }
                playSweep(start, end, duration, volume, noise);
            } catch (Exception ignored) {
                // Audio is optional on systems without an available output line.
            }
        }

        private void musicLoop() {
            double[] melody = {
                    220, 0, 329.63, 0, 440, 392, 329.63, 0,
                    196, 0, 293.66, 0, 392, 329.63, 293.66, 0,
                    174.61, 0, 261.63, 0, 349.23, 329.63, 261.63, 0,
                    196, 246.94, 293.66, 392, 329.63, 293.66, 246.94, 0
            };
            int note = 0;
            while (true) {
                try {
                    double frequency = melody[note++ % melody.length];
                    playMusicNote(frequency, 0.145);
                } catch (Exception ignored) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        private void playMusicNote(double frequency, double duration) throws Exception {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format, (int) (SAMPLE_RATE * duration * 2));
            line.start();
            int samples = (int) (SAMPLE_RATE * duration);
            byte[] data = new byte[samples * 2];
            for (int i = 0; i < samples; i++) {
                double t = i / SAMPLE_RATE;
                double envelope = Math.min(1, i / 300.0)
                        * Math.min(1, (samples - i) / 800.0);
                double value = frequency == 0 || muted ? 0
                        : (Math.sin(2 * Math.PI * frequency * t)
                        + 0.28 * Math.sin(2 * Math.PI * frequency * 2 * t)) * 0.055 * envelope;
                short sample = (short) (value * Short.MAX_VALUE);
                data[i * 2] = (byte) sample;
                data[i * 2 + 1] = (byte) (sample >> 8);
            }
            line.write(data, 0, data.length);
            line.drain();
            line.close();
        }

        private void playSweep(double start, double end, double duration,
                               double volume, boolean addNoise) throws Exception {
            int samples = (int) (SAMPLE_RATE * duration);
            byte[] data = new byte[samples * 2];
            Random random = new Random();
            double phase = 0;
            for (int i = 0; i < samples; i++) {
                double progress = i / (double) samples;
                double frequency = start + (end - start) * progress;
                phase += 2 * Math.PI * frequency / SAMPLE_RATE;
                double envelope = Math.sin(Math.PI * progress);
                double value = Math.sin(phase);
                if (addNoise) value = value * 0.72 + (random.nextDouble() * 2 - 1) * 0.28;
                short sample = (short) (value * envelope * volume * Short.MAX_VALUE);
                data[i * 2] = (byte) sample;
                data[i * 2 + 1] = (byte) (sample >> 8);
            }
            writeAudio(data);
        }

        private void playNotes(double[] notes, double noteDuration, double volume) throws Exception {
            int samplesPerNote = (int) (SAMPLE_RATE * noteDuration);
            byte[] data = new byte[samplesPerNote * notes.length * 2];
            for (int n = 0; n < notes.length; n++) {
                for (int i = 0; i < samplesPerNote; i++) {
                    double progress = i / (double) samplesPerNote;
                    double envelope = Math.sin(Math.PI * progress);
                    double t = i / SAMPLE_RATE;
                    double value = Math.sin(2 * Math.PI * notes[n] * t)
                            + 0.25 * Math.sin(4 * Math.PI * notes[n] * t);
                    short sample = (short) (value * envelope * volume * Short.MAX_VALUE);
                    int offset = (n * samplesPerNote + i) * 2;
                    data[offset] = (byte) sample;
                    data[offset + 1] = (byte) (sample >> 8);
                }
            }
            writeAudio(data);
        }

        private void writeAudio(byte[] data) throws Exception {
            if (muted) return;
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(data, 0, data.length);
            line.drain();
            line.close();
        }
    }
}
