package scenes;

import core.*;
import entities.*;
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

    public LobbyScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/LobbyScene/lobby_bg.png");

        // Initialize player
        player = new Player(100, 400);

        // Initialize machines
        popcornMachine = new PopcornMachine(300, 200);
        drinkMachine = new DrinkMachine(500, 200);
        ticketMachine = new TicketMachine(400, 350);

        // Set machines to ready state
        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);
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

        // Check for interactions
        if (input.isSpaceJustPressed()) {
            handleInteractions();
        }
    }

    private void handleInteractions() {
        Rectangle playerBounds = player.getBounds();
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

        // Draw floor
        g.setColor(new Color(100, 100, 120));
        g.fillRect(0, Constants.WINDOW_HEIGHT - 100, Constants.WINDOW_WIDTH, 100);

        // Draw machines
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);

        // Draw player
        player.render(g);

        // Draw UI
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Lobby - Press SPACE near machines to interact", 20, 30);
        g.drawString("Item: " + getItemName(player.getItemState()), 20, 50);
    }

    private String getItemName(int itemState) {
        switch (itemState) {
            case Constants.ITEM_NONE: return "None";
            case Constants.ITEM_POPCORN: return "Popcorn";
            case Constants.ITEM_DRINK: return "Drink";
            case Constants.ITEM_BOTH: return "Popcorn + Drink";
            case Constants.ITEM_TICKET: return "Ticket";
            default: return "Unknown";
        }
    }
}