package scenes;

import core.*;
import entities.Player;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public class MenuScene extends Scene {
    private BufferedImage backgroundImage;
    private Player player;

    // Menu options
    private Rectangle playButton;
    private Rectangle exitButton;
    private boolean onPlayButton = false;

    public MenuScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/MenuScene/menu_bg.png");

        // Initialize player at starting position
        player = new Player(100, 300);

        // Define button areas
        playButton = new Rectangle(300, 250, 200, 60);
        exitButton = new Rectangle(300, 350, 200, 60);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Update player movement
        player.update(deltaTime, input);

        // Check if player is near buttons
        Rectangle playerBounds = player.getBounds();
        onPlayButton = playerBounds.intersects(playButton);

        // Check for interaction
        if (input.isSpaceJustPressed()) {
            if (onPlayButton || playerBounds.intersects(playButton)) {
                sceneManager.switchScene(Constants.SCENE_BUFFER);
            } else if (playerBounds.intersects(exitButton)) {
                System.exit(0);
            }
        }

        // Keep player within bounds
        if (player.getX() < 0) player.setX(0);
        if (player.getY() < 0) player.setY(0);
        if (player.getX() > Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH);
        }
        if (player.getY() > Constants.WINDOW_HEIGHT - Constants.PLAYER_HEIGHT) {
            player.setY(Constants.WINDOW_HEIGHT - Constants.PLAYER_HEIGHT);
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Draw background
        g.setColor(new Color(20, 20, 40));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Draw title
        g.setFont(AssetLoader.getFont("title"));
        g.setColor(Color.YELLOW);
        String title = "CinemaCorner";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 150);

        // Draw buttons
        g.setFont(AssetLoader.getFont("default"));

        // Play button
        g.setColor(onPlayButton ? new Color(100, 200, 100) : new Color(70, 150, 70));
        g.fillRect(playButton.x, playButton.y, playButton.width, playButton.height);
        g.setColor(Color.BLACK);
        g.drawRect(playButton.x, playButton.y, playButton.width, playButton.height);
        g.setColor(Color.WHITE);
        String playText = "PLAY";
        int playTextX = playButton.x + (playButton.width - g.getFontMetrics().stringWidth(playText)) / 2;
        g.drawString(playText, playTextX, playButton.y + 38);

        // Exit button
        g.setColor(new Color(150, 70, 70));
        g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.BLACK);
        g.drawRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.WHITE);
        String exitText = "EXIT";
        int exitTextX = exitButton.x + (exitButton.width - g.getFontMetrics().stringWidth(exitText)) / 2;
        g.drawString(exitText, exitTextX, exitButton.y + 38);

        // Draw player
        player.render(g);

        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("Use WASD to move, SPACE to select", 20, Constants.WINDOW_HEIGHT - 30);
    }
}