package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import com.example.cooked_fever.R;
import com.example.cooked_fever.food.FoodItem;

public class FryHolder implements Appliance{
    private int id;
    private final Rect hitbox;
    private FoodItem currentItem;
    private float x, y;
    private final Paint paint = new Paint();
    private final Context context;
    private final Bitmap foodWarmerBitmap;

    public FryHolder (Context context, int x, int y, int width, int height, int index) {
        this.context = context;
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.id = index;
        this.x = (float) x + (float)(width / 2);
        this.y = (float) y + (float)(height / 2);
        currentItem = null;

        // Load the pan image
        foodWarmerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plate);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        if (foodWarmerBitmap != null) {
            Bitmap scaledPan = Bitmap.createScaledBitmap(foodWarmerBitmap, hitbox.width(), hitbox.height(), false);
            canvas.drawBitmap(scaledPan, hitbox.left, hitbox.top, null);
        } else {
            // fallback in case image didn't load
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(hitbox, paint);
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) /*&& currentItem != null*/;
    }

    // GET METHOD
    public Boolean isEmpty() {
        if (currentItem != null) {
            return false;
        } else {
            return true;
        }
    }
    public Boolean placeFood(FoodItem foodItem, float x, float y) {
        if (foodItem.getFoodItemName().equals("Fries")) {
            currentItem = foodItem;
            foodItem.setItemPosition(x, y);
            foodItem.setItemOriginalPosition(x, y);
            return true;
        } else {
            currentItem = null;
            return false;
        }
    }
    @Override
    public FoodItem takeFood() {
        FoodItem item = currentItem;
        currentItem = null;
        return item;
    }
    @Override
    public Rect getHitbox() {return hitbox;}
    public float getX() {return x;}
    public float getY() {return y;}

    @Override
    public void reset() {
        currentItem = null;
    }
}
