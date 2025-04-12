package com.example.cooked_fever.appliances;

import android.graphics.*;
import android.view.MotionEvent;
import java.util.*;
import android.util.Log;

import com.example.cooked_fever.food.*;

public class ApplianceManager {

    private final List<Appliance> appliances  = new ArrayList<>();
    private int screenWidth;
    private int screenHeight;

    private final String LOG_TAG = this.getClass().getSimpleName();
    public ApplianceManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        initializeAppliances();
    }

    private void initializeAppliances() {
        appliances.clear();
        Log.d("Appliance Manager", "INitializeing");

        // Add Coca cola maker
        appliances.add(new CocaColaMaker(200, screenHeight - 300));

        // Add 6 TableTops: 2 columns x 3 rows
        int plateWidth = 300;
        int plateHeight = 200;
        int baseY = screenHeight - 500;
        int leftX = 900;
        int rightX = leftX + plateWidth;
        int rowGap = 200;

        for (int i = 0; i < 6; i++) {
            int row = i % 3;
            int x = i < 3 ? leftX : rightX;
            int y = baseY - row * rowGap;
            String acceptedFood = (i < 3) ? "Burger" : "Hotdog";
            appliances.add(new TableTop(x, y, plateWidth, plateHeight, i, acceptedFood));
        }

        // Add 6 Pans: 2 columns x 3 rows
        int panWidth = 300;
        int panHeight = 200;
        leftX = 1700;
        rightX = leftX + panWidth;
        for (int i = 0; i < 6; i++) {
            int row = i % 3;
            int x = i < 3 ? leftX : rightX;
            int y = screenHeight - 500 - row * 200;
            String type = (i < 3) ? "Patty" : "Sausage";
            appliances.add(new Pan(x, y, panWidth, panHeight, i, type));
        }

        Log.d("Appliance Manager", "222");
    }

    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        initializeAppliances(); // Rebuild with correct dimensions
    }

    public void update() {
        for (Appliance appliance : appliances) {
            appliance.update();
        }
    }

    public void draw(Canvas canvas) {
        for (Appliance appliance : appliances) {
            appliance.draw(canvas);
        }
    }

    public boolean handleTouch(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        for (Appliance appliance : appliances) {
            if (appliance.onClick(x, y)) {
                return true;
            }
        }
        return false;
    }

    // GET METHOD
    public List<Appliance> getAppliances() {
        return appliances;
    }

    // Assign Food Item to appliance
    public void assign(FoodItem foodItem) {

        // Get food name
        String foodItemName = foodItem.getName();

        for (Appliance appliance : appliances) {
            Log.d("AppliManager", "aap" + appliance);

            // Burger -> Tabletop
            if (foodItemName.equals("Burger")) {
                if (appliance instanceof TableTop) {
                    TableTop tableTop = (TableTop) appliance;   // Tabletop
                    if (tableTop.accepts(foodItemName) && tableTop.isEmpty()) {
                        tableTop.placeFood(foodItem);
                        Log.d("Game", "Placed " + foodItemName + " on TableTop " + tableTop.getId());
                        break;
                    }
                }
            }

            //
        }
    }
}