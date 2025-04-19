package com.example.cooked_fever.appliances;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.util.Log;
import android.content.*;
import java.util.*;

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
        synchronized (appliances) {
            appliances.clear();
            Log.d("ApplianceManager", "Initializing");

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

            // Add 2 Pans: 1 column x 2 rows
            int warmWidth = 200;
            int warmHeight = 100;
            appliances.add(new FoodWarmer(context, 1750,420, warmWidth, warmHeight, 0));
            appliances.add(new FoodWarmer(context, 1950, 420, warmWidth, warmHeight, 1));

            int fryMakerWidth = 200;
            int fryMakerHeight = 100;
            appliances.add(new FryMaker(context, 300,500, fryMakerWidth, fryMakerHeight, 0));

            int fryHolderWidth = 200;
            int fryHolderHeight = 100;
            appliances.add(new FryHolder(context, 300,700, fryHolderWidth, fryHolderHeight, 0));
            appliances.add(new FryHolder(context, 300, 800, fryHolderWidth, fryHolderHeight, 1));
            Log.d("ApplianceManager", "Finished initializing appliances");
        }
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
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances); // create a safe copy
        }

        for (Appliance appliance : snapshot) {
            appliance.draw(canvas);
        }
    }

    public void reset() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            appliance.reset();
        }
    }

    public Appliance handleTouch(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance.onClick(x, y)) {
                return appliance;
            }
        }
        return null;
    }
    public Appliance getApplianceAtCoord(int x, int y) {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance.onClick(x, y)) {

                return appliance;
            }
        }
        return null;
    }

    // TrashBin
    public Boolean isTrash(Appliance appliance) {
        return appliance instanceof TrashBin;
    }
    public void doTrash(Appliance appliance) {
        appliance.takeFood();
        Log.d("ApplianceManager", "Trash from: " + appliance);
    }

    // GET METHOD
    public List<Appliance> getAppliances() {
        return appliances;
    }

    // Cola stuff
    public Boolean checkColaMachine() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof CocaColaMaker) {
                boolean status = ((CocaColaMaker) appliance).hasDrinkReady();
                return status;
            }
        }
        return false;
    }
    public void pauseColaMachine() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof CocaColaMaker) {
                ((CocaColaMaker) appliance).serving();
                return;
            }
        }
    }
    public void resumeColaMachine() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof CocaColaMaker) {
                ((CocaColaMaker) appliance).servingComplete();
                return;
            }
        }
    }

    public float getColaMachineX() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof CocaColaMaker) {
                CocaColaMaker colaMachine = (CocaColaMaker) appliance;
                return colaMachine.getHitbox().exactCenterX();
            }
        }
        return 0;
    }
    public float getColaMachineY() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof CocaColaMaker) {
                CocaColaMaker colaMachine = (CocaColaMaker) appliance;
                return colaMachine.getHitbox().exactCenterY();
            }
        }
        return 0;
    }

    public FoodItem getTableItem() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof TableTop) {
                return ((TableTop) appliance).peekFood();
            }
        }
        return null;
    }

    // Checking space
    public Boolean hasTableSpace(FoodItem foodItem) {
        if (!(foodItem.getFoodItemName().equals("BurgerBun") ||
                foodItem.getFoodItemName().equals("HotdogBun"))) {
            Log.d("ApplianceManager", "Rejected: " + foodItem.getFoodItemName());
            return false;
        }

        int counter = 0;

        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof TableTop) {
                TableTop tableTop = (TableTop) appliance;
                if (tableTop.accepts(foodItem.getFoodItemName()) && !tableTop.isEmpty()) {
                    counter++;
                }
            }
            if (counter == 3) {
                Log.d("ApplianceManager", "Table full");
                return false;
            }
        }

        return true;
    }
    public Boolean hasPanSpace(FoodItem foodItem) {
        if (!(foodItem.getFoodItemName().equals("Sausage") ||
                foodItem.getFoodItemName().equals("Patty"))) return false;

        int counter = 0;

        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof Pan) {
                Pan pan = (Pan) appliance;
                if (pan.accepts(foodItem.getFoodItemName()) && !pan.isEmpty()) {
                    counter++;
                }
            }
            if (counter == 3) {
                Log.d("ApplianceManager", "Pan full");
                return false;
            }
        }

        return true;
    }

    // Food Warmer
    public Boolean keepWarm(FoodItem foodItem, FoodWarmer foodWarmer) {
        return foodWarmer.placeFood(foodItem);
    }

    // Fry stuff
    public Boolean checkFryMaker(FryMaker fryMaker) {
        return fryMaker.isReady();
    }
    public Boolean isEmptyFryHolder() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }
        int counter = 0;
        for (Appliance appliance : snapshot) {
            if (appliance instanceof FryHolder) {
                FryHolder fryHolder = (FryHolder) appliance;
                Log.d("ApplianceManager", "status: " + fryHolder.isEmpty());
                if (fryHolder.isEmpty()) {
                    counter++;
                }
            }
        }
        return counter > 0 ? true : false;
    }

    public void startFrying(Appliance appliance) {
        if (appliance instanceof FryMaker) {
            FryMaker fryMaker = (FryMaker) appliance;
            fryMaker.makeFries();
        }
    }
    public void stopFrying (Appliance appliance) {
        if (appliance instanceof FryMaker) {
            FryMaker fryMaker = (FryMaker) appliance;
            fryMaker.reset();
        }
    }
    public FryMaker getFryMaker() {
        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {
            if (appliance instanceof FryMaker) {
                FryMaker fryMaker = (FryMaker) appliance;
                return fryMaker;
            }
        }
        return null;
    }

    // Auto assign Food Item to appliance
    public void assign(FoodItem foodItem) {
        String foodItemName = foodItem.getFoodItemName();

        List<Appliance> snapshot;
        synchronized (appliances) {
            snapshot = new ArrayList<>(appliances);
        }

        for (Appliance appliance : snapshot) {

            // Burger -> Tabletop
            if (foodItemName.equals("BurgerBun") || foodItemName.equals("HotdogBun")) {
                if (appliance instanceof TableTop) {
                    TableTop tableTop = (TableTop) appliance;
                    if (tableTop.accepts(foodItemName) && tableTop.isEmpty()) {
                        tableTop.placeFood(foodItem, tableTop.getX(), tableTop.getY());
                        Log.d("ApplianceManager", "Placed " + foodItemName + " on TableTop " + tableTop.getId());
                        break;
                    }
                }
            }

            // Patty or Sausage -> Pan
            if (foodItemName.equals("Patty") || foodItemName.equals("Sausage")) {
                if (appliance instanceof Pan) {
                    Pan pan = (Pan) appliance;
                    if (pan.accepts(foodItemName) && pan.isEmpty()) {
                        pan.placeFood(foodItem, pan.getX(), pan.getY());
                        Log.d("ApplianceManager", "Placed " + foodItemName + " on Pan " + pan.getId());
                        break;
                    }
                }
            }

            if (foodItemName.equals("Fries")) {
                if (appliance instanceof FryHolder) {
                    FryHolder fryHolder = (FryHolder) appliance;
                    if (fryHolder.isEmpty()) {
                        fryHolder.placeFood(foodItem, fryHolder.getX(), fryHolder.getY());
                        break;
                    }

                }
            }
        }
    }
}