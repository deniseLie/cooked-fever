package com.example.cooked_fever.customers;

import android.graphics.*;
import android.view.*;
import android.util.Log;

import java.util.*;
import java.util.concurrent.*;

import com.example.cooked_fever.appliances.*;
import com.example.cooked_fever.food.*;


public class CustomerManager {

    // Customer Static Variables
    private final int MAX_CUSTOMER = 5;
    private final int CUSTOMER_SPACING = 220;
    private final int START_X = 200;
    private final int CUSTOMER_Y = 200;
    private final String[] availableMenu = {"Cola", "Burger", "Hotdog", "Fries"};

    private final List<Customer> customers = new CopyOnWriteArrayList<>();
    private boolean[] customerSlots = new boolean[MAX_CUSTOMER];
    private long lastCustomerSpawn = System.currentTimeMillis();

    private final Random random = new Random();
    private final String LOG_TAG = this.getClass().getSimpleName();
    private ExecutorService executor = Executors.newFixedThreadPool(4); // For background updates

    // Constructor
    public CustomerManager() {
    }

    // GET
    public List<Customer> getCustomerList() {
        return customers;
    }

    // METHOD
    public Boolean receiveItem (Customer customer, FoodItem foodItem) {
        return customer.serveItem(foodItem.getFoodItemName());
    }

    // Update function every delta time
    public void update(long now) {
        if (executor.isShutdown()) return;

        // Background update each customer
        for (Customer customer : customers) {
            executor.execute(customer::update);
        }

        // Remove customers who have left and update slot info
        for (Customer c : customers) {
            if (c.shouldLeave()) {
                customerSlots[c.getSlotIndex()] = false;
            }
        }

        // Actually remove them (safe with CopyOnWriteArrayList)
        customers.removeIf(Customer::shouldLeave);

        // Spawn new customer every 5 seconds and have enough space
        if (now - lastCustomerSpawn >= 5000 && customers.size() < MAX_CUSTOMER) {
            spawnCustomer();
            lastCustomerSpawn = now;
        }
    }

    // Handle Touch
    public Customer handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (Customer customer : customers) {

            // Check if the click is within the bounds of the food item
            if (customer.isCustomerHitbox((int)x, (int)y)) {
                return customer;
            }
        }
        return null;
    }

    // Helper function to find empty slot index
    private int getNextAvailableSlot() {
        for (int i = 0; i < MAX_CUSTOMER; i++) {
            if (!customerSlots[i]) return i;
        }
        return -1;
    }

    // Reset customer
    public void reset() {
        executor.shutdownNow();  // cleanup old one
        executor = Executors.newFixedThreadPool(2); // create a new one

        customers.clear();
        for (int i = 0; i < customerSlots.length; i++) {
            customerSlots[i] = false;
        }
        this.lastCustomerSpawn = System.currentTimeMillis();
    }

    // Spawn customer
    private void spawnCustomer() {

        // Get slot index
        int slotIndex = getNextAvailableSlot();
        if (slotIndex == -1) return;    // No available slots

        // Customer spawn
        int x = START_X + slotIndex * CUSTOMER_SPACING;
        int y = CUSTOMER_Y;

        // Random number of items between 1–3
        int orderCount = 1 + random.nextInt(3);
        List<String> order = new ArrayList<>();

        for (int i = 0; i < orderCount; i++) {
            order.add(availableMenu[random.nextInt(availableMenu.length)]);
        }

        Customer customer = new Customer(x, y, order);
        customer.setSlotIndex(slotIndex);   // save slot index
        customerSlots[slotIndex] = true;
        customers.add(customer);    // add customer
    }

    // Draw
    public void draw(Canvas canvas) {
        for (Customer customer : customers) {
            customer.draw(canvas);
        }
    }
}
