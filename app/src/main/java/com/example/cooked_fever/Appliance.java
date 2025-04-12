package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface Appliance {
    void update();
    void draw(Canvas canvas);

    boolean isReady();
    boolean onClick(float x, float y);  // return true if this appliance was interacted with
    Rect getHitbox();
}