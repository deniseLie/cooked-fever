package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Appliance {
    Rect hitbox;
    boolean isCooking = false;
    long cookStartTime = 0;
    final long COOK_DURATION = 3000; // 3 seconds

    public Appliance(int x, int y) {
        hitbox = new Rect(x, y, x + 200, y + 200);
    }

    public void startCooking() {
        if (isCooking) return;
        isCooking = true;
        cookStartTime = System.currentTimeMillis();

        // Async thread (asynchronous element)
        new Thread(() -> {
            try {
                Thread.sleep(COOK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                isCooking = false;
            }
        }).start();
    }

    public void update() {
        // Optional logic if you want to track cooking progress
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(isCooking ? Color.RED : Color.BLUE);
        canvas.drawRect(hitbox, paint);

        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(36f);
        text.setAntiAlias(true);
        canvas.drawText(isCooking ? "Cooking..." : "Ready", hitbox.left + 20, hitbox.top + 120, text);
    }
}
