package scenes;

import core.*;
import entities.*;
import systems.AudioSystem;
import ui.HUD;
import utils.Constants;
import utils.CollisionUtil;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LobbyScene extends Scene {
    private BufferedImage backgroundImage;
    private Player player;
    private PopcornMachine popcornMachine;
    private DrinkMachine drinkMachine;
    private TicketMachine ticketMachine;
    private Trash trash;
    private HUD hud;

    private Rectangle startTrigger;
    private boolean gameStarted = false;
    private static int currentDay = 1; // Track current day

    public LobbyScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/LobbyScene.png");

        // Initialize player
        player = new Player(100, 400);

        // Initialize machines at exact positions
        popcornMachine = new PopcornMachine(580, 220);
        drinkMachine = new DrinkMachine(760, 200);
        ticketMachine = new TicketMachine(930, 220);
        trash = new Trash(1130, 320);

        // Set machines to ready state
        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);

        // HUD
        hud = new HUD();
        hud.setDay(currentDay);

        // Start trigger at exact position
        startTrigger = new Rectangle(200, 400, 70, 70);

        // Reset game started flag
        gameStarted = false;

        // Play lobby music
        AudioSystem.getInstance().playLobbyMusic();

        System.out.println("LobbyScene initialized for Day " + currentDay);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {

        System.out.println(player.getX() + " " + player.getY());
        // Update player
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(deltaTime, input);

        // Check collisions with machines, walls, and boundaries
        if (CollisionUtil.checkWallCollision(player) ||
                !CollisionUtil.isWithinBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)) {
            player.setX(oldX);
            player.setY(oldY);
        }

        // Update machines
        popcornMachine.update(deltaTime);
        drinkMachine.update(deltaTime);
        ticketMachine.update(deltaTime);

        // Update HUD
        hud.setHeldItem(player.getItemState());
        hud.setDay(currentDay);
        checkInteractables();

        // Check for interactions
        if (input.isEPressed()) {
            AudioSystem.getInstance().playInteract();
            handleInteractions();
        }

        // Check for trash
        if (input.isTJustPressed()) {
            if (player.getItemState() != Constants.ITEM_NONE) {
                AudioSystem.getInstance().playInteract();
                player.setItemState(Constants.ITEM_NONE);
                hud.addMoney(-10); // Penalty
            }
        }

        // Check start trigger
        if (!gameStarted && player.getBounds().intersects(startTrigger)) {
            hud.setInteractMessage("Press E to start Day " + currentDay);
            if (input.isEPressed()) {
                AudioSystem.getInstance().playInteract();
                gameStarted = true;
                sceneManager.switchScene(Constants.SCENE_BUFFER);
            }
        }
    }

    private void checkInteractables() {
        Rectangle interactionArea = new Rectangle(
                player.getX() - 10, player.getY() - 10,
                Constants.PLAYER_WIDTH + 20, Constants.PLAYER_HEIGHT + 20
        );

        boolean nearSomething = false;

        if (interactionArea.intersects(popcornMachine.getBounds())) {
            hud.setInteractMessage("Press E to interact with Popcorn Machine");
            nearSomething = true;
        } else if (interactionArea.intersects(drinkMachine.getBounds())) {
            hud.setInteractMessage("Press E to interact with Drink Machine");
            nearSomething = true;
        } else if (interactionArea.intersects(ticketMachine.getBounds())) {
            hud.setInteractMessage("Press E to interact with Ticket Machine");
            nearSomething = true;
        } else if (interactionArea.intersects(trash.getBounds())) {
            hud.setInteractMessage("Press T to trash item");
            nearSomething = true;
        }

        hud.setNearInteractable(nearSomething);
        if (!nearSomething) {
            hud.setInteractMessage("");
        }
    }

    private void handleInteractions() {
        Rectangle interactionArea = new Rectangle(
                player.getX() - 10, player.getY() - 10,
                Constants.PLAYER_WIDTH + 20, Constants.PLAYER_HEIGHT + 20
        );

        // Check popcorn machine
        if (interactionArea.intersects(popcornMachine.getBounds())) {
            if (popcornMachine.canDispense()) {
                if (player.getItemState() == Constants.ITEM_NONE) {
                    player.setItemState(Constants.ITEM_POPCORN);
                    popcornMachine.dispense();
                } else if (player.getItemState() == Constants.ITEM_DRINK) {
                    player.setItemState(Constants.ITEM_BOTH);
                    popcornMachine.dispense();
                }
            } else if (popcornMachine.getState() == Constants.MACHINE_EMPTY) {
                popcornMachine.startRefill();
            }
        }

        // Check drink machine
        if (interactionArea.intersects(drinkMachine.getBounds())) {
            if (drinkMachine.canDispense()) {
                if (player.getItemState() == Constants.ITEM_NONE) {
                    player.setItemState(Constants.ITEM_DRINK);
                    drinkMachine.dispense();
                } else if (player.getItemState() == Constants.ITEM_POPCORN) {
                    player.setItemState(Constants.ITEM_BOTH);
                    drinkMachine.dispense();
                }
            } else if (drinkMachine.getState() == Constants.MACHINE_EMPTY) {
                drinkMachine.startRefill();
            }
        }

        // Check ticket machine
        if (interactionArea.intersects(ticketMachine.getBounds())) {
            if (ticketMachine.canDispense() && player.getItemState() == Constants.ITEM_NONE) {
                player.setItemState(Constants.ITEM_TICKET);
                ticketMachine.dispense();
            } else if (ticketMachine.getState() == Constants.MACHINE_EMPTY) {
                ticketMachine.startPrinting();
            }
        }
    }

    // Static method to set the current day
    public static void setCurrentDay(int day) {
        currentDay = day;
        System.out.println("LobbyScene - Day set to: " + day);
    }

    // Static method to get current day
    public static int getCurrentDay() {
        return currentDay;
    }

    @Override
    public void render(Graphics2D g) {
        // Draw background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Draw machines
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);
        trash.render(g);

        //Draw Oval
        g.setColor(new Color(255, 0, 0, 100)); // RED with alpha
        g.fillOval(startTrigger.x, startTrigger.y, startTrigger.width, startTrigger.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("Start Day " + currentDay, startTrigger.x, startTrigger.y - 10);

        // Draw player
        player.render(g);

        // Draw HUD
        hud.render(g);
    }
}