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

public class Gameplay3 extends Scene {

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

    public Gameplay3(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {

        // Background
        backgroundImage = AssetLoader.loadImage("images/Gameplay3.png");

        // Day 3 config
        dayConfig = new DayConfig(3);

        // Player
        player = new Player(40, 470);

        // Initialize machines at exact positions
        popcornMachine = new PopcornMachine(580, 220);
        drinkMachine = new DrinkMachine(760, 200);
        ticketMachine = new TicketMachine(930, 220);
        trash = new Trash(1130, 320);

        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);

        // Systems
        customerSystem = new CustomerSystem(dayConfig);
        scoringSystem = new ScoringSystem();

        // HUD
        hud = new HUD();
        hud.setDay(3);

        // Audio
        AudioSystem.getInstance().playDayStart();
        AudioSystem.getInstance().playGameplay3Music();
    }

    @Override
    public void update(long deltaTime, InputHandler input) {

        // Pause toggle
        if (input.isEscJustPressed()) {
            isPaused = !isPaused;
            hud.setPaused(isPaused);
        }

        if (isPaused) {
            return;
        }

        // Day fail
        if (customerSystem.isDayFailed()) {
            AudioSystem.getInstance().playGameIncomplete();
            sceneManager.switchScene(Constants.SCENE_GAMEPLAY);
            return;
        }

        // Day complete
        if (customerSystem.isDayComplete()) {
            AudioSystem.getInstance().playDayClear();
            sceneManager.switchScene(Constants.SCENE_BUFFER);
            return;
        }

        // Update player movement
        int oldX = player.getX();
        int oldY = player.getY();
        player.update(deltaTime, input);

        // Only wall + bounds collision (OPTION A)
        if (CollisionUtil.checkWallCollision(player) ||
                !CollisionUtil.isWithinBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)) {
            player.setX(oldX);
            player.setY(oldY);
        }

        // Update systems
        customerSystem.update(deltaTime);
        popcornMachine.update(deltaTime);
        drinkMachine.update(deltaTime);
        ticketMachine.update(deltaTime);

        // HUD info
        hud.setHeldItem(player.getItemState());
        hud.setMoney(scoringSystem.getTotalMoney());

        checkInteractables();

        // Interaction
        if (input.isEJustPressed()) {
            AudioSystem.getInstance().playInteract();
            handleInteractions();
        }

        // Trash item
        if (input.isTJustPressed() && player.getItemState() != Constants.ITEM_NONE) {
            AudioSystem.getInstance().playInteract();
            player.setItemState(Constants.ITEM_NONE);
            scoringSystem.addTrashedItem();
        }
    }

    private void checkInteractables() {

        Rectangle area = new Rectangle(
                player.getX() - 20,
                player.getY() - 20,
                Constants.PLAYER_WIDTH + 40,
                Constants.PLAYER_HEIGHT + 40
        );

        boolean near = false;

        if (area.intersects(popcornMachine.getBounds())) {
            hud.setInteractMessage("Press E for Popcorn Machine");
            near = true;
        }
        else if (area.intersects(drinkMachine.getBounds())) {
            hud.setInteractMessage("Press E for Drink Machine");
            near = true;
        }
        else if (area.intersects(ticketMachine.getBounds())) {
            hud.setInteractMessage("Press E for Ticket Machine");
            near = true;
        }
        else {
            Customer c = customerSystem.getFirstWaitingCustomer();
            if (c != null && area.intersects(c.getBounds())) {
                hud.setInteractMessage("Press E to serve customer");
                near = true;
            }
        }

        hud.setNearInteractable(near);

        if (!near) {
            hud.setInteractMessage("");
        }
    }

    private void handleInteractions() {

        Rectangle area = new Rectangle(
                player.getX() - 20,
                player.getY() - 20,
                Constants.PLAYER_WIDTH + 40,
                Constants.PLAYER_HEIGHT + 40
        );

        if (area.intersects(popcornMachine.getBounds())) {
            handlePopcornMachine();
            return;
        }

        if (area.intersects(drinkMachine.getBounds())) {
            handleDrinkMachine();
            return;
        }

        if (area.intersects(ticketMachine.getBounds())) {
            handleTicketMachine();
            return;
        }

        Customer c = customerSystem.getFirstWaitingCustomer();
        if (c != null && area.intersects(c.getBounds())) {
            handleCustomerServe(c);
        }
    }

    private void handlePopcornMachine() {
        if (popcornMachine.canDispense()) {

            if (player.getItemState() == Constants.ITEM_NONE) {
                player.setItemState(Constants.ITEM_POPCORN);
                popcornMachine.dispense();
            }
            else if (player.getItemState() == Constants.ITEM_DRINK) {
                player.setItemState(Constants.ITEM_BOTH);
                popcornMachine.dispense();
            }
        }
        else if (popcornMachine.getState() == Constants.MACHINE_EMPTY) {
            popcornMachine.startRefill();
        }
    }

    private void handleDrinkMachine() {
        if (drinkMachine.canDispense()) {

            if (player.getItemState() == Constants.ITEM_NONE) {
                player.setItemState(Constants.ITEM_DRINK);
                drinkMachine.dispense();
            }
            else if (player.getItemState() == Constants.ITEM_POPCORN) {
                player.setItemState(Constants.ITEM_BOTH);
                drinkMachine.dispense();
            }
        }
        else if (drinkMachine.getState() == Constants.MACHINE_EMPTY) {
            drinkMachine.startRefill();
        }
    }

    private void handleTicketMachine() {
        if (ticketMachine.canDispense() &&
                player.getItemState() == Constants.ITEM_NONE) {

            player.setItemState(Constants.ITEM_TICKET);
            ticketMachine.dispense();
        }
        else if (ticketMachine.getState() == Constants.MACHINE_EMPTY) {
            ticketMachine.startPrinting();
        }
    }

    private void handleCustomerServe(Customer customer) {

        int item = player.getItemState();

        if (item == Constants.ITEM_NONE) {
            return;
        }

        // Combo items
        if (item == Constants.ITEM_BOTH) {
            customer.giveItem(Constants.ITEM_POPCORN);
            customer.giveItem(Constants.ITEM_DRINK);
        }
        else {
            boolean accepted = customer.giveItem(item);

            if (!accepted) {
                scoringSystem.addWrongServe();
                return;
            }
        }

        player.setItemState(Constants.ITEM_NONE);

        if (customer.isOrderComplete()) {
            customerSystem.serveCustomer(customer, true);
            scoringSystem.addCorrectServe();
        }
    }

    @Override
    public void render(Graphics2D g) {

        g.setColor(new Color(180, 180, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0,
                    Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, null);
        }

        // Machines
        popcornMachine.render(g);
        drinkMachine.render(g);
        ticketMachine.render(g);
        trash.render(g);

        // Customers
        for (Customer c : customerSystem.getCustomers()) {
            c.render(g);
        }

        // Player + HUD
        player.render(g);
        hud.render(g);

        // Progress text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String text = "Served: " +
                customerSystem.getCustomersServed() +
                "/" + dayConfig.getGoalCustomers();

        g.drawString(text, Constants.WINDOW_WIDTH - 150, 50);
    }
}
