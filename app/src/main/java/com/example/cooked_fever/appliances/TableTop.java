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
    private final String acceptedFood;  // "BurgerBun" or "HotdogBun"
    private FoodItem currentItem = null;
    private float x, y;

    private final Paint platePaint = new Paint();
    private final Paint itemPaint = new Paint();
    private final Paint textPaint = new Paint();

    private final String LOG_TAG = this.getClass().getSimpleName();

    public TableTop(int x, int y, int width, int height, int id, String acceptedFood) {
        this.id = id;
        this.acceptedFood = acceptedFood;
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.x = (float) x + (float)(width/2);
        this.y = (float) y + (float)(height/2);

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
    }

    @Override
    public boolean onClick(int x, int y) {

//        Log.d("TableTop", "Table " + x);
        if (hitbox.contains(x, y)) {
            if (currentItem != null) {
//                takeFood(); // Take the food when tapped
//                Log.d("TableTop" ,"currentItem: " + currentItem);
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
    public float getX() {return x;}
    public float getY() {return y;}


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
    public void placeFood(FoodItem foodItem, float x, float y) {
        foodItem.setItemPosition(x, y);
        foodItem.setItemOriginalPosition(x, y);
        this.currentItem = foodItem;

    }

    public FoodItem peekFood() {
        return currentItem;
    }
    @Override
    public FoodItem takeFood() {
        FoodItem taken = currentItem;
//        Log.d("TableTop", "currentItem isEmpty? " + isEmpty());
        currentItem = null;
//        Log.d("TableTop", "isEmpty? " + isEmpty());
//        Log.d("TableTop" ,"item taken: " + taken.getFoodItemName());
//        Log.d("TableTop" ,"x: " + taken.getX() + " y: " + taken.getY());
        return taken;
    }

    public void reset() {
        this.currentItem = null; // Clear placed food
    }

}