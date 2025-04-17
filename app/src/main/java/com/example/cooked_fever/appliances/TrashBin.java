package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import com.example.cooked_fever.R;

public class TrashBin implements Appliance {
    private float x, y;
    private final Rect hitbox;

    private final Paint text = new Paint();
    private final Bitmap trashBitmap;

    public TrashBin(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        this.hitbox = new Rect(x - 20, y - 20, x + 220, y + 220); // 200 + padding
        this.trashBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trash_can);

        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);
    }

    @Override
    public void update() {
        // No dynamic logic for trash
    }

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

        canvas.drawText("TrashBin", x, y + 250, text);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            Log.d("TrashBin", "Clicked");
            return true;
        }
        return false;
    }

    public boolean isTrashBin(int x, int y) {
        Log.d("TrashBin", "isTrashBin " + x);
        return hitbox.contains(x, y);
    }

    @Override
    public Rect getHitbox() {
        return hitbox;
    }

    @Override
    public void reset() {
        // Nothing to reset for now
    }
}
