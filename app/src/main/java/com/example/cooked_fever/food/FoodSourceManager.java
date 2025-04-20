package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import android.util.Log;

import java.util.*;

public class FoodSourceManager {

    private final List<FoodSource> sources = new ArrayList<>();
    private final Context context;

    public FoodSourceManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;

        addFoodSource(1050, screenHeight - 650, "BurgerBun");
        addFoodSource(1350, screenHeight - 650, "HotdogBun");
        addFoodSource(1900, screenHeight - 650, "Patty");
        addFoodSource(2200, screenHeight - 650, "Sausage");
//        addFoodSource(400,screenHeight - 550, "Cola");
    }

    public void addFoodSource(int x, int y, String name) {
        sources.add(new FoodSource(context, x, y, name));
    }

    public void draw(Canvas canvas, Context context) {
        for (FoodSource source : sources) {
            source.draw(canvas, context);
        }
    }

    public FoodSource getTouchedSource(float touchX, float touchY) {
        for (FoodSource source : sources) {
            if (source.isTouched(touchX, touchY)) {
                Log.d("FoodSourceManager", "source: " + source.getFoodSourceName());
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
    public void setup(int screenHeight) {
        sources.clear();
        int burgerX = 1050;   // under TableTop left column
        int hotdogX = 1350;  // under TableTop right column
        int pattyX = 1850;   // under Pan left column
        int sausageX = 2150; // under Pan right column
        int y = screenHeight - 150; // below appliances

        addFoodSource(burgerX, y, "BurgerBun");
        addFoodSource(hotdogX, y, "HotdogBun");
        addFoodSource(pattyX, y, "Patty");
        addFoodSource(sausageX, y, "Sausage");
    }
}
