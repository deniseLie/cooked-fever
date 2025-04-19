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

    public void draw(Canvas canvas) {
        for (FoodSource source : sources) {
            source.draw(canvas);
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

    public void reset() {
        for (FoodSource source : sources) {
            source.reset();
        }
    }
}
