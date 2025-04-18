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
    private Boolean isCooking;
    private Boolean readyFries;
    private final int fryingDuration = 3000; // 10 seconds

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
        isCooking = false;
        readyFries = false;

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
    public boolean isReady() {
        return readyFries;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y) && !isCooking;
    }

    // GET METHOD
    public void makeFries() {
        isCooking = true;
        Log.d("FryMaker", "cooking status: " + isCooking);
        executor.execute(() -> {
            try {
                Thread.sleep(fryingDuration); // Simulate frying time
                // Once done, update the UI thread
                uiHandler.post(() -> {
                    isCooking = false;
                    readyFries = true;
                    Log.d("FryMaker", "cooking status: " + isCooking);
                    Log.d("FryMaker", "Fries status: " + readyFries);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public FoodItem takeFood() {
            return null;
    }
    @Override
    public Rect getHitbox() {return hitbox;}
    public float getX() {return x;}
    public float getY() {return y;}

    @Override
    public void reset() {
        isCooking = false;
        readyFries = false;
    }

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
}
