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
    private long cookingStartTime;
    private final long cookingDuration = 8000; // 8 seconds

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    // Constructor
    public Pan(int x, int y, int width, int height, int index, String acceptedFood) {
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.acceptedFood = acceptedFood;
        this.id = index;

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
    }

    @Override
    public void update() {
        if (isCooking && currentItem != null) {
            long now = System.currentTimeMillis();
            if (now - cookingStartTime >= cookingDuration) {
                isCooking = false; // Cooking done
//                currentItem.setState("Cooked");
            }
        }
    }

    public void draw(Canvas canvas) {
        // Pan base
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(hitbox, paint);

        // Food status
        if (acceptedFood == null) {
            canvas.drawText("Empty", hitbox.left + 20, hitbox.top + 60, textPaint);
        } else if (isCooking) {
            canvas.drawText("Cooking " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        } else {
            canvas.drawText("Cooked " + acceptedFood, hitbox.left + 10, hitbox.top + 60, textPaint);
        }
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            return isReady(); // true if ready to be taken
        }
        return false;
    }

    // GET METHOD
    @Override
    public Rect getHitbox() {
        return hitbox;
    }

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
        return currentItem == null;
    }

    // METHOD
    public boolean placeFood(FoodItem item) {
        if (isEmpty() && item.getName().equals(acceptedFood)) {
            currentItem = item;
            startCooking();
            return true;
        }
        return false;
    }

    private void startCooking() {
        isCooking = true;
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

    // Call when user drags from pan to pick up item
//    public GameItem tryPickupItem() {
//        if (isReady()) {
//            GameItem cooked = new GameItem(currentItem + " (Cooked)");
//            isOnPan = false;
//            return cooked;
//        }
//        return null;
//    }
}