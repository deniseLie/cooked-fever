package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FoodItem {
    private String foodItemName;
    private Boolean isPrepared;
    private Boolean isBadlyCooked;
    private float x, y;

    public FoodItem (float x, float y, String foodItemName) {
        this.x = x;
        this.y = y;
        this.foodItemName = foodItemName;
        this.isPrepared = false;
        this.isBadlyCooked = false;
    }

    // Setter
    public void prepareFoodItem() {this.isPrepared = true;}
    public void badlyCook() {this.isBadlyCooked = true;}

    // Getter
    public String getFoodItemName () {return this.foodItemName;}
    public Boolean getIsPrepared() {return this.isPrepared;}
    public Boolean getIsBadlyCooked() {return this.isBadlyCooked;}
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

        canvas.drawText(this.foodItemName, x - 60, y + 80, text);


        // Draw the food's cooked status (e.g., "Cooking" or "Cooked")
        canvas.drawText("Status: " + this.isPrepared, x - 60, y + 80, text);  // Adjust y position (y + 80)

        // Draw whether the food is cooked well or badly
        canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Well" : "Badly"), x - 60, y + 120, text);  // Adjust y position (y + 120)
    }
}
