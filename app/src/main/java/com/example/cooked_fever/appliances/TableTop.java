package com.example.cooked_fever.appliances;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.cooked_fever.food.FoodItem;

public class TableTop implements Appliance {

    private final int id;
    private final Rect hitbox;
    private final String acceptedFood;  // "Burger" or "Hotdot"
    private FoodItem currentItem = null;

    private final Paint platePaint = new Paint();
    private final Paint itemPaint = new Paint();
    private final Paint textPaint = new Paint();

    private final String LOG_TAG = this.getClass().getSimpleName();

    public TableTop(int x, int y, int width, int height, int id, String acceptedFood) {
        this.id = id;
        this.acceptedFood = acceptedFood;
        this.hitbox = new Rect(x, y, x + width, y + height);

        platePaint.setColor(Color.LTGRAY);
        itemPaint.setColor(Color.rgb(139, 69, 19)); // Placeholder: brown
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
    }

    @Override
    public void update() {
        // No behavior needed for passive surface
    }

    @Override
    public void draw(Canvas canvas) {
        // Draw tabletop
        canvas.drawRect(hitbox, platePaint);
        canvas.drawText("T" + id, hitbox.left + 20, hitbox.top + 30, textPaint);

        // Draw food if present
        if (currentItem != null) {
            int centerX = (hitbox.left + hitbox.right) / 2;
            int centerY = (hitbox.top + hitbox.bottom) / 2;
            canvas.drawCircle(centerX, centerY, 40, itemPaint);
            canvas.drawText(currentItem.getName(), centerX - 40, centerY + 60, textPaint);
        }
    }

    @Override
    public boolean onClick(int x, int y) {

        Log.d("TableTop", "Table " + x);
        if (hitbox.contains(x, y)) {
            if (currentItem != null) {
                takeFood(); // Take the food when tapped
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

    public int getId() {
        return id;
    }

    // CHECK METHOD
    @Override
    public boolean isReady() {
        return currentItem != null;
    }

    public boolean isEmpty() {
        return currentItem == null;
    }

    public boolean accepts(String foodName) {
        return acceptedFood.equals(foodName);
    }

    // METHOD
    public void placeFood(FoodItem foodItem) {
        this.currentItem = foodItem;
    }

    public FoodItem peekFood() {
        return currentItem;
    }

    public FoodItem takeFood() {
        FoodItem taken = currentItem;
        currentItem = null;
        return taken;
    }


}