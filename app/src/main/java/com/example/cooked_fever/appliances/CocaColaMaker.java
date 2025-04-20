package com.example.cooked_fever.appliances;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.os.*;
import java.util.concurrent.*;
import com.example.cooked_fever.R;
import com.example.cooked_fever.food.*;
import com.example.cooked_fever.utils.SoundUtils;

public class CocaColaMaker implements Appliance {

    private final Rect hitbox;
    private boolean preparingCola;
    private boolean readyCola;
    private boolean servingCola;
    private boolean servedCola;
    private long refillStartTime;
    private final int refillDuration = 6000; // 10 seconds
    private final Context context;
    private final Bitmap spriteFilling;
    private final Bitmap spriteCupEmpty;
    private final Bitmap spriteNoCup;
    private final Bitmap spriteCupFilled;

    // Executor to manage background tasks
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public CocaColaMaker(Context context, int x, int y) {
        this.context = context;
        this.spriteFilling = BitmapFactory.decodeResource(context.getResources(), R.drawable.cola_machine_filling);
        this.spriteCupEmpty = BitmapFactory.decodeResource(context.getResources(), R.drawable.cola_machine_cup_empty);
        this.spriteNoCup = BitmapFactory.decodeResource(context.getResources(), R.drawable.cola_machine);
        this.spriteCupFilled = BitmapFactory.decodeResource(context.getResources(), R.drawable.cola_machine_cup_filled);

        hitbox = new Rect(x, y, x + 300, y + 300);
        preparingCola = false;
        readyCola = false;
        servingCola = false;
        servedCola = true;
    }

    // GET METHOD
    @Override
    public Rect getHitbox() {
        return hitbox;
    }
    @Override
    public boolean isReady() {
        return readyCola;
    }
    public boolean hasDrinkReady() {
        return readyCola;
    }

    // METHOD
    @Override
    public void update() {
        if (servedCola) {
            servedCola = false;
            startFilling();
        }
    }

    public void startFilling() {

        preparingCola = true;
        SoundUtils.playWater();

        executor.execute(() -> {
            try {
                Thread.sleep(refillDuration); // Simulate filling time

                // Once done, update the UI thread
                uiHandler.post(() -> {
                    preparingCola = false;
                    readyCola = true;
                    SoundUtils.playFizz();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void serving() {
        // Handle serving the drink asynchronously.
        executor.execute(() -> {
            try {
                // Simulate the serving process (no delay here in a real case)
                uiHandler.post(() -> {
                    readyCola = false;
                    servingCola = true;
                });
            } catch (Exception e) {
                Log.d("CokeMachine", "Error while serving: " + e.toString());
            }
        });
    }

    public void servingComplete() {
        servedCola = true;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y);
    }

    @Override
    public void reset() {
        preparingCola = false;
        readyCola = false;
        servingCola = false;
        servedCola = true;
        this.refillStartTime = 0;
    }

    @Override
    public FoodItem takeFood() {return null;}

    @Override
    public void draw(Canvas canvas) {
        // Draw glass area
        Rect glassRect = new Rect(hitbox.left + 60, hitbox.top + 100, hitbox.right - 60, hitbox.bottom - 30);

        Bitmap spriteToDraw = null;

        if (preparingCola) {
            spriteToDraw = spriteFilling;
        } else if (readyCola) {
            spriteToDraw = spriteCupFilled;
        } else if (servingCola) {
            spriteToDraw = spriteNoCup;
        } else { // hasGlass && !isFilling && !isFilled
            spriteToDraw = spriteCupEmpty;
        }

        if (spriteToDraw != null) {
            float scaleFactor = 1.7f;
            int scaledWidth = (int)(hitbox.width() * scaleFactor);
            int scaledHeight = (int)(hitbox.height() * scaleFactor);

            Bitmap scaled = Bitmap.createScaledBitmap(spriteToDraw, scaledWidth, scaledHeight, false);

            Matrix matrix = new Matrix();
            matrix.preScale(1, 1); // flip horizontally

            Bitmap flipped = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);

            int drawX = hitbox.left - 30; // adjust horizontal position
            int drawY = hitbox.top - 400;  // adjust vertical position

            canvas.drawBitmap(flipped, drawX, drawY, null);
        }
    }
}