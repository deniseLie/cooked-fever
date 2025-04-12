package com.example.cooked_fever;

import android.graphics.*;
import android.graphics.Rect;


public class FoodSource {
    private String foodSourceName;
    private final Rect hitbox;
    private float x, y;
    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    public FoodSource (float x, float y, String foodSourceName) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
        this.x = x;
        this.y = y;
        this.foodSourceName = foodSourceName;
    }

    // Getter
    public String getFoodSourceName () {return this.foodSourceName;}
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText(this.foodSourceName, x - 60, y + 80, text);
    }
}
