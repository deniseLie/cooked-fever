package com.example.cooked_fever.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.example.cooked_fever.appliances.*;
import com.example.cooked_fever.customers.*;
import com.example.cooked_fever.food.*;

/**
 * A class representing the main logic of this demo
 */

public class Game {

    private final Runnable sendNotification;
    private final Consumer<Consumer<Canvas>> canvasUser;

    private final Paint customerPaint = new Paint();
    private final Paint appliancePaint = new Paint();
    private final Paint textPaint = new Paint();

    private long lastUpdateTime = System.currentTimeMillis();
    private long lastCustomerSpawn = System.currentTimeMillis();

    private int screenWidth = 1080;
    private int screenHeight = 1920;

    private final Random random = new Random();
    private final String LOG_TAG = this.getClass().getSimpleName();

    private int coins = 0;
    private final long GAME_DURATION_MS = 20000; //60 seconds
    private long gameStartTime = System.currentTimeMillis();
    private boolean isGameOver = false;

    // Var game Logic Menu
    private final String[] availableMenu = {"Cola"};

    // Customer Static Variables
    private final int MAX_CUSTOMER = 5;
    private final int CUSTOMER_SPACING = 220;
    private final int START_X = 200;
    private final int CUSTOMER_Y = 200;

    // List
    private final List<Customer> customers = new ArrayList<>();
    private boolean[] customerSlots = new boolean[MAX_CUSTOMER];

    // Managers
    private final ApplianceManager applianceManager = new ApplianceManager(screenWidth, screenHeight);
    private final FoodSourceManager foodSourceManager = new FoodSourceManager(screenWidth, screenHeight);
    private final FoodItemManager foodItemManager = new FoodItemManager();

    // User Interaction
    private FoodItem draggedFoodItem = null;  // Track which food item is being dragged
    private float offsetX, offsetY;  // Track where the user clicked on the food item to ensure smooth dragging



