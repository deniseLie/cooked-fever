package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import java.util.*;

public class FoodSourceManager {

    private final List<FoodSource> sources = new ArrayList<>();
    private final Context context;

    public FoodSourceManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
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
//                Log.d("FoodSourceManager", "source: " + source.getFoodSourceName());
                return source;
            }
        }
        return null;
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
        int burgerX = 1150;   // under TableTop left column
        int hotdogX = 1500;  // under TableTop right column
        int pattyX = 2000;   // under Pan left column
        int sausageX = 2350; // under Pan right column
        int y = screenHeight - 100; // below appliances

        addFoodSource(burgerX, y, "BurgerBun");
        addFoodSource(hotdogX, y, "HotdogBun");
        addFoodSource(pattyX, y, "Patty");
        addFoodSource(sausageX, y, "Sausage");
    }
}
