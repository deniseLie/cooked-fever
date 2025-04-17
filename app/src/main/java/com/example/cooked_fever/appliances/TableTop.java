package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.util.Log;

import com.example.cooked_fever.R;
import com.example.cooked_fever.food.FoodItem;

public class TableTop implements Appliance {

    private final int id;
    private final Rect hitbox;
    private final String acceptedFood;
    private FoodItem currentItem = null;
    private float x, y;

    private final Paint textPaint = new Paint();
    private final Bitmap tabletopBitmap;

    public TableTop(Context context, int x, int y, int width, int height, int id, String acceptedFood) {
        this.id = id;
        this.acceptedFood = acceptedFood;
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.x = x + width / 2f;
        this.y = y + height / 2f;

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        this.tabletopBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plate);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        if (tabletopBitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(tabletopBitmap, hitbox.width(), hitbox.height(), false);
            canvas.drawBitmap(scaled, hitbox.left, hitbox.top, null);
        }
    }


    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) && currentItem != null;
    }

    @Override
    public Rect getHitbox() { return hitbox; }

    public int getId() { return id; }
    public float getX() { return x; }
    public float getY() { return y; }

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

    public void placeFood(FoodItem foodItem, float x, float y) {
        foodItem.setItemPosition(x, y);
        foodItem.setItemOriginalPosition(x, y);
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

    public void reset() {
        this.currentItem = null;
    }
}
