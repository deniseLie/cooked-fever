package com.example.cooked_fever.appliances;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TableTop {

    private final Rect hitbox;
    private boolean hasBurger = false;

    private final Paint platePaint = new Paint();
    private final Paint itemPaint = new Paint();
    private final Paint textPaint = new Paint();

    private int tableTopIndex = 0;

    public TableTop(int x, int y, int width, int height, int index) {
        tableTopIndex = index;
        hitbox = new Rect(x, y, x + width, y + height);

        platePaint.setColor(Color.LTGRAY);
        itemPaint.setColor(Color.rgb(139, 69, 19)); // burger brown
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {

        // Draw Plate
        canvas.drawRect(hitbox, platePaint);
        canvas.drawText("" + tableTopIndex, hitbox.left + 20, hitbox.top + 30, textPaint);

        // Draw burger as a circle
        if (hasBurger) {
            int centerX = (hitbox.left + hitbox.right) / 2;
            int centerY = (hitbox.top + hitbox.bottom) / 2;
            canvas.drawCircle(centerX, centerY, 40, itemPaint);
            canvas.drawText("Burger", centerX - 30, centerY + 60, textPaint);
        }
    }

    public boolean hasBurger() {
        return hasBurger;
    }

    public void addBurger() {
        hasBurger = true;
    }
    public void removeBurger() {
        hasBurger = false;
    }

    public Rect getHitbox() {
        return hitbox;
    }
}