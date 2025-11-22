package scenes;

import core.SceneManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScene extends Scene {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 960;
    private float alpha = 0f;

    private JFrame gameWindow;
    private JPanel panel;

    public MenuScene() {
        // Empty constructor, window will be passed in showScene()
    }

    @Override
    public void showScene(JFrame window) {
        this.gameWindow = window;

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw((Graphics2D) g);
            }
        };
        panel.setBackground(Color.DARK_GRAY);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Example: next scene
                System.out.println("Menu clicked!");
            }
        });

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        window.setContentPane(panel);
        window.revalidate();
        window.repaint();

        startFadeIn();
    }

    private void draw(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        String text = "MENU SCENE";
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

    @Override
    public void hideScene() {
        if (gameWindow != null && panel != null) {
            gameWindow.getContentPane().remove(panel);
        }
    }
}
