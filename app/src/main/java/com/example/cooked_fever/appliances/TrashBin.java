package com.example.cooked_fever.appliances;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.cooked_fever.food.FoodItem;

public class TrashBin implements Appliance {
    private float x, y;
    private final Rect hitbox;

    private final Paint paint = new Paint();
    private final Paint text = new Paint();
    public TrashBin (int x, int y) {
        hitbox = new Rect(x-20, y-20, x + 200 + 20, y + 200 + 20);
        this.x = (int) x;
        this.y = (int) y;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(x-20, y-20, x+ 200 + 20, y + 200 + 20, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText("TrashBin", x, y+110, text);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean onClick(int x, int y) {
        if (hitbox.contains(x, y)) {
            Log.d("TrashBin" ,"Clicked");
            return true;
        }
        return false;
    }

    public boolean isTrashBin(int x, int y) {
        Log.d("TrashBin", "isTrashBin " + x);
        if (hitbox.contains(x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public Rect getHitbox() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public FoodItem takeFood() {
        return null;
    }
}
