package com.example.cooked_fever.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.util.Log;
import android.content.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private int screenWidth = 1080;
    private int screenHeight = 1920;

//    private int coins = 0;
    private final long GAME_DURATION_MS = 3600000; // 1 hour
    private long gameStartTime = System.currentTimeMillis();
    private boolean isGameOver = false;

    private final Context context;

    // Managers
    private final ApplianceManager applianceManager;
    private final FoodSourceManager foodSourceManager;
    private final FoodItemManager foodItemManager;
    private final CustomerManager customerManager;
    private final CoinManager coinManager;

    // User Interaction
    private FoodItem draggedFoodItem = null;  // Track which food item is being dragged
    private float offsetX, offsetY;  // Track where the user clicked on the food item to ensure smooth dragging

    public Game(Context context, Runnable sendNotification, Consumer<Consumer<Canvas>> canvasUser) {
        this.context = context;
        this.sendNotification = sendNotification;
        this.canvasUser = canvasUser;

        // Initialize managers
        this.applianceManager = new ApplianceManager(context, screenWidth, screenHeight);
        this.foodSourceManager = new FoodSourceManager(screenWidth, screenHeight);
        this.foodItemManager = new FoodItemManager(context);
        this.customerManager = new CustomerManager();
        this.coinManager = new CoinManager(context);
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

        // Managers update
        customerManager.update(now);
        applianceManager.update();
        if (applianceManager.checkColaMachine()) { // drinkReady
            Log.d("Game", "update: drinkReady");
            float colaX = applianceManager.getColaMachineX();
            float colaY = applianceManager.getColaMachineY();
            foodItemManager.addFoodItem(new FoodItem(context, colaX, colaY, "Cola"));
            applianceManager.pauseColaMachine();
        }
        FryMaker fryMaker = applianceManager.getFryMaker();
        if (applianceManager.checkFryMaker(fryMaker)) { // readyFries
            Log.d("Game", "Fries are ready");
            while (applianceManager.isEmptyFryHolder()) {
                FoodItem foodItem = new FoodItem(context, 300, screenHeight - 500, "Fries");
                applianceManager.assign(foodItem);
                foodItemManager.addFoodItem(foodItem);
            }
            Log.d("Game", "Fries are done");
            applianceManager.stopFrying(fryMaker);
        }
    }

    public void draw() {
        canvasUser.accept(canvas -> {
            if (canvas == null) return;

            canvas.drawColor(Color.DKGRAY); // Background

            // Managers draw
            customerManager.draw(canvas);
            applianceManager.draw(canvas);
            foodSourceManager.draw(canvas);
            foodItemManager.draw(canvas, context);
            coinManager.draw(canvas, context);

            List<Customer> customers = customerManager.getCustomerList();
            canvas.drawText("Customers: " + customers.size(), 30, 60, textPaint);
            canvas.drawText("Coins: " + coinManager.getCollectedCoins(), 30, 120, textPaint);

            int rating = getRating();
            canvas.drawText("Rating: " + rating + " star(s)", 30, 180, textPaint);

            if (isGameOver) {
                canvas.drawText("Game Over!", 30, 240, textPaint);
                canvas.drawText("Final Rating: " + getRating() + " star(s)", 30, 300, textPaint);
                canvas.drawText("Total Coins: " + coinManager.getCollectedCoins(), 30, 360, textPaint);

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
//        Log.d("Game", "x: " + x + " y: " + y);

        FoodItem foodItem = foodItemManager.handleTouch(event);
        if (foodItem != null && foodItem.isDraggable()) {
            draggedFoodItem = foodItem;  // Set the dragged food item
            Log.d("Game-Click" ,"item picked up: " + draggedFoodItem.getFoodItemName());
            offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
            offsetY = y - foodItem.getY();
//                draggedFoodItem.startDrag();

            return; // Stop checking other food items once we've found the one being dragged
        }

        // Customer interaction
        Customer customer = customerManager.handleTouch(event);
        if (customer != null) {
//            Log.d("Game-Click", "Serve Customer");
            customer.serveItem("Cola"); // Assuming you’ll implement this method

            Coin newCoin = coinManager.addNewCoins(context, customer.id, customer.getX(), customer.getReward());
            coinManager.addCoin(newCoin);
            return;
        }

        Coin coinCollected = coinManager.handleTouch(event);
        if (coinCollected != null) {
            coinManager.collectCoin(coinCollected.getCoinAmount());
            coinManager.removeCoin(coinCollected);
            coinCollected = null;
            return;
        }

        // Food Source interaction
        FoodSource source = foodSourceManager.getTouchedSource(x, y);
        if (source != null) {
            Log.d("Game-Click", "FoodSource clicked: " + source.getFoodSourceName());

            // Initialize food item
            foodItem = foodItemManager.createFoodItem(source.getX(), source.getY(), source.getFoodSourceName());
            Log.d("Game-Click" ,"foodItem created: " + foodItem.getFoodItemName());

            // Cola Interaction
            if (foodItem.getFoodItemName().equals("Cola")) {

            // Other Food Item Interaction
            } else {
                // Check if got space to initialize
                // If buger, are there any of 3 slots
                // If Hotdog, are there any of 3 slots
                if (applianceManager.hasPanSpace(foodItem) || applianceManager.hasTableSpace(foodItem) ) {
                    // Take Food Item -> Tagged to an appliance -> Assigns foodItem to a slot
                    applianceManager.assign(foodItem);
//                    foodItemManager.addFoodItem(foodItem);
                    Log.d("Game-Click" ,"appliance source: " + foodItem.getFoodItemName());
                    foodItemManager.addFoodItem(foodItem);
                    return;
                }

            }
            foodItem = null;
//            foodItemManager.addFoodItem(foodItem);
        }

        // Appliance interaction
        Appliance appliance = applianceManager.handleTouch(event);
        if (appliance != null) {
            if (appliance instanceof CocaColaMaker) {
                CocaColaMaker colaMachine = (CocaColaMaker) appliance;
                if (colaMachine.hasDrinkReady()) { // Checks if ready
//                    applianceManager.doColaReady() // Play sound to signify ready?
                    // ??? Idk what to do now since it auto spawns
                } else {
                    // Reflect error message?
//                    applianceManager.doColaNotReady() // Play sound to signal not ready
                }
                colaMachine = null;
            } else if (appliance instanceof FryMaker) {
                Log.d("Game", "Frymaker");
                FryMaker fryMaker = (FryMaker) appliance;
                if (applianceManager.isEmptyFryHolder()) {
                    applianceManager.startFrying(fryMaker);
                }
            }
            appliance = null;
            return;
        }

    }

    public void drag(MotionEvent event) {
        // Ensure there’s a food item being dragged
        if (draggedFoodItem != null) {
            // Calculate the new position for the dragged food item based on mouse/finger movement
            float newX = event.getX() - offsetX;  // Adjust for initial click offset
            float newY = event.getY() - offsetY;
            draggedFoodItem.setItemPosition(newX, newY);  // Update the food item’s position
        }
    }

    public void release(MotionEvent event) {
        if (draggedFoodItem != null) {
            Log.d("droppedItem" ,"droppedItem: " + draggedFoodItem.getFoodItemName());

            // Check if food drop on valid customer, customer food served
            Customer customer = customerManager.handleTouch(event);
            if (customer != null) {
                Boolean validReceive = customerManager.receiveItem(customer, draggedFoodItem);
                if (validReceive) {
                    foodItemManager.removeFoodItem(draggedFoodItem);
                    if (customer.isServed()) {
                        Coin newCoin = coinManager.addNewCoins(context, customer.id, customer.getX(), customer.getReward());
                        coinManager.addCoin(newCoin);
                    }

//                    coins += customer.getReward();

                    // If cola, make a new drink
                    if (draggedFoodItem.getFoodItemName().equals("Cola")) {
                        applianceManager.resumeColaMachine();
                    }
                }

                // Set item not dragged anymore
                applianceManager.doTrash(applianceManager.getApplianceAtCoord((int)draggedFoodItem.getOriginalX(), (int)draggedFoodItem.getOriginalY()));
                draggedFoodItem.setItemPosition(draggedFoodItem.getOriginalX(), draggedFoodItem.getOriginalY());
                draggedFoodItem.stopDrag();
                draggedFoodItem = null;
                return;
            }
            // Trash
            Appliance appliance = applianceManager.handleTouch(event);
            if (appliance != null) {
                if (applianceManager.isTrash(appliance)) {
                    Log.d("Game" ,"Trashed: " + draggedFoodItem.getFoodItemName());
                    applianceManager.doTrash(applianceManager.getApplianceAtCoord((int)draggedFoodItem.getOriginalX(), (int)draggedFoodItem.getOriginalY()));
                    if (draggedFoodItem.getFoodItemName().equals("Cola")) {
                        applianceManager.resumeColaMachine();
                    }
                    foodItemManager.removeFoodItem(draggedFoodItem);
                    draggedFoodItem.stopDrag();
                    draggedFoodItem = null;
                    return;
                } else if (appliance instanceof FoodWarmer) {
                    Boolean isWarm = applianceManager.keepWarm(draggedFoodItem, (FoodWarmer) appliance);
                    if (isWarm) { // Successful placing -> Empty old
                        applianceManager.doTrash(applianceManager.getApplianceAtCoord((int)draggedFoodItem.getOriginalX(), (int)draggedFoodItem.getOriginalY()));
                        draggedFoodItem.setItemOriginalPosition(draggedFoodItem.getX(), draggedFoodItem.getY());
                    } else { // Failed placing -> Reset position
                        draggedFoodItem.setItemPosition(draggedFoodItem.getOriginalX(), draggedFoodItem.getOriginalY());
                    }
                    // Stop drag
                    draggedFoodItem.stopDrag();
                    draggedFoodItem = null;
                    return;
                }
            }

            // Handle food combination
            boolean combinationSucceeded = false;
            String draggedFoodName = draggedFoodItem.getFoodItemName();

            if (draggedFoodName.equals("Patty") || draggedFoodName.equals("Sausage")) {

                // Check if the player dragged to combine
                FoodItem targetItem = foodItemManager.findOtherItemAtTouch(event, draggedFoodItem);

                if (targetItem != null && targetItem != draggedFoodItem && !draggedFoodItem.getIsBadlyCooked()) {
                    // Attempt combination and check success
                    combinationSucceeded = foodItemManager.combine(draggedFoodItem, targetItem);
                    applianceManager.doTrash(applianceManager.getApplianceAtCoord((int)draggedFoodItem.getOriginalX(), (int)draggedFoodItem.getOriginalY()));
                }
            }

            // Reset position if no valid interaction occurred
            if (!combinationSucceeded) {
                draggedFoodItem.setItemPosition(draggedFoodItem.getOriginalX(), draggedFoodItem.getOriginalY());
            }

            draggedFoodItem.stopDrag();
            draggedFoodItem = null;

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


    public int getRating(){
        int coinsCollected = coinManager.getCollectedCoins();
        if (coinsCollected >= 20 ) return 3;
        else if (coinsCollected >= 10 ) return 2;
        else return 1;
    }
    public void restart(){
        this.gameStartTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.isGameOver = false;
//        this.coins = 0;

        // reset managers
        customerManager.reset();
        applianceManager.reset();
        foodSourceManager.reset();
        coinManager.reset();
    }
    public boolean isGameOver() {
        return isGameOver;
    }
}