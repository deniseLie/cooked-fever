package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.cooked_fever.R;
import com.example.cooked_fever.food.FoodItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FryMaker implements Appliance{
    private static final ExecutorService executor = Executors.newCachedThreadPool(); // Shared pool
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    private int id;
    private final Rect hitbox;
//    private String acceptedFood; // "Patty" or "Sausage"
    private FoodItem currentItem = null;

    private float x, y;
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    private final Context context;
    private final Bitmap panBitmap;

    public FryMaker (Context context, int x, int y, int width, int height, int index) {
        this.context = context;
        this.hitbox = new Rect(x, y, x + width, y + height);
//        this.acceptedFood = acceptedFood;
        this.id = index;
        this.x = (float) x + (float)(width / 2);
        this.y = (float) y + (float)(height / 2);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        // Load the pan image
        panBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pan);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) && currentItem != null;
    }

    // GET METHOD
    public void placeFood(FoodItem foodItem) {
        switch (foodItem.getFoodItemName()){
            case "Patty":
            case "Sausage":
                currentItem = foodItem;
                break;
            default:

                currentItem = null;
        }
    }
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

    }
}
