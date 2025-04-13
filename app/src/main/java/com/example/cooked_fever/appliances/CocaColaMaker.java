package com.example.cooked_fever.appliances;

import android.graphics.*;

import com.example.cooked_fever.appliances.Appliance;

public class CocaColaMaker implements Appliance {

    private final Rect hitbox;
    private boolean hasGlass = true;
    private boolean isFilling = false;
    private long refillStartTime;
    private final int refillDuration = 5000; // 10 seconds

    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    public CocaColaMaker(int x, int y) {
        hitbox = new Rect(x, y, x + 200, y + 200);
        text.setColor(Color.WHITE);
        text.setTextSize(36f);
        text.setAntiAlias(true);
    }

    @Override
    public void update() {

        // No glass, nothing to do
        if (!hasGlass && !isFilling) {
            return;
        }

        // Start filling
        if (hasGlass && !isFilling) {
//            isFilling = true;
//            refillStartTime = System.currentTimeMillis();
        }

        // Filling coke
        if (isFilling) {
            long now = System.currentTimeMillis();

            // stop filling if time
            if (now - refillStartTime >= refillDuration) {
                isFilling = false;
            }
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

        if (!hasGlass) {
            paint.setColor(Color.LTGRAY); // Empty
        } else if (isFilling) {
            paint.setColor(Color.YELLOW); // Filling
        } else {
            paint.setColor(Color.BLUE); // Full
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
        if (!hasGlass) {
            canvas.drawText("Glass Taken", hitbox.left + 30, hitbox.bottom - 10, text);
        } else if (isFilling) {
            canvas.drawText("Filling...", hitbox.left + 40, hitbox.bottom - 10, text);
        } else {
            canvas.drawText("Ready", hitbox.left + 60, hitbox.bottom - 10, text);
        }
    }


    @Override
    public boolean isReady() {
        return hasGlass && !isFilling;
    }

    public boolean hasGlassReady() {
        return hasGlass && !isFilling;
    }

    public void takeGlass() {
        if (hasGlass && !isFilling) {
            hasGlass = false;
        }
    }

    public void returnGlass() {
        hasGlass = true;
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            takeGlass();
            return true;
        }
        return false;
    }

    @Override
    public Rect getHitbox() {
        return hitbox;
    }
}