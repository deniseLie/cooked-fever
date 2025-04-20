package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import com.example.cooked_fever.R;
import com.example.cooked_fever.food.FoodItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodWarmer implements Appliance{
    private static final ExecutorService executor = Executors.newCachedThreadPool(); // Shared pool
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    private int id;
    private final Rect hitbox;
    private FoodItem currentItem = null;
    private float x, y;
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();
    private final Context context;
    private final Bitmap panBitmap;

    public FoodWarmer (Context context, int x, int y, int width, int height, int index) {
        this.context = context;
        this.hitbox = new Rect(x, y, x + width, y + height);
        this.id = index;
        this.x = (float) x + (float)(width / 2);
        this.y = (float) y + (float)(height / 2);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        // Load the pan image
        panBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_warmer);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
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
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) /*&& currentItem != null*/;
    }

    // GET METHOD
    public Boolean placeFood(FoodItem foodItem, float x, float y) {
        if (currentItem != null) return false;

        switch (foodItem.getFoodItemName()){
            case "Patty":
            case "Sausage":
                foodItem.setItemPosition(x, y);
                currentItem = foodItem;
                return true;
            default:
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


