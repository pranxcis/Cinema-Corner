package scenes;

import core.*;
import entities.*;
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

    public LobbyScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/LobbyScene.png");

        // Initialize player
        player = new Player(100, 400);

        // Initialize machines
        popcornMachine = new PopcornMachine(200, 200);
        drinkMachine = new DrinkMachine(400, 200);
        ticketMachine = new TicketMachine(600, 200);
        trash = new Trash(50, 300);

        // Set machines to ready state
        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);

        // HUD
        hud = new HUD();
        hud.setDay(1);

        // Start trigger (specific area on map)
        startTrigger = new Rectangle(650, 400, 100, 100);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Update player
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(deltaTime, input);

        // Check collisions with machines and walls
        if (CollisionUtil.checkMachineCollision(player, popcornMachine) ||
                CollisionUtil.checkMachineCollision(player, drinkMachine) ||
                CollisionUtil.checkMachineCollision(player, ticketMachine) ||
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
        checkInteractables();

        // Check for interactions
        if (input.isEJustPressed()) {
            handleInteractions();
        }

        // Check for trash
        if (input.isTJustPressed()) {
            if (player.getItemState() != Constants.ITEM_NONE) {
                player.setItemState(Constants.ITEM_NONE);
                hud.addMoney(-10); // Penalty
            }
        }

        // Check start trigger
        if (!gameStarted && player.getBounds().intersects(startTrigger)) {
            hud.setInteractMessage("Press E to start Day 1");
            if (input.isEJustPressed()) {
                gameStarted = true;
                sceneManager.switchScene(Constants.SCENE_BUFFER);
            }
        }
    }

    private void checkInteractables() {
        Rectangle playerBounds = player.getBounds();
        Rectangle interactionArea = new Rectangle(
                player.getX() - 20, player.getY() - 20,
                Constants.PLAYER_WIDTH + 40, Constants.PLAYER_HEIGHT + 40
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
                player.getX() - 20, player.getY() - 20,
                Constants.PLAYER_WIDTH + 40, Constants.PLAYER_HEIGHT + 40
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

    @Override
    public void render(Graphics2D g) {
        // Draw background
        g.setColor(new Color(180, 180, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Draw floor
        g.setColor(new Color(100, 100, 120));
        g.fillRect(0, Constants.WINDOW_HEIGHT - 100, Constants.WINDOW_WIDTH, 100);

        // Draw machines
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);
        trash.render(g);

        // Draw start trigger
        g.setColor(new Color(0, 255, 0, 100));
        g.fillRect(startTrigger.x, startTrigger.y, startTrigger.width, startTrigger.height);
        g.setColor(Color.WHITE);
        g.drawString("START", startTrigger.x + 25, startTrigger.y + 55);

        // Draw player
        player.render(g);

        // Draw HUD
        hud.render(g);
    }
}