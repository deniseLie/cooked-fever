package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.os.*;
import java.util.concurrent.*;
import android.content.*;
import com.example.cooked_fever.R;

import com.example.cooked_fever.appliances.Appliance;
import com.example.cooked_fever.food.*;

public class CocaColaMaker implements Appliance {

    private final Rect hitbox;
    // Preparing = hasGlass, isFilling, !isFilled
    // Ready = hasGlass, !isFilling, isFilled
    // Serving = !hasGlass, !isFilling, isFilled
    // Serving complete = hasGlass, !isFilling, !isFilled
//    private boolean hasGlass = true;
//    private boolean isFilling = false;
//    private boolean isFilled = false;
    private boolean preparingCola = true;
    private boolean readyCola = false;
    private boolean servingCola = false;
    private boolean servedCola = false;
    private long refillStartTime;
    private final int refillDuration = 10000; // 10 seconds

    private final Context context;

    private final Paint paint = new Paint();
    private final Paint text = new Paint();
//    private final FoodItemManager foodItemManager; // ✅ move initialization into constructor

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

        hitbox = new Rect(x, y, x + 200, y + 200);
        text.setColor(Color.WHITE);
        text.setTextSize(36f);
        text.setAntiAlias(true);
//        this.foodItemManager = new FoodItemManager(context);

        // Start Filling
        startFilling();
    }

    // GET METHOD
    @Override
    public Rect getHitbox() {
        return hitbox;
    }
    @Override
//    public boolean isReady() {
//        return hasGlass && !isFilling && isFilled;
//    }
    public boolean isReady() {
        return readyCola;
    }
    public boolean hasDrinkReady() {
        return readyCola;
    }

    // METHOD
    @Override
    public void update() {
        // Trigger fill when needed — this is called every frame
//        if (hasGlass && isFilling && !isFilled) {
//            startFilling();
//        }
        if (servedCola) {
            servedCola = false;
            startFilling();
        }
        if (servingCola) {
            return;
        }

        // Trigger return glass process if needed
        // Serving Complete
        // DENISE PS : I DONT THINK WE NEED THIS5
//        if (hasGlass && !isFilling && !isFilled) {
//            returnGlass();
//        }
    }

    public void startFilling() {
        preparingCola = true;

        executor.execute(() -> {
            try {
                Log.d("CokeMachine" ,"Filling");
                Thread.sleep(refillDuration); // Simulate filling time

                // Once done, update the UI thread
                uiHandler.post(() -> {
                    preparingCola = false;
                    readyCola = true;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("CokeMachine" ,"Coke Spilled: " + e.toString());
            }
        });
    }

    public void serving() {
        // Handle serving the drink asynchronously.
        executor.execute(() -> {
            try {
                Log.d("CokeMachine", "Serving");
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
        if (hitbox.contains(x, y)) {
//            if (isReady()) {
//                serving();
                return true;
//            } else {
//                return false;
//            }
        }
        return false;
    }

    @Override
    public void reset() {
        executor.shutdownNow(); // Stop background threads
        preparingCola = true;
        readyCola = false;
        servingCola = false;
        servedCola = false;
        this.refillStartTime = 0;
    }

    @Override
    public FoodItem takeFood() {return null;}

    @Override
    public void draw(Canvas canvas) {
        // Draw machine body with rounded rectangle
        paint.setColor(Color.DKGRAY);
        canvas.drawRoundRect(
                hitbox.left, hitbox.top, hitbox.right, hitbox.bottom,
                20f, 20f, paint
        );

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
            Bitmap scaled = Bitmap.createScaledBitmap(spriteToDraw, hitbox.width(), hitbox.height(), false);
            canvas.drawBitmap(scaled, hitbox.left, hitbox.top, null);
        }

        // Draw outline of glass
        Paint border = new Paint();
        border.setColor(Color.WHITE);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(4f);
        canvas.drawRect(glassRect, border);

        // Draw label "Cola Machine"
        text.setColor(Color.WHITE);
        text.setTextSize(36f);
        canvas.drawText("Cola Maker", hitbox.left + 30, hitbox.top + 40, text);

        // Draw status
        text.setTextSize(28f);
        if (preparingCola) {
            canvas.drawText("Preparing", hitbox.left + 30, hitbox.bottom - 10, text);
        } else if (readyCola) {
            canvas.drawText("Ready", hitbox.left + 40, hitbox.bottom - 10, text);
        } else if (servingCola) {
            canvas.drawText("Serving", hitbox.left + 60, hitbox.bottom - 10, text);
        } else { // hasGlass && !isFilling && !isFilled
            canvas.drawText("Returning Glass", hitbox.left + 60, hitbox.bottom - 10, text); // Serving complete
        }
    }
}