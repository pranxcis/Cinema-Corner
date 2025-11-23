package utils;

import entities.*;
import java.awt.*;

public class CollisionUtil {

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
}