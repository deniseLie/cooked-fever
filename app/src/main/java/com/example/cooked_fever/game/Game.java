package com.example.cooked_fever.game;

import android.graphics.*;
import android.view.MotionEvent;
import android.util.Log;
import android.content.*;
import java.util.function.Consumer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cooked_fever.appliances.*;
import com.example.cooked_fever.customers.*;
import com.example.cooked_fever.food.*;
import com.example.cooked_fever.R;

/**
 * A class representing the main logic of this demo
 */

public class Game {

    private final Consumer<Consumer<Canvas>> canvasUser;
    private final Paint customerPaint = new Paint();
    private final Paint appliancePaint = new Paint();
    private final Paint textPaint = new Paint();
    private long lastUpdateTime = System.currentTimeMillis();
    private int screenWidth;
    private int screenHeight;
    private final long GAME_DURATION_MS = 60000; // 1 hour
    private long gameStartTime = System.currentTimeMillis();
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private final Context context;
    private Bitmap kitchenTableBitmap;

    // Managers
    private final ApplianceManager applianceManager;
    private final FoodSourceManager foodSourceManager;
    private final FoodItemManager foodItemManager;
    private final CustomerManager customerManager;
    private final CoinManager coinManager;

    // User Interaction
    private FoodItem draggedFoodItem = null;  // Track which food item is being dragged
    private float offsetX, offsetY;  // Track where the user clicked on the food item to ensure smooth dragging

    public Game(Context context, Consumer<Consumer<Canvas>> canvasUser) {
        this.context = context;
        this.canvasUser = canvasUser;

        // Initialize managers
        this.customerManager = new CustomerManager(context, screenWidth);
        this.applianceManager = new ApplianceManager(context, screenWidth, screenHeight);
        this.foodSourceManager = new FoodSourceManager(context, screenWidth, screenHeight);
        this.foodItemManager = new FoodItemManager(context);
        this.coinManager = new CoinManager(context);

        // Kitchen background sprite
        kitchenTableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.kitchen_table);

        customerPaint.setColor(Color.MAGENTA);
        appliancePaint.setColor(Color.BLUE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setAntiAlias(true);
    }

    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        // Rebuild with correct dimensions
        applianceManager.resize(width, height);

