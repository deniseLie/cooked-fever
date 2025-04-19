package com.example.cooked_fever.customers;

import android.content.Context;
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
    private Context context;

    private int screenWidth = 1080;
    // Constructor
    private int customersFulfilled = 0;
    private int customersMissed = 0;
    public CustomerManager(Context context, int screenWidth) {
        this.context = context;
        this.screenWidth = screenWidth;
    }

    public int getCustomersFulfilled() {
        return customersFulfilled;
    }

    public int getCustomersMissed() {
        return customersMissed;
    }

    public void resetStats() {
        customersFulfilled = 0;
        customersMissed = 0;
    }
    public void setScreenWidth(int width) {
        this.screenWidth = width;
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

        // Collect customers to remove safely
        List<Customer> toRemove = new ArrayList<>();

        for (Customer c : customers) {
            if (c.shouldLeave()) {
                if (c.isServed()) {
                    customersFulfilled++;
                } else {
                    customersMissed++;
                }
                customerSlots[c.getSlotIndex()] = false;
                toRemove.add(c);
            }
        }

        customers.removeAll(toRemove);

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
        executor.shutdownNow();
        executor = Executors.newFixedThreadPool(2);

        customers.clear();
        Arrays.fill(customerSlots, false);
        lastCustomerSpawn = System.currentTimeMillis();

        resetStats();
    }

    // Spawn customer
    private void spawnCustomer() {
        int slotIndex = getNextAvailableSlot();
        if (slotIndex == -1) return;

        int totalSlots = customerSlots.length;

        int leftPadding = 400;
        int rightPadding = 550; // More space on the right to avoid the timer

        float usableWidth = screenWidth - leftPadding - rightPadding;
        float spacing = usableWidth / (totalSlots - 1);  // e.g. 4 gaps for 5 customers
        int x = (int)(leftPadding + slotIndex * spacing);
        int y = CUSTOMER_Y;

        int orderCount = 1 + random.nextInt(3);
        List<String> order = new ArrayList<>();
        for (int i = 0; i < orderCount; i++) {
            order.add(availableMenu[random.nextInt(availableMenu.length)]);
        }

        Customer customer = new Customer(context, x, y, order);
        customer.setSlotIndex(slotIndex);
        customerSlots[slotIndex] = true;
        customers.add(customer);
    }

    // Draw
    public void draw(Canvas canvas) {
        for (Customer customer : customers) {
            customer.draw(canvas);
        }
    }
}
