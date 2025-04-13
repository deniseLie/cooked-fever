package com.example.cooked_fever.food;

import android.graphics.*;

public class FoodSource {
    private String foodSourceName; //Cola, burgerbun, hotdogbun,
    private final Rect hitbox;
    private float x, y;

    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    public FoodSource (int x, int y, String foodSourceName) {
        hitbox = new Rect(x-20, y-20, x + 200 + 20, y + 200 + 20);
        this.x = x;
        this.y = y;
        this.foodSourceName = foodSourceName;
    }

    // Getter
    public String getFoodSourceName () {return foodSourceName;}
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

    public boolean isTouched(float touchX, float touchY) {
        return hitbox.contains((int) touchX, (int) touchY);
    }

    public void reset(){

    }
}