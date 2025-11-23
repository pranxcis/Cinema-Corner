package scenes;

import core.*;
import entities.Player;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuScene extends Scene {
    private BufferedImage backgroundImage;
    private Player player;

    // Wall boundaries
    private static final int TOP_WALL_Y = 348;
    private static final int BOTTOM_WALL_Y = 636;

    // Door areas
    private Rectangle exitDoor;
    private Rectangle decoyDoor;

    // Play area trigger
    private static final int PLAY_AREA_X = 1160;

    public MenuScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/MenuScene.png");

        // Initialize player at starting position
        player = new Player(33, 490);

        // Define door hitboxes
        exitDoor = new Rectangle(310, 333, 80, 100);
        decoyDoor = new Rectangle(590, 342, 100, 100);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {

        // Update player movement
        player.update(deltaTime, input);

        // Apply wall boundaries
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

        // Check if player reached play area (right edge)
        if (player.getX() >= PLAY_AREA_X - Constants.PLAYER_WIDTH) {
            sceneManager.switchScene(Constants.SCENE_BUFFER);
            return;
        }

        // Check for door interactions
        Rectangle playerBounds = player.getBounds();

        if (input.isSpaceJustPressed()) {
            // Exit door - quit game
            if (playerBounds.intersects(exitDoor)) {
                System.exit(0);
            }
            // Decoy door - does nothing (locked)
            else if (playerBounds.intersects(decoyDoor)) {
                System.out.println("This door is locked!");
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

        // Draw player
        player.render(g);

        // Draw debug boundaries (optional - remove in production)
        //g.setColor(new Color(255, 0, 0, 150));
        //g.drawLine(0, TOP_WALL_Y, Constants.WINDOW_WIDTH, TOP_WALL_Y); // Top wall
        //g.drawLine(0, BOTTOM_WALL_Y, Constants.WINDOW_WIDTH, BOTTOM_WALL_Y); // Bottom wall

        // Draw door hitboxes (optional - remove in production)
        //g.setColor(new Color(0, 255, 0, 100));
        //g.fillRect(exitDoor.x, exitDoor.y, exitDoor.width, exitDoor.height);
        //g.setColor(Color.WHITE);
        //g.drawString("EXIT", exitDoor.x + 15, exitDoor.y + 50);

        //g.setColor(new Color(255, 255, 0, 100));
        //g.fillRect(decoyDoor.x, decoyDoor.y, decoyDoor.width, decoyDoor.height);
        //g.setColor(Color.WHITE);
        //g.drawString("LOCKED", decoyDoor.x + 5, decoyDoor.y + 50);

        // Draw play area indicator (optional - remove in production)
        //g.setColor(new Color(0, 0, 255, 150));
        //g.drawLine(PLAY_AREA_X, 0, PLAY_AREA_X, Constants.WINDOW_HEIGHT);
        //g.setFont(new Font("Arial", Font.BOLD, 20));
        //g.drawString("PLAY →", PLAY_AREA_X - 70, 300);

        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("WASD: Move | SPACE: Interact  | Go right to Play", 20, Constants.WINDOW_HEIGHT - 30);
    }
}