package systems;

import entities.Customer;
import config.*;
import utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;

public class CustomerSystem {
    private List<Customer> customers;
    private long spawnTimer;
    private long spawnInterval;
    private int customersServed;
    private int angryCustomers;
    private int wrongServes;
    private int goalCustomers;
    private Random random;

    public CustomerSystem(DayConfig dayConfig) {
        customers = new ArrayList<>();
        spawnTimer = 0;
        spawnInterval = dayConfig.getSpawnInterval();
        customersServed = 0;
        angryCustomers = 0;
        wrongServes = 0;
        goalCustomers = dayConfig.getGoalCustomers();
        random = new Random();
    }

    public void update(long deltaTime) {
        // Update spawn timer
        spawnTimer += deltaTime;

        // Spawn new customer if ready and not at capacity
        if (spawnTimer >= spawnInterval && customers.size() < 3) {
            spawnCustomer();
            spawnTimer = 0;
            System.out.println("Customer spawned! Total customers: " + customers.size());
        }

        // Update all customers
        for (int i = customers.size() - 1; i >= 0; i--) {
            Customer customer = customers.get(i);
            customer.update(deltaTime);

            // Remove if angry or leaving
            if (customer.getState() == Constants.CUSTOMER_ANGRY) {
                angryCustomers++;
                customers.remove(i);
                System.out.println("Customer left angry! Angry count: " + angryCustomers);
                // Move queue up
                updateQueuePositions();
            } else if (customer.getState() == Constants.CUSTOMER_LEAVING &&
                    customer.hasReachedTarget()) {
                customers.remove(i);
                System.out.println("Customer left satisfied! Served: " + customersServed);
                // Move queue up
                updateQueuePositions();
            }
        }
    }

    private void updateQueuePositions() {
        // Reassign counter positions to remaining customers
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            if (customer.getState() == Constants.CUSTOMER_WAITING) {
                Point counterPos = SpawnConfig.getCounterPosition(i);
                customer.setTarget(counterPos.x, counterPos.y);
            }
        }
    }

    private void spawnCustomer() {
        int customerType = random.nextInt(3) + 1; // 1, 2, or 3
        Point spawn = SpawnConfig.SPAWN_POINT;

        Customer customer = new Customer(spawn.x, spawn.y, customerType);

        // Assign counter position based on current queue size
        Point counterPos = SpawnConfig.getCounterPosition(customers.size());
        customer.setTarget(counterPos.x, counterPos.y);
        customer.setState(Constants.CUSTOMER_WAITING);

        customers.add(customer);

        System.out.println("Spawned customer type " + customerType + " at (" + spawn.x + ", " + spawn.y + ") heading to (" + counterPos.x + ", " + counterPos.y + ")");
    }

    public Customer getFirstWaitingCustomer() {
        // Return customer at Counter 1 only
        for (Customer customer : customers) {
            if (customer.getState() == Constants.CUSTOMER_WAITING &&
                    customer.hasReachedTarget() &&
                    customer.isAtCounter1()) {
                return customer;
            }
        }
        return null;
    }

    public void serveCustomer(Customer customer, boolean correct) {
        if (correct) {
            customer.setState(Constants.CUSTOMER_SATISFIED);
            customer.setTarget(SpawnConfig.EXIT_POINT.x, SpawnConfig.EXIT_POINT.y);
            customer.setState(Constants.CUSTOMER_LEAVING);
            customersServed++;
        } else {
            wrongServes++;
            // Customer stays and becomes more impatient
        }
    }

    public boolean isDayComplete() {
        return customersServed >= goalCustomers;
    }

    public boolean isDayFailed() {
        return angryCustomers >= GameConfig.MAX_ANGRY_CUSTOMERS ||
                wrongServes >= GameConfig.MAX_WRONG_SERVES;
    }

    // Getters
    public List<Customer> getCustomers() { return customers; }
    public int getCustomersServed() { return customersServed; }
    public int getAngryCustomers() { return angryCustomers; }
    public int getWrongServes() { return wrongServes; }
    public int getGoalCustomers() { return goalCustomers; }
}