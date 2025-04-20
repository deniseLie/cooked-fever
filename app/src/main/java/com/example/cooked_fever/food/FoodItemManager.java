package com.example.cooked_fever.food;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import java.util.*;

// Tracks existing food
// Creates food
public class FoodItemManager {
    private List<FoodItem> createdFoodItems = new ArrayList<>();
    private final Context context;

    public FoodItemManager(Context context) {
        this.context = context;
    }

    public FoodItem createFoodItem(float x, float y, String foodItemName) {
        FoodItem newFoodItem = new FoodItem(context, x, y, foodItemName);
        if (foodItemName.equals("Cola")) {
            newFoodItem.prepareFoodItem();
        }
        return newFoodItem;
    }

    public void addFoodItem(FoodItem foodItem) {
        synchronized(createdFoodItems) {
            createdFoodItems.add(foodItem);
        }
    }

    public void removeFoodItem (FoodItem foodItem) {
        synchronized(createdFoodItems) {
            createdFoodItems.remove(foodItem);

            // Clear references
            foodItem = null;
        }
    }

    // Combine two food items
    public Boolean combine(FoodItem foodItem1, FoodItem foodItem2) {
        String name1 = foodItem1.getFoodItemName();
        String name2 = foodItem2.getFoodItemName();
        boolean success = false;

        // Ensure meat are prepared and not burnt
        if (!foodItem1.getIsPrepared() && !foodItem1.getIsBadlyCooked()) {
            return success;
        }

        // Patty + Bun = Burger
        if (name1.equals("Patty") && name2.equals("BurgerBun")) {

            foodItem2.setFoodItemName("Burger");
            removeFoodItem(foodItem1); // Remove the bun or patty used
            success = true;

            // Sausage + Hotdog Bun = Hotdog
        } else if (name1.equals("Sausage") && name2.equals("HotdogBun")) {

            foodItem2.setFoodItemName("Hotdog");
            removeFoodItem(foodItem1); // Remove the bun or sausage used
            success = true;
        }

        return success;
    }

    // Handle Touch
    public FoodItem handleTouch(MotionEvent event) {
        synchronized(createdFoodItems) {
            float x = event.getX();
            float y = event.getY();
            for (FoodItem foodItem : createdFoodItems) {
                if (foodItem.onClick(x, y)) {  // Check if the click is within the bounds of the food item
                    return foodItem; // Return food item
                }
            }
            return null;
        }
    }

    // Handle Touch with exception
    public FoodItem findOtherItemAtTouch(MotionEvent event, FoodItem excludedItem) {
        synchronized(createdFoodItems) {
            float x = event.getX();
            float y = event.getY();

            for (FoodItem foodItem : createdFoodItems) {
                if (foodItem != excludedItem && foodItem.onClick(x, y)) {
                    return foodItem;
                }
            }
            return null;
        }
    }

    public void draw(Canvas canvas, Context context) {
        List<FoodItem> snapshot;
        synchronized (createdFoodItems) {
            snapshot = new ArrayList<>(createdFoodItems);
        }

        for (FoodItem item : snapshot) {
            item.draw(canvas, context);
        }
    }

    public void reset() {
        synchronized (createdFoodItems) {
            createdFoodItems.clear();
        }
    }
}
