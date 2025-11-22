package scenes;

import core.SceneManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IntroScene extends Scene {

    private JFrame window;
    private JPanel panel;

    private float alpha = 0f;
    private Timer fadeTimer;

    private boolean fadingIn = true;
    private boolean forceFadeOut = false;

    // Slower fade speed
    private final int FADE_DELAY = 70;      // ms per step (slower)
    private final float FADE_STEP = 0.03f;  // slower fade

    private final int DISPLAY_TIME = 2000;  // stay visible before fade-out

    private Image introImage;

    @Override
    public void showScene(JFrame window) {
        this.window = window;

        introImage = new ImageIcon("assets/images/intro.png").getImage();

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Black background
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Fade image
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                int x = (getWidth() - introImage.getWidth(null)) / 2;
                int y = (getHeight() - introImage.getHeight(null)) / 2;

                g2d.drawImage(introImage, x, y, null);
                g2d.dispose();
            }
        };

        // Clicking forces fade-out immediately
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                forceFadeOut = true;
                startFadeOut();
            }
        });

        window.setContentPane(panel);
        window.revalidate();
        window.setVisible(true);

        startFadeIn();
    }

    // ============================
    // FADE IN
    // ============================
    private void startFadeIn() {
        fadingIn = true;
        forceFadeOut = false;

        stopFadeTimer();

        fadeTimer = new Timer(FADE_DELAY, e -> {
            alpha += FADE_STEP;

            if (alpha >= 1f) {
                alpha = 1f;
                stopFadeTimer();

                // Wait, then fade out—only if not clicked early
                new Timer(DISPLAY_TIME, ev -> {
                    if (!forceFadeOut) {
                        startFadeOut();
                    }
                }).start();
            }

            panel.repaint();
        });

        fadeTimer.start();
    }

    // ============================
    // FADE OUT
    // ============================
    private void startFadeOut() {
        if (!fadingIn && fadeTimer != null) return; // already fading out

        fadingIn = false;
        stopFadeTimer();

        fadeTimer = new Timer(FADE_DELAY, e -> {
            alpha -= FADE_STEP;

            if (alpha <= 0f) {
                alpha = 0f;
                stopFadeTimer();
                goToNextScene();
            }

            panel.repaint();
        });

        fadeTimer.start();
    }

    // Safely stop timer
    private void stopFadeTimer() {
        if (fadeTimer != null) {
            fadeTimer.stop();
            fadeTimer = null;
        }
    }

    // ============================
    // TRANSITION
    // ============================
    private void goToNextScene() {
        System.out.println("Intro finished! Switching to MenuScene...");
        SceneManager.setScene(new MenuScene());
    }

    @Override
    public void hideScene() {
        stopFadeTimer();
        if (window != null) {
            window.getContentPane().remove(panel);
        }
    }
}
