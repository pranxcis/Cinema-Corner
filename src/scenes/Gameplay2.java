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

public class Gameplay2 extends Scene {

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

    public Gameplay2(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void init() {
        backgroundImage = AssetLoader.loadImage("images/Gameplay2.png");

        // Day config for Day 2
        dayConfig = new DayConfig(2);

        // Player start
        player = new Player(40, 470);

        // Initialize machines at exact positions
        popcornMachine = new PopcornMachine(580, 220);
        drinkMachine = new DrinkMachine(760, 200);
        ticketMachine = new TicketMachine(930, 220);
        trash = new Trash(1130, 320);

        // Machines start FULL
        popcornMachine.setState(Constants.MACHINE_FULL);
        drinkMachine.setState(Constants.MACHINE_FULL);
        ticketMachine.setState(Constants.MACHINE_FULL);

        // Systems
        customerSystem = new CustomerSystem(dayConfig);
        scoringSystem = new ScoringSystem();

        // HUD
        hud = new HUD();
        hud.setDay(2);

        // Audio
        AudioSystem.getInstance().playDayStart();
        AudioSystem.getInstance().playGameplay2Music();

        System.out.println("Gameplay2 initialized for Day 2");
    }

    @Override
    public void update(long deltaTime, InputHandler input) {

        // Pause toggle
        if (input.isEscJustPressed()) {
            isPaused = !isPaused;
            hud.setPaused(isPaused);
        }

        if (isPaused) return;

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

        // Update player (Option A: NO machine collisions)
        int oldX = player.getX();
        int oldY = player.getY();

        player.update(deltaTime, input);

        // Only block walls + boundaries
        if (CollisionUtil.checkWallCollision(player) ||
                !CollisionUtil.isWithinBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)) {

            player.setX(oldX);
            player.setY(oldY);
        }

        // Update machines and customers
        customerSystem.update(deltaTime);
        popcornMachine.update(deltaTime);
        drinkMachine.update(deltaTime);
        ticketMachine.update(deltaTime);

        // HUD updates
        hud.setHeldItem(player.getItemState());
        hud.setMoney(scoringSystem.getTotalMoney());

        checkInteractables();

        // Machine / Customer interaction
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
                player.getX() - 20, player.getY() - 20,
                Constants.PLAYER_WIDTH + 40,
                Constants.PLAYER_HEIGHT + 40
        );

        boolean near = false;

        if (area.intersects(popcornMachine.getBounds())) {
            hud.setInteractMessage("Press E for Popcorn Machine");
            near = true;

        } else if (area.intersects(drinkMachine.getBounds())) {
            hud.setInteractMessage("Press E for Drink Machine");
            near = true;

        } else if (area.intersects(ticketMachine.getBounds())) {
            hud.setInteractMessage("Press E for Ticket Machine");
            near = true;

        } else {
            Customer c = customerSystem.getFirstWaitingCustomer();
            if (c != null && area.intersects(c.getBounds())) {
                hud.setInteractMessage("Press E to serve customer");
                near = true;
            }
        }

        hud.setNearInteractable(near);
        if (!near) hud.setInteractMessage("");
    }

    private void handleInteractions() {

        Rectangle area = new Rectangle(
                player.getX() - 20, player.getY() - 20,
                Constants.PLAYER_WIDTH + 40,
                Constants.PLAYER_HEIGHT + 40
        );

        // Machines first
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

        // Serve customer
        Customer customer = customerSystem.getFirstWaitingCustomer();
        if (customer != null && area.intersects(customer.getBounds())) {
            handleCustomerServe(customer);
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

        } else if (popcornMachine.getState() == Constants.MACHINE_EMPTY) {
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

        } else if (drinkMachine.getState() == Constants.MACHINE_EMPTY) {
            drinkMachine.startRefill();
        }
    }

    private void handleTicketMachine() {
        if (ticketMachine.canDispense() && player.getItemState() == Constants.ITEM_NONE) {
            player.setItemState(Constants.ITEM_TICKET);
            ticketMachine.dispense();
        }
        else if (ticketMachine.getState() == Constants.MACHINE_EMPTY) {
            ticketMachine.startPrinting();
        }
    }

    private void handleCustomerServe(Customer customer) {

        int item = player.getItemState();
        if (item == Constants.ITEM_NONE) return;

        // BOTH combo
        if (item == Constants.ITEM_BOTH) {
            customer.giveItem(Constants.ITEM_POPCORN);
            customer.giveItem(Constants.ITEM_DRINK);

        } else {
            boolean ok = customer.giveItem(item);
            if (!ok) {
                scoringSystem.addWrongServe();
                return;
            }
        }

        player.setItemState(Constants.ITEM_NONE);

        // Complete order
        if (customer.isOrderComplete()) {
            customerSystem.serveCustomer(customer, true);
            scoringSystem.addCorrectServe();
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
        for (Customer c : customerSystem.getCustomers()) {
            c.render(g);
        }

        // Player
        player.render(g);

        // HUD
        hud.render(g);

        // Day progress text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(
                "Served: " + customerSystem.getCustomersServed() + "/" + dayConfig.getGoalCustomers(),
                Constants.WINDOW_WIDTH - 160,
                50
        );
    }
}
