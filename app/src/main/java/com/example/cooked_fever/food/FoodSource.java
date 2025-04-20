package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.cooked_fever.R;

public class FoodSource {
    private String foodSourceName; // Cola, burgerbun, hotdogbun, etc.
    private final Rect hitbox;
    private float x, y;
    private Bitmap sprite;

    private final Paint paint = new Paint();
    private final Paint text = new Paint();

public FoodSource(Context context, int x, int y, String foodSourceName) {
//        hitbox = new Rect(x - 50, y - 50, x + 50, y + 50);
    this.x = x;
    this.y = y;
    this.foodSourceName = foodSourceName;

    sprite = BitmapFactory.decodeResource(context.getResources(), loadSprite(context, foodSourceName));

    if (sprite != null) {
        int targetWidth = (int)(sprite.getWidth() * 0.09);
        int targetHeight = (int)(sprite.getHeight() * 0.09);

        // Position hitbox around the center where the sprite is drawn
        int left = (int)(x - targetWidth / 2f);
        int top = (int)(y - targetHeight / 2f + 40); // same Y adjustment as draw()
        int right = left + targetWidth;
        int bottom = top + targetHeight;
        Log.d("FoodSource", "sprite not null");
        this.hitbox = new Rect(left, top, right, bottom);
    } else {
        Log.d("FoodSource", "sprite is null??");
        // fallback default hitbox if sprite failed to load
//            this.hitbox = new Rect(x - 50, y - 50, x + 50, y + 50);
        hitbox = new Rect(x - 50, y - 50, x + 50, y + 50);
    }
}


    private int loadSprite(Context context, String foodName) {
        int resId;

        switch (foodName.toLowerCase()) {
            case "burgerbun":
                resId = R.drawable.burger_bun_source;
                break;
            case "patty":
            case "burgerpatty":
                resId = R.drawable.burger_patty_source;
                break;
            case "hotdogbun":
                resId = R.drawable.hotdog_bun_source;
                break;
            case "sausage":
            case "hotdograw":
                resId = R.drawable.hotdog_raw_source;
                break;
            default:
                resId = R.drawable.plate; // fallback image
        }
        return resId;
//        sprite = BitmapFactory.decodeResource(context.getResources(), resId);
    }

    // Getter
    public String getFoodSourceName () { return foodSourceName; }
    public float getX() { return x; }
    public float getY() { return y; }

    public void draw(Canvas canvas, Context context) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(this.hitbox, paint);
//
//        Paint text = new Paint();
//        text.setColor(Color.BLACK);
//        text.setTextSize(32f);
//        text.setAntiAlias(true);
//
//        canvas.drawText(this.foodSourceName, x - 60, y + 80, text);

//        int resId = loadSprite(context, foodSourceName);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap bitmap = this.sprite;

        if (bitmap != null) {
            int bitmapSize = getBitmapSize(foodSourceName);
//            int bitmapSize = this.sprite.getHeight();
//            int width = (int) (bitmapSize * 1.2f);  // 20% wider
//            int height = bitmapSize;
            int width = this.sprite.getWidth();
            int height = this.sprite.getHeight();

            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(scaled, this.x - bitmapSize / 2f, this.y - bitmapSize / 2f, null);
        } else {
            // fallback if bitmap can't load
            paint.setColor(Color.GRAY);
            canvas.drawCircle(x, y, 50, paint);
        }

//        Paint text = new Paint();
//        text.setColor(Color.BLACK);
//        text.setTextSize(32f);
//        text.setAntiAlias(true);

//        canvas.drawText(this.foodItemName, x - 60, y + 80, text);
//        canvas.drawText("Status: " + this.isPrepared, x - 60, y + 100, text);
//        canvas.drawText("Cooked: " + (this.isBadlyCooked ? "Badly" : "Well"), x - 60, y + 120, text);
    }

    private int getBitmapSize(String name) {

        switch (name.toLowerCase()) {
            case "patty":
            case "sausage":
                return 80;
            case "hotdogbun":
            case "hotdog":
            case "burgerbun":
            case "burger":
                return 120;
            default:
                return 100; // fallback image
        }
//        if (name.equals("Patty") || name.equals("Sausage")) {
//            return 80;
//        } else if (name.equals("HotdogBun") || name.equals("Hotdog") ||
//                name.equals("BurgerBun") || name.equals("Burger")) {
//            return 120;
//        }
//        return 100;
    }


    public boolean isTouched(float touchX, float touchY) {
        return hitbox.contains((int) touchX, (int) touchY);
    }

    public void reset() {
        // No state to reset currently
    }
}
