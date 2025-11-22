package scenes;

import javax.swing.*;
import java.awt.*;

public class GameplayScene {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 960;
    private float alpha = 0f;

    private JFrame gameWindow;
    private JPanel panel;

    public GameplayScene(JFrame gameWindow) {
        this.gameWindow = gameWindow;

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw((Graphics2D) g);
            }
        };
        panel.setBackground(Color.BLUE);
    }

    public void showScene() {
        gameWindow.setContentPane(panel);
        gameWindow.revalidate();
        gameWindow.repaint();
        startFadeIn();
    }

    private void draw(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        String text = "GAMEPLAY SCENE";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (WIDTH - textWidth) / 2, HEIGHT / 2);
    }

    private void startFadeIn() {
        Timer timer = new Timer(30, e -> {
            alpha += 0.02f;
            if (alpha >= 1f) {
                alpha = 1f;
                ((Timer) e.getSource()).stop();
            }
            panel.repaint();
        });
        timer.start();
    }
}
