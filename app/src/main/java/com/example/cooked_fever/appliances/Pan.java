package com.example.cooked_fever.appliances;

import android.graphics.*;

import com.example.cooked_fever.appliances.Appliance;

public class Pan implements Appliance {

    private final Rect hitbox;
    private String itemType = null; // "Burger" or "Sausage"
    private boolean isCooking = false;
    private boolean isOnPan = false;
    private long cookingStartTime;
    private final long cookingDuration = 8000; // 8 seconds

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    public Pan(int x, int y, int width, int height) {
        this.hitbox = new Rect(x, y, x + width, y + height);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32f);
        textPaint.setAntiAlias(true);
    }

    @Override
    public void update() {
        if (isCooking && itemType != null) {
            long now = System.currentTimeMillis();
            if (now - cookingStartTime >= cookingDuration) {
                isCooking = false; // Finished cooking
                // change food state
            }
        }
    }

    public void draw(Canvas canvas) {
        // Pan base
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(hitbox, paint);

        // Food status
        if (itemType == null) {
            canvas.drawText("Empty", hitbox.left + 20, hitbox.top + 60, textPaint);
        } else if (isCooking) {
            canvas.drawText("Cooking " + itemType, hitbox.left + 10, hitbox.top + 60, textPaint);
        } else {
            canvas.drawText("Cooked " + itemType, hitbox.left + 10, hitbox.top + 60, textPaint);
        }
    }

    @Override
    public boolean isReady() {
        return isOnPan && !isCooking;
    }

    @Override
    public Rect getHitbox() {
        return hitbox;
    }

    @Override
    public boolean onClick(int x, int y) {
        return false;
    }

    public void grillItem() {
        isOnPan = true;
        cookingStartTime = System.currentTimeMillis();
    }

    // Call when user drags from pan to pick up item
//    public GameItem tryPickupItem() {
//        if (isReady()) {
//            GameItem cooked = new GameItem(currentItem + " (Cooked)");
//            isOnPan = false;
//            return cooked;
//        }
//        return null;
//    }
}