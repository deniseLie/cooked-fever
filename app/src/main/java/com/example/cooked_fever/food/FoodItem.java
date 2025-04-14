package com.example.cooked_fever.food;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class FoodItem {
    private String foodItemName;
    private Boolean isPrepared;
    private Boolean isBadlyCooked;

    private final Rect hitbox;
    private float x, y;
    private float originalX, originalY;
    private boolean isDragged = false; // Track if the item is being dragged
    private boolean isDraggable = true; // Track whether item allow being dragged

    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    // Constructor
    public FoodItem (float x, float y, String foodItemName) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;

        this.foodItemName = foodItemName;
        this.isPrepared = false;
        this.isBadlyCooked = false;
        Log.d("FoodItemCreation", "Created a new FoodItem: " + this.foodItemName);
    }

    // Getter
    public String getFoodItemName () {return this.foodItemName;}
    public Boolean getIsPrepared() {return this.isPrepared;}
    public Boolean getIsBadlyCooked() {return this.isBadlyCooked;}
    public float getX() {return x;}
    public float getY() {return y;}
    public float getOriginalX() {return originalX;}
    public float getOriginalY() {return originalY;}
    public Boolean isDraggable() {return isDraggable;}

    // Setter
    public void prepareFoodItem() {this.isPrepared = true;}
    public void badlyCook() {this.isBadlyCooked = true;}
    public void setItemPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void setItemOriginalPosition(float x, float y) {
        this.originalX = x;
        this.originalY = y;
    }
    public void startDrag() {isDragged = true;} // Start dragging
    public void stopDrag() {isDragged = false;}  // Mark the item as not being dragged anymore
    public void setDraggable(Boolean draggable) {isDraggable = draggable;}

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

    // Draw
    public void draw(Canvas canvas) {
//        Log.d("drawItem" ,"cola drawn: " + this.getFoodItemName());
//        Log.d("Location" ,"x: " + this.getX() + " y: " + this.getY());
        Paint paint = new Paint();
        switch (this.getFoodItemName()) {
            case "Cola":
                paint.setColor(Color.RED);
                Log.d("FoodItem" ,"cola drawn: " + this.getFoodItemName());
//                canvas.drawCircle(x, y, 50, paint);
                break;
            case "Hotdog":
                paint.setColor(Color.RED);
//                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
                break;
            case "Burger":
                paint.setColor(Color.rgb(210, 140, 60));
//                canvas.drawCircle(x, y, 50, paint);
                break;
            case "Patty":
                paint.setColor(Color.rgb(90, 50, 30));
//                canvas.drawCircle(x, y, 50, paint);
                break;
            case "Sausage":
                paint.setColor(Color.rgb(235, 100, 120));
//                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
                break;
        }
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText(this.foodItemName, x - 60, y + 80, text);
        canvas.drawText("Status: " + this.isPrepared, x - 60, y + 100, text);  // Adjust y position (y + 80)
        canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Badly" : "Well"), x - 60, y + 120, text);  // Adjust y position (y + 120)
    }
}