    public Game(Runnable sendNotification, Consumer<Consumer<Canvas>> canvasUser) {
        this.sendNotification = sendNotification;
        this.canvasUser = canvasUser;

        // Pain sprites
        customerPaint.setColor(Color.MAGENTA);
        appliancePaint.setColor(Color.BLUE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setAntiAlias(true);
    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // Resize manager
        applianceManager.resize(width, height);
    }

    public void update() {
        long now = System.currentTimeMillis();

        if (isGameOver) return;
        if (now - gameStartTime >= GAME_DURATION_MS) {
            isGameOver = true;
            Log.d("Game", "Game over!");
            sendNotification.run();  // Optional: trigger game-end notification
            return;
        }

        long deltaTime = now - lastUpdateTime;
        lastUpdateTime = now;

       // Update customers
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

        // Managers update
        applianceManager.update();
    }

    public void draw() {
        canvasUser.accept(canvas -> {
            if (canvas == null) return;

            canvas.drawColor(Color.DKGRAY); // Background

            // Draw appliances
//            for (FoodItem foodItem : createdfoodItems) {
//                    foodItem.draw(canvas);
//            }

            // Draw customers
            for (Customer customer : customers) {
                customer.draw(canvas);
            }

            // Managers draw
            applianceManager.draw(canvas);
            foodSourceManager.draw(canvas);
            foodItemManager.draw(canvas);

            canvas.drawText("Customers: " + customers.size(), 30, 60, textPaint);
            canvas.drawText("Coins: " + coins, 30, 120, textPaint);

            int rating = getRating();
            canvas.drawText("Rating: " + rating + " star(s)", 30, 180, textPaint);

            if (isGameOver) {
                canvas.drawText("Game Over!", 30, 240, textPaint);
                canvas.drawText("Final Rating: " + getRating() + " star(s)", 30, 300, textPaint);
                canvas.drawText("Total Coins: " + coins, 30, 360, textPaint);

                Paint restartText = new Paint();
                restartText.setColor(Color.WHITE);
                restartText.setTextSize(60f);
                restartText.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Tap to Restart", screenWidth / 2f, screenHeight / 2f + 100, restartText);
            }
        });
    }

    public void click(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

//        for (Appliance appliance : appliances) {
//            if (appliance.onClick(x, y)) {
//                FoodItem newColaDrink = new FoodItem(100, 135, "Cola");
//                createdfoodItems.add(newColaDrink);
//
//                Log.d("createdFoodItemsList" ,"createdfoodItems List: " + createdfoodItems.get(0).getFoodItemName());
//                // If the appliance interacted, stop checking others
//                return;
//            }
//        }
//        for (FoodItem foodItem : createdfoodItems) {
//            if (foodItem.onClick(x, y)) {  // Check if the click is within the bounds of the food item
//                draggedFoodItem = foodItem;  // Set the dragged food item
//                Log.d("startdragItem" ,"item picked up: " + draggedFoodItem.getFoodItemName());
//                offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
//                offsetY = y - foodItem.getY();
////                draggedFoodItem.startDrag();
//                return; // Stop checking other food items once we've found the one being dragged
//            }
//        }
        FoodItem foodItem = foodItemManager.handleTouch(event);
        if (foodItem != null) {
            draggedFoodItem= foodItem;  // Set the dragged food item
            Log.d("startdragItem" ,"item picked up: " + draggedFoodItem.getFoodItemName());
            offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
            offsetY = y - foodItem.getY();
//                draggedFoodItem.startDrag();
            return; // Stop checking other food items once we've found the one being dragged
        }

        for (Customer customer : customers) {
            if (x >= customer.getX() && x <= customer.getX() + 100 &&
                    y >= customer.getY() && y <= customer.getY() + 100) {
                Log.d("Game", "Serve Customer");
                customer.serveItem("Cola"); // Assuming you’ll implement this method
                coins+=customer.getReward();
                break;
            }
        }

        // Food Source interaction
        FoodSource source = foodSourceManager.getTouchedSource(x, y);
        if (source != null) {
            Log.d("Game", "FoodSource clicked: " + source.getFoodSourceName());

            // Initialize food item
            foodItem = foodItemManager.createFoodItem(source.getX(), source.getY(), source.getFoodSourceName());
            foodItemManager.addFoodItem(foodItem);
            if (foodItem.getFoodItemName().equals("Cola")) {
                Log.d("foodItem" ,"foodItem: " + foodItem.getFoodItemName());
                if (applianceManager.checkColaMachine()) {
//                     foodItemManager.draw(canvas);
                    foodItem.prepareFoodItem();
                    draggedFoodItem = foodItem;  // Set the dragged food item
                    Log.d("startdragItem" ,"item picked up: " + draggedFoodItem.getFoodItemName());
                    offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
                    offsetY = y - foodItem.getY();
                    applianceManager.pauseColaMachine();
//                draggedFoodItem.startDrag();
                    return; // Stop checking other food items once we've found the one being dragged
                }
            } else {
                 // Take Food Item -> Tagged to an appliance
                applianceManager.assign(foodItem);
            }
//            FoodItem foodItem = new FoodItem(source.getX(), source.getY(), source.getFoodSourceName());
        }

        // Appliance interaction
        applianceManager.handleTouch(event);
    }

    public void drag(MotionEvent event) {
        if (draggedFoodItem != null) {  // Ensure there’s a food item being dragged
            // Calculate the new position for the dragged food item based on mouse/finger movement
//            applianceManager.pauseColaMachine();
//            Log.d("draggedItem" ,"draggedItem: " + draggedFoodItem.getFoodItemName());
            float newX = event.getX() - offsetX;  // Adjust for initial click offset
            float newY = event.getY() - offsetY;
            draggedFoodItem.setPosition(newX, newY);  // Update the food item’s position
//            invalidate();  // Redraw the canvas to update the new position of the food item
        }
    }
    public void release(MotionEvent event) {
        if (draggedFoodItem != null) {
            Log.d("droppedItem" ,"droppedItem: " + draggedFoodItem.getFoodItemName());
            if (draggedFoodItem.getFoodItemName().equals("Cola")) {
                applianceManager.resumeColaMachine();
            }
            // Check if the food item was dropped on a valid destination
            if (isValidDropLocation(draggedFoodItem, event.getX(), event.getY())) {
                // Drop the food item at the new location
                draggedFoodItem.stopDrag();  // Mark it as not being dragged anymore
            } else {
                // Invalid drop location, remove the food item
                foodItemManager.removeFoodItem(draggedFoodItem);
                draggedFoodItem = null;
            }
//            invalidate();  // Refresh the canvas after release
        }
    }

    private boolean isValidDropLocation(FoodItem draggedFoodItem, float x, float y) {
        // Define the valid region (e.g., a specific area on the screen, like an appliance area)
        // Call managers to check if I am in any valid hitboxes
//        FoodSource source = foodSourceManager.getTouchedSource(x, y);

        return x > 100 && y > 100 && x < 500 && y < 500; // Example bounds
    }

    public long getSleepTime() {
        return 16; // ~60fps
    }

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

        customers.add(customer);
    }

    // Helper function to find empty slot index
    private int getNextAvailableSlot() {
        for (int i = 0; i < MAX_CUSTOMER; i++) {
            if (!customerSlots[i]) return i;
        }
        return -1;
    }
    public int getRating(){
        if (coins >= 20 ) return 3;
        else if (coins >= 10 ) return 2;
        else return 1;
    }
    public void restart(){
        this.gameStartTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.lastCustomerSpawn = System.currentTimeMillis();
        this.isGameOver = false;
        this.coins = 0;

        customers.clear();
        for (int i = 0; i < customerSlots.length; i++) {
            customerSlots[i] = false;
        }

        applianceManager.reset();
        foodSourceManager.reset();
    }
    public boolean isGameOver() {
        return isGameOver;
    }
}