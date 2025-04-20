package com.example.cooked_fever.food;

import android.graphics.*;
import android.content.*;
import com.example.cooked_fever.R;
import com.example.cooked_fever.utils.SoundUtils;


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

    // Constructor
    public FoodItem (Context context, float x, float y, String foodItemName) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;

        this.foodItemName = foodItemName;
        this.isPrepared = false;
        this.isBadlyCooked = false;
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
    public void setFoodItemName(String name) {this.foodItemName = name;}
    public void setIsPrepared(Boolean isPrepared) {this.isPrepared = isPrepared;}
    public void prepareFoodItem() {
        this.isPrepared = true;
        SoundUtils.playSizzle();
    }

    public void badlyCook() {
        this.isBadlyCooked = true;
        SoundUtils.playBurnt();
    }
    public void setItemPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void setItemOriginalPosition(float x, float y) {
        this.originalX = x;
        this.originalY = y;
    }
    public void startDrag() {
        isDragged = true;
        SoundUtils.playPickup();
    }

    public void stopDrag() {
        isDragged = false;
        SoundUtils.playPlace();
    }
    public void setDraggable(Boolean draggable) {isDraggable = draggable;}

    public void playSizzleSound() {SoundUtils.playSizzle();}

    // Interaction
    public boolean onClick(float clickX, float clickY) {
        // Calculate the distance from the clicked point to the center of the circle (x, y)
        float dx = clickX - this.x; // x coordinate of the circle's center
        float dy = clickY - this.y; // y coordinate of the circle's center
        float distance = (float) Math.sqrt(dx * dx + dy * dy); // Euclidean distance

        // Check if the click is within the circle's radius
        return distance <= 50; // 50 is the radius of the circle
    }

    private int getDrawableResourceId() {
        switch (foodItemName) {
            case "Cola":
                return isPrepared ? R.drawable.cup_filled : R.drawable.cup_empty;
    
            case "HotdogBun":
                return R.drawable.hotdog_bun;
    
            case "BurgerBun":
                return R.drawable.burger_empty;
    
            case "Patty":
                return isBadlyCooked ? R.drawable.patty_burnt :
                       isPrepared    ? R.drawable.patty_cooked :
                                       R.drawable.patty_raw;
    
            case "Burger":
                return R.drawable.burger_completed;
    
            case "Sausage":
                return isBadlyCooked ? R.drawable.sausage_burnt :
                       isPrepared    ? R.drawable.sausage_cooked :
                                       R.drawable.sausage_raw;
    
            case "Hotdog":
                return R.drawable.hotdog_completed;

            case "Fries":
                return R.drawable.cooked_fries;

            default:
                return R.drawable.patty_raw;
        }
    }

    private int getBitmapSize(String name) {
        if (name.equals("Patty") || name.equals("Sausage")) {
            return 80;
        } else if (name.equals("HotdogBun") || name.equals("Hotdog") ||
                name.equals("BurgerBun") || name.equals("Burger")) {
            return 120;
        }
        return 100;
    }

    public void draw(Canvas canvas, Context context) {
        int resId = getDrawableResourceId();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        if (bitmap != null) {
            int bitmapSize = getBitmapSize(foodItemName);
            int width = (int) (bitmapSize * 1.2f);  // 20% wider
            int height = bitmapSize;
            if (this.getFoodItemName().equals("Cola")) {
                width = (int) (bitmapSize * 1.6f);
                height = (int) (bitmapSize * 1.4f);
            }

            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(scaled, x - bitmapSize / 2f, y - bitmapSize / 2f, null);
        } else {
            // fallback if bitmap can't load
            paint.setColor(Color.GRAY);
            canvas.drawCircle(x, y, 50, paint);
        }
    }
}
