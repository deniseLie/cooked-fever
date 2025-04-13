package com.example.cooked_fever.appliances;

import android.graphics.*;
import android.util.Log;

import com.example.cooked_fever.appliances.Appliance;
import com.example.cooked_fever.food.*;

public class CocaColaMaker implements Appliance {

    private final Rect hitbox;
    // Preparing = hasGlass, isFilling, !isFilled
    // Ready = hasGlass, !isFilling, isFilled
    // Serving = !hasGlass, !isFilling, isFilled
    // Serving complete = hasGlass, !isFilling, !isFilled
    private boolean hasGlass = true;
    private boolean isFilling = false;
    private boolean isFilled = true;
    private long refillStartTime;
    private final int refillDuration = 5000; // 10 seconds

    private final Paint paint = new Paint();
    private final Paint text = new Paint();
    private final FoodItemManager foodItemManager = new FoodItemManager();

    public CocaColaMaker(int x, int y) {
        hitbox = new Rect(x, y, x + 200, y + 200);
        text.setColor(Color.WHITE);
        text.setTextSize(36f);
        text.setAntiAlias(true);
    }

    @Override
    public void update() {
//        // No glass, nothing to do
//        if (!isFilled && !hasGlass && !isFilling) {
//            hasGlass = true;
//            return;
//        }

//        // Start filling
//        if (hasGlass && !isFilling && !isFilled) {
//            isFilling = true;
//            refillStartTime = System.currentTimeMillis();
//        }

        // Preparing coke
        if (hasGlass && isFilling && !isFilled) {
            try {
                Log.d("CokeMachine" ,"Filling");
                Thread.sleep(refillDuration); // Pauses the thread for 2 seconds (2000 milliseconds)
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Log.d("CokeMachine" ,"Coke Spilled: " + e.toString());
            }
            isFilled = true;
            isFilling = false;
            Log.d("CokeMachine" ,"Filled");

//            long now = System.currentTimeMillis();
//
//            // stop filling if time
//            if (now - refillStartTime >= refillDuration) {
//                isFilling = false;
//                isFilled = true;
//                return;
//            }
        }
        // Ready
        if (hasGlass && !isFilling && isFilled) {
            return;
        }
        // Serving
        if (!hasGlass && !isFilling && isFilled) {
            return;
        }
        // Serving Complete
        if (hasGlass && !isFilling && !isFilled) {
            try {
                Log.d("CokeMachine" ,"Returning Glass");
                Thread.sleep(2000); // Pauses the thread for 2 seconds (2000 milliseconds)
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Log.d("CokeMachine" ,"Filling error: " + e.toString());
            }
            isFilling = true;
        }
    }

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

        if (hasGlass && isFilling && !isFilled) {
            paint.setColor(Color.YELLOW); // Preparing
        } else if (hasGlass && !isFilling && isFilled) {
            paint.setColor(Color.GREEN); // Ready
        } else if (!hasGlass && !isFilling && isFilled){
            paint.setColor(Color.BLUE); // Serving
        } else { // hasGlass && !isFilling && !isFilled
            paint.setColor(Color.LTGRAY); // Serving complete
        }
        canvas.drawRect(glassRect, paint);

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
        if (hasGlass && isFilling && !isFilled) {
            canvas.drawText("Preparing", hitbox.left + 30, hitbox.bottom - 10, text);
        } else if (hasGlass && !isFilling && isFilled) {
            canvas.drawText("Ready", hitbox.left + 40, hitbox.bottom - 10, text);
        } else if (!hasGlass && !isFilling && isFilled) {
            canvas.drawText("Serving", hitbox.left + 60, hitbox.bottom - 10, text);
        } else { // hasGlass && !isFilling && !isFilled
            canvas.drawText("Returning Glass", hitbox.left + 60, hitbox.bottom - 10, text); // Serving complete
        }
    }

    @Override
    public boolean isReady() {
        return hasGlass && !isFilling && isFilled;
    }

    public boolean hasDrinkReady() {
        return hasGlass && !isFilling && !isFilled;
    }

//    public void takeGlass() {
//        hasGlass = false;
//        isFilled = false;
//        isFilling = false;
//    }

    public void serving() {
        hasGlass = false;
        isFilling = false;
        isFilled = true;
    }
    public void servingComplete() {
        hasGlass = true;
        isFilling = false;
        isFilled = false;
    }

    public void returnGlass() {
        hasGlass = true;
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            if (isReady()) {
                serving();
                return true;
            } else {
                return false;
            }
//            return true;
        }
        return false;
    }

    @Override
    public Rect getHitbox() {
        return hitbox;
    }

    @Override
    public void reset() {
        this.hasGlass = true;         // Refill complete – ready for use
        this.isFilling = false;       // No longer actively filling
        this.refillStartTime = 0;
    }
}