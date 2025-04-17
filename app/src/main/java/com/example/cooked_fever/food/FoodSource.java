package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
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
        this.x = x;
        this.y = y;
        this.foodSourceName = foodSourceName;

        loadSprite(context, foodSourceName);

        if (sprite != null) {
            int targetWidth = (int)(sprite.getWidth() * 0.09);
            int targetHeight = (int)(sprite.getHeight() * 0.09);

            // Position hitbox around the center where the sprite is drawn
            int left = (int)(x - targetWidth / 2f);
            int top = (int)(y - targetHeight / 2f + 40); // same Y adjustment as draw()
            int right = left + targetWidth;
            int bottom = top + targetHeight;

            this.hitbox = new Rect(left, top, right, bottom);
        } else {
            // fallback default hitbox if sprite failed to load
            this.hitbox = new Rect(x - 50, y - 50, x + 50, y + 50);
        }
    }


    private void loadSprite(Context context, String foodName) {
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

        sprite = BitmapFactory.decodeResource(context.getResources(), resId);
    }

    // Getter
    public String getFoodSourceName () { return foodSourceName; }
    public float getX() { return x; }
    public float getY() { return y; }

    public void draw(Canvas canvas) {
        if (sprite != null) {
            // Scale the sprite to a smaller size
            int targetWidth = (int)(sprite.getWidth() * 0.09);   // scale to 40%
            int targetHeight = (int)(sprite.getHeight() * 0.09); // scale to 40%

            Bitmap scaledSprite = Bitmap.createScaledBitmap(sprite, targetWidth, targetHeight, true);

            canvas.drawBitmap(scaledSprite, x - targetWidth / 2f, y - targetHeight / 2f + 40, null);
        } else {
            paint.setColor(Color.GREEN);
            canvas.drawCircle(x, y, 50, paint);
        }
//        text.setColor(Color.BLACK);
//        text.setTextSize(32f);
//        text.setAntiAlias(true);
//        canvas.drawText(this.foodSourceName, x - 60, y + 80, text);
    }


    public boolean isTouched(float touchX, float touchY) {
        return hitbox.contains((int) touchX, (int) touchY);
    }

    public void reset() {
        // No state to reset currently
    }
}
