package tools;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AssetGenerator {
    private static final Color BACKGROUND = new Color(12, 15, 27);
    private static final Color PANEL = new Color(25, 31, 52);
    private static final Color FIRE = new Color(220, 86, 44);
    private static final Color WATER = new Color(60, 150, 235);
    private static final Color THUNDER = new Color(235, 190, 48);
    private static final Color ACCENT = new Color(112, 210, 255);
    private static final Color TEXT = new Color(238, 243, 255);

    public static void main(String[] args) throws IOException {
        createFolders();
        writeIcon("assets/icons/fire.png", FIRE, "fire");
        writeIcon("assets/icons/water.png", WATER, "water");
        writeIcon("assets/icons/thunder.png", THUNDER, "thunder");
        writePortrait("assets/characters/player.png", ACCENT, "P", "Player");
        writePortrait("assets/characters/training_dummy.png", new Color(150, 120, 80), "TD", "Training Dummy");
        writePortrait("assets/characters/fire_spirit.png", FIRE, "FS", "Fire Spirit");
        writePortrait("assets/characters/thunder_beast.png", THUNDER, "TB", "Thunder Beast");
        writeBackground("assets/ui/background.png");
        writeCardFrame("assets/ui/card_frame.png");
    }

    private static void createFolders() {
        new File("assets/icons").mkdirs();
        new File("assets/characters").mkdirs();
        new File("assets/ui").mkdirs();
    }

    private static void writeIcon(String path, Color color, String type) throws IOException {
        int size = 128;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        setup(graphics);
        drawGlow(graphics, size, size, color);
        graphics.setColor(color);

        if (type.equals("fire")) {
            Path2D flame = new Path2D.Double();
            flame.moveTo(64, 14);
            flame.curveTo(92, 42, 96, 62, 82, 96);
            flame.curveTo(72, 118, 38, 112, 31, 88);
            flame.curveTo(25, 66, 42, 47, 52, 31);
            flame.curveTo(52, 50, 66, 54, 64, 14);
            graphics.fill(flame);
            graphics.setColor(new Color(255, 210, 110));
            graphics.fillOval(52, 70, 24, 34);
        } else if (type.equals("water")) {
            Path2D drop = new Path2D.Double();
            drop.moveTo(64, 16);
            drop.curveTo(94, 54, 104, 78, 86, 102);
            drop.curveTo(72, 122, 38, 118, 28, 94);
            drop.curveTo(19, 72, 39, 48, 64, 16);
            graphics.fill(drop);
            graphics.setColor(new Color(170, 225, 255));
            graphics.drawArc(44, 54, 30, 36, 100, 120);
        } else {
            Path2D bolt = new Path2D.Double();
            bolt.moveTo(72, 10);
            bolt.lineTo(36, 68);
            bolt.lineTo(63, 68);
            bolt.lineTo(50, 118);
            bolt.lineTo(94, 52);
            bolt.lineTo(67, 52);
            bolt.closePath();
            graphics.fill(bolt);
            graphics.setColor(new Color(255, 245, 150));
            graphics.setStroke(new BasicStroke(5));
            graphics.draw(bolt);
        }

        graphics.dispose();
        ImageIO.write(image, "png", new File(path));
    }

    private static void writePortrait(String path, Color color, String initials, String name) throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        setup(graphics);
        gradientBackground(graphics, width, height, PANEL, BACKGROUND);
        drawGlow(graphics, width, height, color);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        graphics.fill(new Ellipse2D.Double(46, 34, 164, 164));
        graphics.setColor(color.brighter());
        graphics.setStroke(new BasicStroke(5));
        graphics.draw(new Ellipse2D.Double(46, 34, 164, 164));
        graphics.setColor(TEXT);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 54));
        drawCenteredText(graphics, initials, width, 130);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 22));
        drawCenteredText(graphics, name, width, 224);
        graphics.dispose();
        ImageIO.write(image, "png", new File(path));
    }

    private static void writeBackground(String path) throws IOException {
        int width = 1280;
        int height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        setup(graphics);
        gradientBackground(graphics, width, height, new Color(8, 11, 24), new Color(25, 31, 58));

        for (int i = 0; i < 90; i++) {
            int x = (i * 137) % width;
            int y = (i * 83) % height;
            int radius = 1 + i % 3;
            graphics.setColor(new Color(120, 180, 255, 45 + i % 60));
            graphics.fillOval(x, y, radius, radius);
        }

        graphics.setColor(new Color(112, 210, 255, 35));
        graphics.setStroke(new BasicStroke(2));
        for (int i = 0; i < 8; i++) {
            graphics.drawOval(160 + i * 120, 120 + i * 28, 420, 420);
        }

        graphics.dispose();
        ImageIO.write(image, "png", new File(path));
    }

    private static void writeCardFrame(String path) throws IOException {
        int width = 300;
        int height = 420;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        setup(graphics);
        graphics.setColor(new Color(8, 10, 20, 230));
        graphics.fillRoundRect(0, 0, width, height, 28, 28);
        graphics.setColor(new Color(255, 255, 255, 45));
        graphics.setStroke(new BasicStroke(6));
        graphics.drawRoundRect(8, 8, width - 16, height - 16, 24, 24);
        graphics.setColor(new Color(112, 210, 255, 70));
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(22, 22, width - 44, height - 44, 18, 18);
        graphics.dispose();
        ImageIO.write(image, "png", new File(path));
    }

    private static void setup(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static void gradientBackground(Graphics2D graphics, int width, int height, Color top, Color bottom) {
        for (int y = 0; y < height; y++) {
            double ratio = y / (double) height;
            int red = (int) (top.getRed() * (1 - ratio) + bottom.getRed() * ratio);
            int green = (int) (top.getGreen() * (1 - ratio) + bottom.getGreen() * ratio);
            int blue = (int) (top.getBlue() * (1 - ratio) + bottom.getBlue() * ratio);
            graphics.setColor(new Color(red, green, blue));
            graphics.drawLine(0, y, width, y);
        }
    }

    private static void drawGlow(Graphics2D graphics, int width, int height, Color color) {
        for (int i = 0; i < 8; i++) {
            int alpha = 70 - i * 8;
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(alpha, 8)));
            graphics.fillOval(18 + i * 8, 18 + i * 8, width - 36 - i * 16, height - 36 - i * 16);
        }
    }

    private static void drawCenteredText(Graphics2D graphics, String text, int width, int baseline) {
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (width - metrics.stringWidth(text)) / 2;
        graphics.drawString(text, x, baseline);
    }
}
