package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import java.util.*;
import android.util.Log;

import com.example.cooked_fever.food.*;

public class ApplianceManager {

    private final List<Appliance> appliances  = new ArrayList<>();
    private int screenWidth;
    private int screenHeight;
    private final Context context;

    private final String LOG_TAG = this.getClass().getSimpleName();
    public ApplianceManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        initializeAppliances();
    }

    private void initializeAppliances() {
        appliances.clear();
        Log.d("Appliance Manager", "INitializeing");

        // Add Coca cola maker
        appliances.add(new CocaColaMaker(context, 200, screenHeight - 300));
        appliances.add(new TrashBin(550, screenHeight - 250));

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
            String acceptedFood = (i < 3) ? "BurgerBun" : "HotdogBun";
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
            appliances.add(new Pan(context, x, y, panWidth, panHeight, i, type));
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
//            if (appliance instanceof TableTop) continue;
            appliance.draw(canvas);
        }
    }

    public Appliance handleTouch(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        for (Appliance appliance : appliances) {
            if (appliance.onClick(x, y)) {
                return appliance;
            }
        }
        return null;
    }
    public Appliance getApplianceAtCoord(int x, int y) {
        for (Appliance appliance : appliances) {
            if (appliance.onClick(x, y)) {
                return appliance;
            }
        }
        return null;
    }
    public Boolean isTrash(Appliance appliance) {
        return appliance instanceof TrashBin;
    }
    public void doTrash(Appliance appliance) {
        if (appliance instanceof Pan) {
            Pan pan = (Pan) appliance;
            pan.takeFood();
        } else if (appliance instanceof TableTop) {
            TableTop tableTop = (TableTop) appliance;
            tableTop.takeFood();
        }
    }

    public void reset() {
        for (Appliance appliance : appliances) {
            appliance.reset(); // ✅ Reset each appliance
        }
    }

    // GET METHOD
    public List<Appliance> getAppliances() {
        return appliances;
    }

    public Boolean checkColaMachine() {
        for (Appliance appliance : appliances) {
            if (appliance instanceof CocaColaMaker) {

                // Serve when drink is ready
                Boolean status = (((CocaColaMaker) appliance).hasDrinkReady());
                if (status) {
                    ((CocaColaMaker) appliance).serving();
                }
                Log.d("ApplianceManager" ,"checkColaMachine: " + status);
                return status;
            }
        }
        return false;
    }
    public void pauseColaMachine() {
        for (Appliance appliance : appliances) {
            if (appliance instanceof CocaColaMaker) {
                ((CocaColaMaker) appliance).serving();
                return;
            }
        }
    }
    public void resumeColaMachine() {
        for (Appliance appliance : appliances) {
            if (appliance instanceof CocaColaMaker) {
                ((CocaColaMaker) appliance).servingComplete();
                return;
            }
        }
    }

    public FoodItem getTableItem() {
        for (Appliance appliance : appliances) {
            if (appliance instanceof TableTop) {
                return ((TableTop) appliance).peekFood();
            }
        }
        return null;
    }

    public Boolean hasTableSpace(FoodItem foodItem) {
        if (!(foodItem.getFoodItemName().equals("BurgerBun") ||
                foodItem.getFoodItemName().equals("HotdogBun"))) {
            Log.d("ApplianceManager" ,"Rejected: " + foodItem.getFoodItemName());
            return false;
        }
        int counter = 0;
        for (Appliance appliance : appliances) {
            if (appliance instanceof TableTop) {
                TableTop tableTop = (TableTop) appliance;
                if (tableTop.accepts(foodItem.getFoodItemName()) && !tableTop.isEmpty()) counter++;
            }
            if (counter == 3) {
                Log.d("ApplianceManager" ,"Table full");
                return false;
            }
        }

        return true;
    }
    public Boolean hasPanSpace(FoodItem foodItem) {
        if (!(foodItem.getFoodItemName().equals("Sausage") ||
                foodItem.getFoodItemName().equals("Patty"))) return false;
        int counter = 0;
        for (Appliance appliance : appliances) {
            if (appliance instanceof Pan) {
                Pan pan = (Pan) appliance;
                if (pan.accepts(foodItem.getFoodItemName()) && !pan.isEmpty()) counter++;
            }
            if (counter == 3) {
                Log.d("ApplianceManager" ,"Pan full");
                return false;
            }
        }

        return true;
    }

    // Assign Food Item to appliance
    public void assign(FoodItem foodItem) {

        // Get food name
        String foodItemName = foodItem.getFoodItemName();

        for (Appliance appliance : appliances) {
            Log.d("AppliManager", "aap" + appliance);

            // Burger -> Tabletop
            if (foodItemName.equals("BurgerBun")) {
                if (appliance instanceof TableTop) {
                    TableTop tableTop = (TableTop) appliance;   // Tabletop
                    if (tableTop.accepts(foodItemName) && tableTop.isEmpty()) {
                        tableTop.placeFood(foodItem, tableTop.getX(), tableTop.getY());
                        Log.d("Game", "Placed " + foodItemName + " on TableTop " + tableTop.getId());
                        break;
                    }
                }
            }

            // Hotdog -> Tabletop
            if (foodItemName.equals("HotdogBun")) {
                if (appliance instanceof TableTop) {
                    TableTop tableTop = (TableTop) appliance;   // Tabletop
                    if (tableTop.accepts(foodItemName) && tableTop.isEmpty()) {
                        tableTop.placeFood(foodItem, tableTop.getX(), tableTop.getY());
                        Log.d("Game", "Placed " + foodItemName + " on TableTop " + tableTop.getId());
                        break;
                    }
                }
            }

            // Patty -> Pan
            if (foodItemName.equals("Patty")) {
                if (appliance instanceof Pan) {
                    Pan pan = (Pan) appliance;   // Tabletop
                    if (pan.accepts(foodItemName) && pan.isEmpty()) {
                        pan.placeFood(foodItem, pan.getX(), pan.getY());
                        Log.d("Game", "Placed " + foodItemName + " on Pan " + pan.getId());
                        break;
                    }
                }
            }

            // Sausage -> Pan
            if (foodItemName.equals("Sausage")) {
                if (appliance instanceof Pan) {
                    Pan pan = (Pan) appliance;   // Tabletop
                    if (pan.accepts(foodItemName) && pan.isEmpty()) {
                        pan.placeFood(foodItem, pan.getX(), pan.getY());
                        Log.d("Game", "Placed " + foodItemName + " on Pan " + pan.getId());
                        break;
                    }
                }
            }
        }
    }
}