        foodSourceManager.clear();  // optional: if you had one before
        foodSourceManager.setup(screenHeight);
        customerManager.setScreenWidth(width);
    }

    public int getCustomersFulfilled() {
        return customerManager.getCustomersFulfilled();
    }

    public int getCustomersMissed() {
        return customerManager.getCustomersMissed();
    }

    public int getCollectedCoins() {
        return coinManager.getCollectedCoins();
    }

    public void update() {
        long now = System.currentTimeMillis();

        if (isGameOver || !isGameStarted) return;
        if (now - gameStartTime >= GAME_DURATION_MS) {
            isGameOver = true;
            Log.d("Game", "Game over!");
            return;
        }

        lastUpdateTime = now;

        // Managers update
        customerManager.update(now);
        applianceManager.update();

        if (applianceManager.checkColaMachine()) { // drinkReady
            float colaX = applianceManager.getColaMachineX();
            float colaY = applianceManager.getColaMachineY();
            FoodItem colaDrink = new FoodItem(context, colaX+60, colaY-160, "Cola");
            colaDrink.setIsPrepared(true);
            foodItemManager.addFoodItem(colaDrink);
            applianceManager.pauseColaMachine();
        }
        FryMaker fryMaker = applianceManager.getFryMaker();
        if (applianceManager.checkFryMaker(fryMaker)) { // readyFries
            while (applianceManager.isEmptyFryHolder()) {
                FoodItem foodItem = new FoodItem(context, 300, screenHeight - 500, "Fries");
                applianceManager.assign(foodItem);
                foodItemManager.addFoodItem(foodItem);
            }
            applianceManager.stopFrying(fryMaker);
        }
    }

    public void draw() {
        canvasUser.accept(canvas -> {
            if (canvas == null) return;

            canvas.drawColor(Color.DKGRAY); // Background
            customerManager.draw(canvas);

            // Draw the background image scaled
            if (kitchenTableBitmap != null) {
                int canvasWidth = canvas.getWidth();
                int canvasHeight = canvas.getHeight();

                int originalWidth = kitchenTableBitmap.getWidth();
                int originalHeight = kitchenTableBitmap.getHeight();

                float widthScale = (float) canvasWidth / originalWidth;
                float heightScale = 1.4f; // increase this to stretch vertically

                int scaledHeight = (int)(originalHeight * widthScale * heightScale);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(kitchenTableBitmap, canvasWidth, scaledHeight, false);

                int yPosition = (canvasHeight - scaledHeight) / 2 + 250; // center vertically (optional)
                canvas.drawBitmap(scaledBitmap, 0, yPosition, null);
            }

            // Managers draw
            foodSourceManager.draw(canvas, context);
            applianceManager.draw(canvas);
            foodItemManager.draw(canvas, context);
            coinManager.draw(canvas, context);

            if (isGameStarted && !isGameOver) {
                // Timer logic
                long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
                int totalSeconds = (int)(GAME_DURATION_MS / 1000);
                int remaining = Math.max(0, totalSeconds - (int) elapsed);

                float radius = 90f;
                float cx = screenWidth - 130;
                float cy = 130;
                RectF oval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);

                Paint bgCircle = new Paint();
                bgCircle.setColor(Color.DKGRAY);
                bgCircle.setStyle(Paint.Style.STROKE);
                bgCircle.setStrokeWidth(18f);
                bgCircle.setAntiAlias(true);
                canvas.drawArc(oval, 0, 360, false, bgCircle);

                Paint arcPaint = new Paint(bgCircle);
                arcPaint.setColor(remaining <= 10 ? Color.RED : Color.YELLOW);
                float sweepAngle = (remaining / (float) totalSeconds) * 360f;
                canvas.drawArc(oval, -90, sweepAngle, false, arcPaint);

                Paint timerText = new Paint();
                timerText.setColor(Color.WHITE);
                timerText.setTextSize(48f);
                timerText.setTextAlign(Paint.Align.CENTER);
                timerText.setAntiAlias(true);
                canvas.drawText(remaining + "s", cx, cy + 12, timerText);
            }
        });
    }

    public void click(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        FoodItem foodItem = foodItemManager.handleTouch(event);
        if (foodItem != null && foodItem.isDraggable()) {
            draggedFoodItem = foodItem;  // Set the dragged food item
            offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
            offsetY = y - foodItem.getY();
            return; // Stop checking other food items once we've found the one being dragged
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
            // Initialize food item
            foodItem = foodItemManager.createFoodItem(source.getX(), source.getY(), source.getFoodSourceName());

            // Cola Interaction
            if (foodItem.getFoodItemName().equals("Cola")) {

            // Other Food Item Interaction
            } else {
                // Check if got space to create food there
                if (applianceManager.hasPanSpace(foodItem) || applianceManager.hasTableSpace(foodItem) ) {
                    // Take Food Item -> Tagged to an appliance -> Assigns foodItem to a slot
                    applianceManager.assign(foodItem);
                    foodItemManager.addFoodItem(foodItem);
                    return;
                }

            }
            foodItem = null;
        }

        // Appliance interaction
        Appliance appliance = applianceManager.handleTouch(event);
        if (appliance != null) {
            if (appliance instanceof FryMaker) {
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

            // Check if food drop on valid customer, customer food served
            Customer customer = customerManager.handleTouch(event);
            if (customer != null) {
                Boolean validReceive = customerManager.receiveItem(customer, draggedFoodItem);
                if (validReceive) {
                    foodItemManager.removeFoodItem(draggedFoodItem);

//                    // Reward coin for correctly served item
                    coinManager.collectCoin(1);

                    // Extra coin animation for fully served customer
                    if (customer.isServed()) {
                        Coin newCoin = coinManager.addNewCoins(context, customer.id, customer.getX(), customer.getReward());
                        coinManager.addCoin(newCoin);
                    }

                    // If cola, make a new drink
                    if (draggedFoodItem.getFoodItemName().equals("Cola")) {
                        applianceManager.resumeColaMachine();
                        draggedFoodItem.stopDrag();
                        draggedFoodItem = null;
                        return;
                    }

                    // Clean up old appliance
                    Appliance oldAppliance = applianceManager.getApplianceAtCoord(
                            (int) draggedFoodItem.getOriginalX(),
                            (int) draggedFoodItem.getOriginalY()
                    );
                    if (oldAppliance != null) {
                        applianceManager.doTrash(oldAppliance);
                    }
                    draggedFoodItem.stopDrag();
                    draggedFoodItem = null;
                    return;
                }
            }

            // 2. Trash Bin
            Appliance appliance = applianceManager.handleTouch(event);
            if (appliance != null) {
                if (applianceManager.isTrash(appliance)) {
                    foodItemManager.removeFoodItem(draggedFoodItem);

                    if (draggedFoodItem.getFoodItemName().equals("Cola")) {
                        applianceManager.resumeColaMachine();
                        draggedFoodItem.stopDrag();
                        draggedFoodItem = null;
                        return;
                    }
                    applianceManager.doTrash(applianceManager.getApplianceAtCoord(
                            (int) draggedFoodItem.getOriginalX(),
                            (int) draggedFoodItem.getOriginalY()
                    ));

                    coinManager.deductCoin(1);

                    draggedFoodItem.stopDrag();
                    draggedFoodItem = null;
                    return;
                } else if (appliance instanceof FoodWarmer) {
                    Boolean isWarm = applianceManager.keepWarm(draggedFoodItem, (FoodWarmer) appliance);
                    if (isWarm) { // Successful placing -> Empty old
                        Appliance originalAppliance = applianceManager.getApplianceAtCoord((int)draggedFoodItem.getOriginalX(), (int)draggedFoodItem.getOriginalY());
                        applianceManager.doTrash(originalAppliance);
                        FoodWarmer foodWarmer = (FoodWarmer) appliance;
                        draggedFoodItem.setItemOriginalPosition(foodWarmer.getX(), foodWarmer.getY());
                    } else { // Failed placing -> Reset position
                        draggedFoodItem.setItemPosition(draggedFoodItem.getOriginalX(), draggedFoodItem.getOriginalY());
                    }
                    // Stop drag
                    draggedFoodItem.stopDrag();
                    draggedFoodItem = null;
                    return;
                }
            }

            // 4. Combination (e.g., Patty + Bun)
            boolean combinationSucceeded = false;
            String draggedFoodName = draggedFoodItem.getFoodItemName();

            if (draggedFoodName.equals("Patty") || draggedFoodName.equals("Sausage")) {

                // Check if the player dragged to combine
                FoodItem targetItem = foodItemManager.findOtherItemAtTouch(event, draggedFoodItem);

                if (targetItem != null && targetItem != draggedFoodItem && !draggedFoodItem.getIsBadlyCooked()) {
                    // Attempt combination and check success
                    combinationSucceeded = foodItemManager.combine(draggedFoodItem, targetItem);

                    if (combinationSucceeded) {
                        applianceManager.doTrash(applianceManager.getApplianceAtCoord(
                                (int) draggedFoodItem.getOriginalX(),
                                (int) draggedFoodItem.getOriginalY()
                        ));
                    }
                }
            }

            // 5. Reset position if not used
            if (!combinationSucceeded) {
                draggedFoodItem.setItemPosition(draggedFoodItem.getOriginalX(), draggedFoodItem.getOriginalY());
            }

            draggedFoodItem.stopDrag();
            draggedFoodItem = null;
        }
    }

    public int getRating(){
        int coinsCollected = coinManager.getCollectedCoins();
        if (coinsCollected >= 20 ) return 3;
        else if (coinsCollected >= 10 ) return 2;
        else return 1;
    }

    public void restart(){
        this.isGameStarted = true;
        this.gameStartTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.isGameOver = false;

        // reset managers
        customerManager.reset();
        applianceManager.reset();
        foodSourceManager.reset();
        foodItemManager.reset();
        coinManager.reset();
    }
    public boolean isGameOver() {
        return isGameOver;
    }
}