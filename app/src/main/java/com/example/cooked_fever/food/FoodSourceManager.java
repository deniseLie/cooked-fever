package com.example.cooked_fever.food;

import android.graphics.*;
import java.util.*;

public class FoodSourceManager {

    private final List<FoodSource> sources = new ArrayList<>();

    public FoodSourceManager(int screenWidth, int screenHeight) {
        addFoodSource(950, screenHeight - 650, "Burger");
        addFoodSource(1900, screenHeight - 650, "Patty");
    }

    public void addFoodSource(int x, int y, String name) {
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
                return source;
            }
        }
        return null;
    }

    public List<FoodSource> getSources() {
        return sources;
    }
}