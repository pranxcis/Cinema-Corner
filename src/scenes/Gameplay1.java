package scenes;

import core.*;
import entities.*;
import systems.*;
import ui.HUD;
import utils.Constants;
import utils.CollisionUtil;
import config.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Gameplay1 extends Scene {
    private BufferedImage backgroundImage;
    private Player player;
    private PopcornMachine popcornMachine;
    private DrinkMachine drinkMachine;
    private TicketMachine ticketMachine;
    private Trash trash;

    private CustomerSystem customerSystem;
    private ScoringSystem scoringSystem;
    private HUD hud;

    private boolean isPaused = false;
    private DayConfig dayConfig;

    public Gameplay1(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/GameplayScene.png");

        // Initialize day config
        dayConfig = new DayConfig(1);

        // Initialize player
        player = new Player(100, 400);

        // Initialize machines
        popcornMachine = new PopcornMachine(200, 200);
        drinkMachine = new DrinkMachine(400, 200);
        ticketMachine = new TicketMachine(600, 200);
        trash = new Trash(50, 300);

        // Set machines to ready
        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);

        // Initialize systems
        customerSystem = new CustomerSystem(dayConfig);
        scoringSystem = new ScoringSystem();

        // Initialize HUD
        hud = new HUD();
        hud.setDay(1);
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Check for pause
        if (input.isEscJustPressed()) {
            isPaused = !isPaused;
            hud.setPaused(isPaused);
        }

        if (isPaused) {
            return; // Don't update game when paused
        }

        // Check if day failed
        if (customerSystem.isDayFailed()) {
            // Reset to Day 1
            sceneManager.switchScene(Constants.SCENE_GAMEPLAY);
            return;
        }

        // Check if day complete
        if (customerSystem.isDayComplete()) {
            sceneManager.switchScene(Constants.SCENE_BUFFER);
            return;
        }

        // Update player
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(deltaTime, input);

        // Check collisions
        if (CollisionUtil.checkMachineCollision(player, popcornMachine) ||
                CollisionUtil.checkMachineCollision(player, drinkMachine) ||
                CollisionUtil.checkMachineCollision(player, ticketMachine) ||
                !CollisionUtil.isWithinBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)) {
            player.setX(oldX);
            player.setY(oldY);
        }

        // Update systems
        customerSystem.update(deltaTime);
        popcornMachine.update(deltaTime);
        drinkMachine.update(deltaTime);
        ticketMachine.update(deltaTime);

        // Update HUD
        hud.setHeldItem(player.getItemState());
        hud.setMoney(scoringSystem.getTotalMoney());
        checkInteractables();

        // Handle interactions
        if (input.isEJustPressed()) {
            handleInteractions();
        }

        // Handle trash
        if (input.isTJustPressed() && player.getItemState() != Constants.ITEM_NONE) {
            player.setItemState(Constants.ITEM_NONE);
            scoringSystem.addTrashedItem();
        }
    }

    private void checkInteractables() {
        Rectangle interactionArea = new Rectangle(
                player.getX() - 20, player.getY() - 20,
                Constants.PLAYER_WIDTH + 40, Constants.PLAYER_HEIGHT + 40
        );

        boolean nearSomething = false;

        if (interactionArea.intersects(popcornMachine.getBounds())) {
            hud.setInteractMessage("Press E for Popcorn Machine");
            nearSomething = true;
        } else if (interactionArea.intersects(drinkMachine.getBounds())) {
            hud.setInteractMessage("Press E for Drink Machine");
            nearSomething = true;
        } else if (interactionArea.intersects(ticketMachine.getBounds())) {
            hud.setInteractMessage("Press E for Ticket Machine");
            nearSomething = true;
        } else {
            // Check customers
            Customer customer = customerSystem.getFirstWaitingCustomer();
            if (customer != null && interactionArea.intersects(customer.getBounds())) {
                hud.setInteractMessage("Press E to serve customer");
                nearSomething = true;
            }
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

        // Check machines first
        if (interactionArea.intersects(popcornMachine.getBounds())) {
            handlePopcornMachine();
        } else if (interactionArea.intersects(drinkMachine.getBounds())) {
            handleDrinkMachine();
        } else if (interactionArea.intersects(ticketMachine.getBounds())) {
            handleTicketMachine();
        } else {
            // Check customer serving
            Customer customer = customerSystem.getFirstWaitingCustomer();
            if (customer != null && interactionArea.intersects(customer.getBounds())) {
                handleCustomerServe(customer);
            }
        }
    }

    private void handlePopcornMachine() {
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

    private void handleDrinkMachine() {
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

    private void handleTicketMachine() {
        if (ticketMachine.canDispense() && player.getItemState() == Constants.ITEM_NONE) {
            player.setItemState(Constants.ITEM_TICKET);
            ticketMachine.dispense();
        } else if (ticketMachine.getState() == Constants.MACHINE_EMPTY) {
            ticketMachine.startPrinting();
        }
    }

    private void handleCustomerServe(Customer customer) {
        int playerItem = player.getItemState();

        if (playerItem == Constants.ITEM_NONE) {
            return; // Can't serve with nothing
        }

        boolean correct = customer.checkOrder(playerItem);

        if (correct) {
            customerSystem.serveCustomer(customer, true);
            scoringSystem.addCorrectServe();
            player.setItemState(Constants.ITEM_NONE);
        } else {
            scoringSystem.addWrongServe();
            // Customer stays, player keeps item
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Background
        g.setColor(new Color(180, 180, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Machines
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);
        trash.render(g);

        // Customers
        for (Customer customer : customerSystem.getCustomers()) {
            customer.render(g);
        }

        // Player
        player.render(g);

        // HUD
        hud.render(g);

        // Day progress
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String progress = "Served: " + customerSystem.getCustomersServed() + "/" + dayConfig.getGoalCustomers();
        g.drawString(progress, Constants.WINDOW_WIDTH - 150, 80);
    }
}