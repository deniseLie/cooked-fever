package com.example.cooked_fever.appliances;

import android.graphics.*;

import com.example.cooked_fever.appliances.Appliance;

import com.example.cooked_fever.food.*;

public class Pan implements Appliance {

    private int id;
    private final Rect hitbox;
    private String acceptedFood; // "Patty" or "Sausage"
    private FoodItem currentItem = null;

    private boolean isCooking = false;
    private boolean isBurnt = false;
    private long cookingStartTime;
    private final long cookingDuration = 10000; // 10 seconds
    private final long burntDuration = 30000; // 30 seconds

    private float x, y;
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    // Constructor
    public Pan(int x, int y, int width, int height, int index, String acceptedFood) {
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.acceptedFood = acceptedFood;
        this.id = index;
        this.x = (float) x + (float)(width/2);
        this.y = (float) y + (float)(height/2);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
    }

    @Override
    public void update() {

        // If Item on Pan
        if (currentItem != null) {
            long now = System.currentTimeMillis();

            // Cooking
            if (isCooking && now - cookingStartTime >= cookingDuration) {
                isCooking = false; // Cooking done
                currentItem.setDraggable(true);
                currentItem.prepareFoodItem();
            }

            // Burnt
            if (!isCooking && now - cookingStartTime >= burntDuration) {
                isBurnt = true;
                currentItem.badlyCook();
            }
        }
    }

    public void draw(Canvas canvas) {
        // Pan base
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(hitbox, paint);

        // Food status
        if (currentItem == null) {
            canvas.drawText("Empty", hitbox.left + 20, hitbox.top + 60, textPaint);
        } else if (isCooking) {
            canvas.drawText("Cooking " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        } else if (!isCooking && !isBurnt) {
            canvas.drawText("Cooked " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        } else if (!isCooking && isBurnt) {
            canvas.drawText("Burnt " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        } else {
            canvas.drawText("Invalid " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        }
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            if (currentItem != null) {
//                takeFood(); // Take the food when tapped
                return true;
            }
        }
        return false;
    }

    // GET METHOD
    @Override
    public Rect getHitbox() {
        return hitbox;
    }
    public float getX() {return x;}
    public float getY() {return y;}

    public int getId() {
        return id;
    }

    public String getAcceptedFood() {
        return acceptedFood;
    }

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
            startCooking();
            return true;
        }
        return false;
    }

    private void startCooking() {
        isCooking = true;
        currentItem.setDraggable(false);
        cookingStartTime = System.currentTimeMillis();
    }

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
        this.cookingStartTime = 0;
    }
}