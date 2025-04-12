package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class FoodItem {
    private String foodItemName;
    private Boolean isPrepared;
    private Boolean isBadlyCooked;
    private final Rect hitbox;
    private float x, y;
    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    public FoodItem (float x, float y, String foodItemName) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
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

    // Interaction
    // Method to check if the user clicked on this food item
//    public boolean onClick(float clickX, float clickY) {
//        return clickX >= x && clickX <= x + width && clickY >= y && clickY <= y + height;
//    }
    public boolean onClick(float clickX, float clickY) {
        // Calculate the distance from the clicked point to the center of the circle (x, y)
        float dx = clickX - this.x; // x coordinate of the circle's center
        float dy = clickY - this.y; // y coordinate of the circle's center
        float distance = (float) Math.sqrt(dx * dx + dy * dy); // Euclidean distance

        // Check if the click is within the circle's radius
        return distance <= 50; // 50 is the radius of the circle
    }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Draw
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText(this.foodItemName, x - 60, y + 80, text);
        canvas.drawText("Status: " + this.isPrepared, x - 60, y + 80, text);  // Adjust y position (y + 80)
        canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Well" : "Badly"), x - 60, y + 120, text);  // Adjust y position (y + 120)
    }
}
