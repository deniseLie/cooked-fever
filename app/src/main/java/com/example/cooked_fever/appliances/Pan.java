package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.os.*;
import java.util.concurrent.*;
import com.example.cooked_fever.R;
import com.example.cooked_fever.food.*;

public class Pan implements Appliance {
    private static final ExecutorService executor = Executors.newCachedThreadPool(); // Shared pool
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private int id;
    private final Rect hitbox;
    private String acceptedFood; // "Patty" or "Sausage"
    private FoodItem currentItem = null;
    private boolean isCooking = false;
    private boolean isBurnt = false;
    private final long cookingDuration = 10000; // 10 seconds
    private final long burntDuration = 30000; // 30 seconds
    private float x, y;
    private final Paint paint = new Paint();
    private final Context context;
    private final Bitmap panBitmap;

    // Constructor
    public Pan(Context context, int x, int y, int width, int height, int index, String acceptedFood) {
        this.context = context;
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.acceptedFood = acceptedFood;
        this.id = index;
        this.x = (float) x + (float)(width / 2);
        this.y = (float) y + (float)(height / 2);
    
        // Load the pan image
        panBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pan);
    }

    @Override
    public void update() {}

    public void draw(Canvas canvas) {
        // Pan base
        if (panBitmap != null) {
            Bitmap scaledPan = Bitmap.createScaledBitmap(panBitmap, hitbox.width(), hitbox.height(), false);
            canvas.drawBitmap(scaledPan, hitbox.left, hitbox.top, null);
        } else {
            // fallback in case image didn't load
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(hitbox, paint);
        }
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) && currentItem != null;
    }

    // GET METHOD
    @Override
    public Rect getHitbox() {
        return hitbox;
    }
    public float getX() {return x;}
    public float getY() {return y;}

    // CHECK METHOD
    public boolean isEmpty() {
        return currentItem == null;
    }

    @Override
    public boolean isReady() {
        return currentItem != null && !isCooking;
    }

    public boolean accepts(String foodName) {
        return acceptedFood.equals(foodName);
    }

    // METHOD
    public boolean placeFood(FoodItem item, float x, float y) {
        if (isEmpty() && item.getFoodItemName().equals(acceptedFood)) {
            item.setItemPosition(x, y);
            item.setItemOriginalPosition(x, y);
            currentItem = item;
            startCookingAsync(item);
            return true;
        }
        return false;
    }

    private void startCookingAsync(FoodItem item) {
        isCooking = true;
        isBurnt = false;
        item.setDraggable(false);

        // Run in the background
        executor.execute(() -> {
            try {

                // Waiting for Cooking done
                Thread.sleep(cookingDuration);
                uiHandler.post(() -> {
                    isCooking = false;
                    item.prepareFoodItem();
                    item.setDraggable(true);
                });

                // Waiting for Food Burnt
                Thread.sleep(burntDuration - cookingDuration);
                uiHandler.post(() -> {
                    if (!isCooking && currentItem == item) {
                        isBurnt = true;
                        item.badlyCook();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public FoodItem takeFood() {
        if (isReady()) {
            FoodItem item = currentItem;
            currentItem = null;
            return item;
        }
        return null;
    }

    @Override
    public void reset() {
        this.currentItem = null;
        this.isCooking = false;
        this.isBurnt = false;
    }
}