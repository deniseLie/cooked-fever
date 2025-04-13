package com.example.cooked_fever.food;

import android.graphics.*;
import java.util.*;
import com.example.cooked_fever.food.*;

// Tracks existing food
// Creates food
public class FoodItemManager {
    private List<FoodItem> createdFoodItems = new ArrayList<>();

    public FoodItemManager() {

    }

    public FoodItem createFoodItem(float x, float y, String foodItemName) {
        FoodItem newFoodItem = new FoodItem(x, y, foodItemName);
        if (foodItemName.equals("Cola")) {
            newFoodItem.prepareFoodItem();
        }
        return newFoodItem;
    }

    public void draw(Canvas canvas) {
        for (FoodItem foodItem : createdFoodItems) {
            foodItem.draw(canvas);
        }
    }

    public void addFoodItem(FoodItem foodItem) {
        createdFoodItems.add(foodItem);
    }

    public void removeFoodItem (FoodItem foodItem) {
        createdFoodItems.remove(foodItem);
    }
}
