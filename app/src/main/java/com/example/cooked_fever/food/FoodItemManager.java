package com.example.cooked_fever.food;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;

import java.util.*;

import com.example.cooked_fever.appliances.Appliance;
import com.example.cooked_fever.food.*;

// Tracks existing food
// Creates food
public class FoodItemManager {
    private List<FoodItem> createdFoodItems = new ArrayList<>();
//    private FoodItem draggedFoodItem = null;  // Track which food item is being dragged
//    private float offsetX, offsetY;  // Track where the user clicked on the food item to ensure smooth dragging

    public FoodItemManager() {

    }

    public FoodItem createFoodItem(float x, float y, String foodItemName) {
        FoodItem newFoodItem = new FoodItem(x, y, foodItemName);
        if (foodItemName.equals("Cola")) {
            newFoodItem.prepareFoodItem();
        }
        Log.d("foodItemCreated" ,"foodItem: " + foodItemName);
        return newFoodItem;
    }

    public void draw(Canvas canvas) {
        for (FoodItem foodItem : createdFoodItems) {
            foodItem.draw(canvas);
        }
    }

    public void addFoodItem(FoodItem foodItem) {
        createdFoodItems.add(foodItem);
        Log.d("foodItemList" ,"foodItem added: " + foodItem.getFoodItemName());
    }

    public void removeFoodItem (FoodItem foodItem) {
        createdFoodItems.remove(foodItem);
    }

    public FoodItem handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (FoodItem foodItem : createdFoodItems) {
            if (foodItem.onClick(x, y)) {  // Check if the click is within the bounds of the food item
//                draggedFoodItem = foodItem;  // Set the dragged food item
//                Log.d("startdragItem" ,"item picked up: " + draggedFoodItem.getFoodItemName());
//                offsetX = x - foodItem.getX();  // Calculate offset to drag smoothly
//                offsetY = y - foodItem.getY();
//                draggedFoodItem.startDrag();
                return foodItem; // Stop checking other food items once we've found the one being dragged
            }
        }
        return null;
    }
//    public boolean handleTouch(MotionEvent event) {
//        int x = (int)event.getX();
//        int y = (int)event.getY();
//
//        for (Appliance appliance : appliances) {
//            if (appliance.onClick(x, y)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
