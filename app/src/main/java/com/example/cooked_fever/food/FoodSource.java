package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import com.example.cooked_fever.R;

public class FoodSource {
    private String foodSourceName; // Cola, burgerbun, hotdogbun, etc.
    private final Rect hitbox;
    private float x, y;
    private Bitmap sprite;
    private final Paint paint = new Paint();

    public FoodSource(Context context, int x, int y, String foodSourceName) {
        this.x = x;
        this.y = y;
        this.foodSourceName = foodSourceName;

        sprite = BitmapFactory.decodeResource(context.getResources(), loadSprite(context, foodSourceName));

        if (sprite != null) {
            int targetSize = 200; // or try 80 if still too large
            sprite = Bitmap.createScaledBitmap(sprite, targetSize, targetSize, true);

            // Position hitbox based on new size
            int left = x - targetSize / 2;
            int top = y - targetSize / 2;
            int right = left + targetSize;
            int bottom = top + targetSize;
            this.hitbox = new Rect(left, top, right, bottom);
        } else {
            // fallback hitbox
            this.hitbox = new Rect(x - 50, y - 50, x + 50, y + 50);
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
    }

    // Getter
    public String getFoodSourceName () { return foodSourceName; }
    public float getX() { return x; }
    public float getY() { return y; }

    public void draw(Canvas canvas, Context context) {
        if (sprite != null) {
            canvas.drawBitmap(sprite, x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f, null);
        } else {
//            // fallback if bitmap can't load
            paint.setColor(Color.GRAY);
            canvas.drawCircle(x, y, 50, paint);
        }
    }

    public boolean isTouched(float touchX, float touchY) {
        return hitbox.contains((int) touchX, (int) touchY);
    }

    public void reset() {}
}
