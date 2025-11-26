package utils;

import entities.*;
import java.awt.*;

public class CollisionUtil {

    // Wall boundaries
    private static final Rectangle WALL_A = new Rectangle(0, 750, 700, 10); // (20,645) to (670,645)
    private static final Rectangle WALL_B = new Rectangle(20, 325, 1280, 10); // (20,325) to (1170,325)
    private static final Rectangle WALL_C = new Rectangle(880, 575, 400, 5); // (835,575) to (1170,575)

    public static boolean checkMachineCollision(Player player, PopcornMachine machine) {
        return player.getBounds().intersects(machine.getBounds());
    }

    public static boolean checkMachineCollision(Player player, DrinkMachine machine) {
        return player.getBounds().intersects(machine.getBounds());
    }

    public static boolean checkMachineCollision(Player player, TicketMachine machine) {
        return player.getBounds().intersects(machine.getBounds());
    }

    public static boolean checkCustomerCollision(Player player, Customer customer) {
        return player.getBounds().intersects(customer.getBounds());
    }

    public static boolean checkWallCollision(Player player) {
        Rectangle playerBounds = player.getBounds();
        return playerBounds.intersects(WALL_A) ||
                playerBounds.intersects(WALL_B) ||
                playerBounds.intersects(WALL_C);
    }

    public static boolean isWithinBounds(Player player, int maxWidth, int maxHeight) {
        Rectangle bounds = player.getBounds();
        return bounds.x >= 0 &&
                bounds.y >= 0 &&
                bounds.x + bounds.width <= maxWidth &&
                bounds.y + bounds.height <= maxHeight;
    }

    public static boolean isNearby(Rectangle rect1, Rectangle rect2, int distance) {
        int dx = Math.abs((rect1.x + rect1.width / 2) - (rect2.x + rect2.width / 2));
        int dy = Math.abs((rect1.y + rect1.height / 2) - (rect2.y + rect2.height / 2));
        return dx <= distance && dy <= distance;
    }

    // Render walls for debugging
    public static void renderWalls(Graphics2D g) {
        g.setColor(new Color(255, 0, 0, 100));
        g.fillRect(WALL_A.x, WALL_A.y, WALL_A.width, WALL_A.height);
        g.fillRect(WALL_B.x, WALL_B.y, WALL_B.width, WALL_B.height);
        g.fillRect(WALL_C.x, WALL_C.y, WALL_C.width, WALL_C.height);
    }
}