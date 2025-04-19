package com.example.cooked_fever.appliances;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.cooked_fever.food.FoodItem;

public interface Appliance {
    void update();
    void draw(Canvas canvas);

    boolean isReady();
    boolean onClick(int x, int y);  // return true if this appliance was interacted with
    Rect getHitbox();

    void reset();
    FoodItem takeFood();
}