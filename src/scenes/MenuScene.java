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

    // Wall boundaries (as requested by the user, these restrict vertical movement)
    private static final int TOP_WALL_Y = 348;
    private static final int BOTTOM_WALL_Y = 636;

    // Menu options
    private Rectangle playButton;
    private Rectangle exitButton;

    // Tracks if the player is currently intersecting a button area
    private boolean isIntersectingPlay = false;
    private boolean isIntersectingExit = false;

    public MenuScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/MenuScene.png");

        // Initialize player at starting position
        player = new Player(100, 300);

        // Define button areas (used for player interaction)
        playButton = new Rectangle(300, 500, 200, 60);
        exitButton = new Rectangle(300, 400, 200, 60);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Update player movement
        player.update(deltaTime, input);

        // Apply explicit top and bottom wall boundaries
        if (player.getY() < TOP_WALL_Y) {
            player.setY(TOP_WALL_Y);
        }
        if (player.getY() + Constants.PLAYER_HEIGHT > BOTTOM_WALL_Y) {
            player.setY(BOTTOM_WALL_Y - Constants.PLAYER_HEIGHT);
        }

        // Keep player within left/right bounds
        if (player.getX() < 0) {
            player.setX(0);
        }
        if (player.getX() > Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH);
        }

        // Now get the updated bounds AFTER applying wall clamping
        Rectangle playerBounds = player.getBounds();

        // Update intersection status
        isIntersectingPlay = playerBounds.intersects(playButton);
        isIntersectingExit = playerBounds.intersects(exitButton);

        // Check for interaction
        if (input.isSpacePressed()) {
            if (isIntersectingPlay) {
                sceneManager.switchScene(Constants.SCENE_BUFFER);
            } else if (isIntersectingExit) {
                System.exit(0);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Draw background image if loaded
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Draw menu buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 28);
        g.setFont(buttonFont);

        // Play Button
        g.setColor(isIntersectingPlay ? new Color(100, 200, 255) : new Color(50, 150, 200));
        g.fillRect(playButton.x, playButton.y, playButton.width, playButton.height);
        g.setColor(Color.WHITE);
        g.drawString("PLAY GAME", playButton.x + 20, playButton.y + 40);

        // Exit Button
        g.setColor(isIntersectingExit ? new Color(255, 100, 100) : new Color(200, 50, 50));
        g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.WHITE);
        g.drawString("EXIT", exitButton.x + 65, exitButton.y + 40);

        // Draw player
        player.render(g);

        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("WASD: Move | SPACE: Interact with menu option", 20, Constants.WINDOW_HEIGHT - 30);
    }
}