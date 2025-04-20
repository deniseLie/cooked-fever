package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import com.example.cooked_fever.R;
import com.example.cooked_fever.food.FoodItem;

public class TrashBin implements Appliance {
    private float x, y;
    private final Rect hitbox;
    private final Bitmap trashBitmap;

    public TrashBin(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        this.hitbox = new Rect(x - 20, y - 20, x + 220, y + 220); // 200 + padding
        this.trashBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trash_can);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        if (trashBitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(trashBitmap, hitbox.width(), hitbox.height(), false);
            canvas.drawBitmap(scaled, hitbox.left, hitbox.top, null);
        } else {
            Paint fallback = new Paint();
            fallback.setColor(Color.RED);
            canvas.drawRect(hitbox, fallback);
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        return hitbox.contains(x, y);
    }

    @Override
    public Rect getHitbox() {
        return hitbox;
    }

    @Override
    public void reset() {}

    @Override
    public FoodItem takeFood() {
        return null;
    }
}
