package com.example.cooked_fever.food;

import android.graphics.*;
import android.util.Log;
import android.content.*;
import com.example.cooked_fever.R;
import android.media.SoundPool;
import android.media.AudioAttributes;


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

    // sound
    private static SoundPool soundPool;
    private static boolean soundsLoaded = false;
    private static int sizzleSound;
    private static int burntSound;
    private static int pickupSound;
    private static int placeSound;
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
        Log.d("FoodItemCreation", "Created a new FoodItem: " + this.foodItemName);

        // Load bitmap based on item name
        int resId = getDrawableResourceId();
    }
    // Sound initializer
    public static void initSounds(Context context) {
        if (soundPool != null) return;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(attrs)
                .build();

        sizzleSound = soundPool.load(context, R.raw.pan_sizzle, 1);
        burntSound = soundPool.load(context, R.raw.pan_burnt, 1);
        pickupSound = soundPool.load(context, R.raw.item_pickup, 1);
        placeSound = soundPool.load(context, R.raw.place_item, 1);

        soundsLoaded = true;
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
        if (soundsLoaded) soundPool.play(sizzleSound, 1, 1, 0, 0, 1);
    }
    public void badlyCook() {
        this.isBadlyCooked = true;
        if (soundsLoaded) soundPool.play(burntSound, 1, 1, 0, 0, 1);
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
        if (soundsLoaded) soundPool.play(pickupSound, 1, 1, 0, 0, 1);
    }
    public void stopDrag() {
        isDragged = false;
        if (soundsLoaded) soundPool.play(placeSound, 1, 1, 0, 0, 1);
    }
    public void setDraggable(Boolean draggable) {isDraggable = draggable;}

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

            default:
                return R.drawable.patty_raw;
        }
    }
    
    // Draw
//     public void draw(Canvas canvas) {
// //        Log.d("drawItem" ,"cola drawn: " + this.getFoodItemName());
// //        Log.d("Location" ,"x: " + this.getX() + " y: " + this.getY());
//         Paint paint = new Paint();
//         switch (this.getFoodItemName()) {
//             case "Cola":
//                 paint.setColor(Color.RED);
//                 Log.d("FoodItem" ,"cola drawn: " + this.getFoodItemName());
// //                canvas.drawCircle(x, y, 50, paint);
//                 break;
//             case "HotdogBun":
//                 paint.setColor(Color.RED);
// //                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
//                 break;
//             case "BurgerBun":
//                 paint.setColor(Color.rgb(210, 140, 60));
// //                canvas.drawCircle(x, y, 50, paint);
//                 break;
//             case "Patty":
//                 paint.setColor(Color.rgb(90, 50, 30));
// //                canvas.drawCircle(x, y, 50, paint);
//                 break;
//             case "Sausage":
//                 paint.setColor(Color.rgb(235, 100, 120));
// //                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
//                 break;
//             case "Burger":
//                 paint.setColor(Color.rgb(235, 180, 85));
// //                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
//                 break;
//             case "Hotdog":
//                 paint.setColor(Color.rgb(200, 50, 50));
// //                canvas.drawOval(x + 6, y - 10, x - 6, y + 10, paint);
//                 break;
//         }

//         if (bitmap != null) {
//             Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//             canvas.drawBitmap(scaled, x - 50, y - 50, null);
//         } else {
//             canvas.drawCircle(x, y, 50, paint); // fallback
//         }
        
//         Paint text = new Paint();
//         text.setColor(Color.BLACK);
//         text.setTextSize(32f);
//         text.setAntiAlias(true);

//         canvas.drawText(this.foodItemName, x - 60, y + 80, text);
//         canvas.drawText("Status: " + this.isPrepared, x - 60, y + 100, text);  // Adjust y position (y + 80)
//         canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Badly" : "Well"), x - 60, y + 120, text);  // Adjust y position (y + 120)
//     }

    public void draw(Canvas canvas, Context context) {
        int resId = getDrawableResourceId();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        if (bitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            canvas.drawBitmap(scaled, x - 50, y - 50, null);
        } else {
            // fallback if bitmap can't load
            paint.setColor(Color.GRAY);
            canvas.drawCircle(x, y, 50, paint);
        }

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText(this.foodItemName, x - 60, y + 80, text);
        canvas.drawText("Status: " + this.isPrepared, x - 60, y + 100, text);
        canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Badly" : "Well"), x - 60, y + 120, text);
    }
}
