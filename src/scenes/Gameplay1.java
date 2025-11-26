package scenes;

import core.*;
import entities.*;
import systems.AudioSystem;
import systems.CustomerSystem;
import systems.ScoringSystem;
import ui.HUD;
import utils.Constants;
import utils.CollisionUtil;
import config.DayConfig;

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
        backgroundImage = AssetLoader.loadImage("images/Gameplay1.png");

        // Initialize day config
        dayConfig = new DayConfig(1);

        // Initialize player
        player = new Player(40, 470);

        // Initialize machines at exact positions
        popcornMachine = new PopcornMachine(580, 220);
        drinkMachine = new DrinkMachine(760, 200);
        ticketMachine = new TicketMachine(930, 220);
        trash = new Trash(1130, 320);

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
        hud.setMoney(scoringSystem.getTotalMoney());

        // Play day start sound and music
        AudioSystem.getInstance().playDayStart();
        AudioSystem.getInstance().playGameplay1Music();

        System.out.println("Gameplay1 initialized for Day " + dayConfig.getDayNumber());
    }

    @Override
    public void update(long deltaTime, InputHandler input) {
        // Pause toggle
        if (input.isEscJustPressed()) {
            isPaused = !isPaused;
            hud.setPaused(isPaused);
        }

        if (isPaused) return;

        // Day fail check
        if (customerSystem.isDayFailed()) {
            AudioSystem.getInstance().playGameIncomplete();
            // return to buffer or menu - follow your scene flow
            sceneManager.switchScene(Constants.SCENE_GAMEPLAY); // keep same scene (or change as needed)
            return;
        }

        // Day complete check
        if (customerSystem.isDayComplete()) {
            AudioSystem.getInstance().playDayClear();
            sceneManager.switchScene(Constants.SCENE_BUFFER);
            return;
        }

        // Update player with old position backup (machines DO NOT block movement in Option A)
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(deltaTime, input);

        // Only block movement on walls and out-of-bounds (same as LobbyScene)
        if (CollisionUtil.checkWallCollision(player) ||
                !CollisionUtil.isWithinBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)) {
            player.setX(oldX);
            player.setY(oldY);
        }

        // Update systems & machines
        customerSystem.update(deltaTime);
        popcornMachine.update(deltaTime);
        drinkMachine.update(deltaTime);
        ticketMachine.update(deltaTime);

        // Update HUD values
        hud.setHeldItem(player.getItemState());
        hud.setMoney(scoringSystem.getTotalMoney());
        hud.setDay(dayConfig.getDayNumber());

        // Interactable hints
        checkInteractables();

        // Interactions (match LobbyScene behaviour: E pressed)
        if (input.isEPressed()) {
            AudioSystem.getInstance().playInteract();
            handleInteractions();
        }

        // Trash (T just pressed)
        if (input.isTJustPressed() && player.getItemState() != Constants.ITEM_NONE) {
            AudioSystem.getInstance().playInteract();
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
        } else if (interactionArea.intersects(trash.getBounds())) {
            hud.setInteractMessage("Press T to trash item");
            nearSomething = true;
        } else {
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

        // Machines first (same logic as LobbyScene)
        if (interactionArea.intersects(popcornMachine.getBounds())) {
            handlePopcornMachine();
            return;
        }

        if (interactionArea.intersects(drinkMachine.getBounds())) {
            handleDrinkMachine();
            return;
        }

        if (interactionArea.intersects(ticketMachine.getBounds())) {
            handleTicketMachine();
            return;
        }

        // Serve customer if nearby
        Customer customer = customerSystem.getFirstWaitingCustomer();
        if (customer != null && interactionArea.intersects(customer.getBounds())) {
            handleCustomerServe(customer);
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

        // BOTH state gives both items
        if (playerItem == Constants.ITEM_BOTH) {
            customer.giveItem(Constants.ITEM_POPCORN);
            customer.giveItem(Constants.ITEM_DRINK);
        } else {
            boolean accepted = customer.giveItem(playerItem);
            if (!accepted) {
                scoringSystem.addWrongServe();
                return;
            }
        }

        // Clear player's item
        player.setItemState(Constants.ITEM_NONE);

        // If order complete, serve customer and score
        if (customer.isOrderComplete()) {
            customerSystem.serveCustomer(customer, true);
            scoringSystem.addCorrectServe();
            // Optionally increment money here based on order — kept in ScoringSystem
        }
        // If not complete, customer waits for remaining items
    }

    @Override
    public void render(Graphics2D g) {
        // Background base color
        g.setColor(new Color(180, 180, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Background image if any
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Machines & trash
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);
        trash.render(g);

        // Customers
        for (Customer c : customerSystem.getCustomers()) {
            c.render(g);
        }

        // Player
        player.render(g);

        // HUD
        hud.render(g);

        // Day progress
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String progress = "Served: " + customerSystem.getCustomersServed() + "/" + dayConfig.getGoalCustomers();
        g.drawString(progress, Constants.WINDOW_WIDTH - 180, 50);
    }
}
