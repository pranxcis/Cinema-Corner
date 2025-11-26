package scenes;

import core.*;
import entities.Player;
import systems.AudioSystem;
import utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EndScene extends Scene {
    private BufferedImage backgroundImage;
    private Player player;

    // Wall boundaries (same as MenuScene)
    private static final int TOP_WALL_Y = 348;
    private static final int BOTTOM_WALL_Y = 750;

    // Menu option hitboxes
    private Rectangle playButton;      // Return to Menu (Lobby)
    private Rectangle elevatorButton;  // Replay
    private Rectangle exitButton;      // Exit Game

    // Intersection states
    private boolean isIntersectingPlay = false;
    private boolean isIntersectingElevator = false;
    private boolean isIntersectingExit = false;

    // Fade-in control
    private int fadeAlpha = 0;
    private int fadeSpeed = 3;

    public EndScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/EndScene.png");

        // Player starts here
        player = new Player(100, 500);

        // Buttons (follow your exact coordinates)
        playButton      = new Rectangle(1160, 500, 30, 400);   // Lobby
        elevatorButton  = new Rectangle(560, 280, 160, 150);   // Replay
        exitButton      = new Rectangle(295, 280, 100, 150);   // Exit

        fadeAlpha = 0;

        AudioSystem.getInstance().playGameCompletedMusic();
    }

    @Override
    public void update(long deltaTime, InputHandler input) {

        // Fade in
        if (fadeAlpha < 255) fadeAlpha += fadeSpeed;
        if (fadeAlpha > 255) fadeAlpha = 255;

        // Update player movement
        player.update(deltaTime, input);

        // Top and bottom wall boundaries
        if (player.getY() < TOP_WALL_Y) {
            player.setY(TOP_WALL_Y);
        }
        if (player.getY() + Constants.PLAYER_HEIGHT > BOTTOM_WALL_Y) {
            player.setY(BOTTOM_WALL_Y - Constants.PLAYER_HEIGHT);
        }

        // Left/right boundaries
        if (player.getX() < 0) player.setX(0);
        if (player.getX() > Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH)
            player.setX(Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH);

        // Updated bounds
        Rectangle pb = player.getBounds();

        // Intersection checking
        isIntersectingPlay      = pb.intersects(playButton);
        isIntersectingElevator  = pb.intersects(elevatorButton);
        isIntersectingExit      = pb.intersects(exitButton);

        // Interaction
        if (input.isSpacePressed()) {

            AudioSystem.getInstance().playInteract();

            if (isIntersectingPlay) {
                sceneManager.switchScene(Constants.SCENE_MENU);      // Go lobby
            }
            else if (isIntersectingElevator) {
                sceneManager.switchScene(Constants.SCENE_BUFFER);    // Replay day
            }
            else if (isIntersectingExit) {
                System.exit(0);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {

        // Background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0,
                    Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Player
        player.render(g);

        // ======================
        //  FADE-IN SUMMARY BOX
        // ======================
        int boxWidth = 500;
        int boxHeight = 100;
        int boxX = (Constants.WINDOW_WIDTH - boxWidth) / 2;
        int boxY = 180;

        Color fadeBox = new Color(0, 0, 0, Math.min(fadeAlpha, 180));
        g.setColor(fadeBox);
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Title text
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(255, 255, 255, fadeAlpha));
        String title = "GAME COMPLETED!";
        int titleX = (Constants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, titleX, boxY + 60);

        // Debug boxes
        /*
        g.setColor(new Color(255,0,0,100)); g.fill(playButton);
        g.setColor(new Color(0,255,0,100)); g.fill(elevatorButton);
        g.setColor(new Color(0,0,255,100)); g.fill(exitButton);
        */
    }
}
