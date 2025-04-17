package com.example.cooked_fever.food;

import android.graphics.*;
import android.util.Log;

import java.util.*;

public class FoodSourceManager {

    private final List<FoodSource> sources = new ArrayList<>();

    public FoodSourceManager() {
        // constructor does nothing now
    }

    public void setup(int screenHeight) {
        sources.clear();
        int burgerX = 1050;   // under TableTop left column
        int hotdogX = 1350;  // under TableTop right column
        int pattyX = 1850;   // under Pan left column
        int sausageX = 2150; // under Pan right column
        int y = screenHeight - 150; // below appliances

        addFoodSource(100, y, "Cola");
        addFoodSource(burgerX, y, "BurgerBun");
        addFoodSource(hotdogX, y, "HotdogBun");
        addFoodSource(pattyX, y, "Patty");
        addFoodSource(sausageX, y, "Sausage");
    }

    public void addFoodSource(int x, int y, String name) {
        Log.d("FoodSourcePosition", name + " at x=" + x + ", y=" + y);
        sources.add(new FoodSource(x, y, name));
    }
    public void draw(Canvas canvas) {
        for (FoodSource source : sources) {
            source.draw(canvas);
        }
    }

    public FoodSource getTouchedSource(float touchX, float touchY) {
        for (FoodSource source : sources) {
            if (source.isTouched(touchX, touchY)) {
                Log.d("foodSourceManager" ,"source: " + source.getFoodSourceName());
                return source;
            }
        }
        return null;
    }

    public List<FoodSource> getSources() {
        return sources;
    }

    public void clear() {
        sources.clear();
    }
    public void reset() {
        // If you have a list of food sources, reset them one by one
        for (FoodSource source : sources) {
            source.reset();
        }
    }
}