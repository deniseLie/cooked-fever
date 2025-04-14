package com.example.cooked_fever.customers;

import android.graphics.*;
import android.view.*;
import android.util.Log;

import java.util.*;

import com.example.cooked_fever.appliances.*;
import com.example.cooked_fever.food.*;


public class CustomerManager {

    // Customer Static Variables
    private final int MAX_CUSTOMER = 5;
    private final int CUSTOMER_SPACING = 220;
    private final int START_X = 200;
    private final int CUSTOMER_Y = 200;
    private final String[] availableMenu = {"Cola", "Burger", "Hotdog"};

    private final List<Customer> customers = new ArrayList<>();
    private boolean[] customerSlots = new boolean[MAX_CUSTOMER];
    private long lastCustomerSpawn = System.currentTimeMillis();

    private final Random random = new Random();
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Constructor
    public CustomerManager() {
    }

    // GET
    public List<Customer> getCustomerList() {
        return customers;
    }

    // METHOD
    public void addCustomer(int x, int y, List<String> orders) {
        customers.add(new Customer(x, y, orders));
    }
    public Boolean receiveItem (Customer customer, FoodItem foodItem) {
        return customer.serveItem(foodItem.getFoodItemName());
    }

    // Update function every delta time
    public void update(long now) {
        Iterator<Customer> iter = customers.iterator();
        while (iter.hasNext()) {
            Customer c = iter.next();
            c.update();

            // If Customer need to leave
            if (c.shouldLeave()) {
                customerSlots[c.getSlotIndex()] = false;
                iter.remove();
            }
        }

        // Spawn new customer every 5 seconds and have enough space
        if (now - lastCustomerSpawn >= 5000 && customers.size() < MAX_CUSTOMER) {
            spawnCustomer();
            lastCustomerSpawn = now;
        }
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

    // Handle Touch
    public Customer handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (Customer customer : customers) {
            if (customer.isCustomerHitbox((int)x, (int)y)) {  // Check if the click is within the bounds of the food item
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
        customers.clear();
        this.lastCustomerSpawn = System.currentTimeMillis();
        for (int i = 0; i < customerSlots.length; i++) {
            customerSlots[i] = false;
        }
    }

    // Draw
    public void draw(Canvas canvas) {
        for (Customer customer : customers) {
            customer.draw(canvas);
        }
    }
}
