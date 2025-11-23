package core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetLoader {
    private static Map<String, BufferedImage> images = new HashMap<>();
    private static Map<String, Font> fonts = new HashMap<>();

    public static void initialize() {
        // Load default font
        fonts.put("default", new Font("Arial", Font.PLAIN, 24));
        fonts.put("title", new Font("Arial", Font.BOLD, 48));
    }

    public static BufferedImage loadImage(String path) {
        if (images.containsKey(path)) {
            return images.get(path);
        }

        try {
            File file = new File("assets/" + path);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                images.put(path, img);
                return img;
            }
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
        }

        // Return placeholder if image not found
        return createPlaceholder(64, 64, path);
    }

    public static BufferedImage createPlaceholder(int width, int height, String label) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Draw colored rectangle based on label
        Color color = getColorFromLabel(label);
        g.setColor(color);
        g.fillRect(0, 0, width, height);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);

        // Draw label
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        String shortLabel = label.length() > 15 ? label.substring(0, 12) + "..." : label;
        g.drawString(shortLabel, 5, height / 2);

        g.dispose();
        return img;
    }

    private static Color getColorFromLabel(String label) {
        if (label.contains("player") || label.contains("Player")) return new Color(100, 150, 255);
        if (label.contains("popcorn") || label.contains("Popcorn")) return new Color(255, 200, 100);
        if (label.contains("drink") || label.contains("Drink")) return new Color(255, 100, 100);
        if (label.contains("ticket") || label.contains("Ticket")) return new Color(200, 200, 255);
        if (label.contains("machine") || label.contains("Machine")) return new Color(150, 150, 150);
        if (label.contains("customer") || label.contains("Customer")) return new Color(255, 180, 200);
        return new Color(100, 100, 100);
    }

    public static Font getFont(String name) {
        return fonts.getOrDefault(name, fonts.get("default"));
    }
}