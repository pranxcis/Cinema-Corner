package scenes;

import core.*;
import entities.Player;
import systems.AudioSystem;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuScene extends Scene {
    private BufferedImage backgroundImage;
    private Player player;

    // Wall boundaries (restrict vertical movement)
    private static final int TOP_WALL_Y = 348;
    private static final int BOTTOM_WALL_Y = 750;

    // Menu options
    private Rectangle playButton;
    private Rectangle elevatorButton;
    private Rectangle exitButton;

    // Tracks if the player is currently intersecting a button area
    private boolean isIntersectingPlay = false;
    private boolean isIntersectingDoor = false;
    private boolean isIntersectingExit = false;

    public MenuScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/MenuScene.png");

        // Initialize player at starting position
        player = new Player(0, 490);

        // Define button areas (used for player interaction)
        playButton = new Rectangle(1160, 500, 30, 400);
        elevatorButton = new Rectangle(560, 280, 160, 150);
        exitButton = new Rectangle(295, 280, 100, 150);

        // Play menu music
        AudioSystem.getInstance().playMenuMusic();
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        System.out.println(player.getX() + " " + player.getY());

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

        // Get the updated bounds AFTER applying wall clamping
        Rectangle playerBounds = player.getBounds();

        // Update intersection status
        isIntersectingPlay = playerBounds.intersects(playButton);
        isIntersectingDoor = playerBounds.intersects(elevatorButton);
        isIntersectingExit = playerBounds.intersects(exitButton);

        // Check for interaction
        if (input.isSpacePressed()) {
            // Play interact sound
            AudioSystem.getInstance().playInteract();

            if (isIntersectingPlay) {
                sceneManager.switchScene(Constants.SCENE_BUFFER);
            } else if (isIntersectingDoor) {
                System.out.println("Can't Access This Right Now");
            } else if (isIntersectingExit) {
                System.exit(0);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Draw background image if loaded
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Draw player
        player.render(g);

        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("WASD: Move | SPACE: Interact with menu option", 20, Constants.WINDOW_HEIGHT - 30);
    }
}