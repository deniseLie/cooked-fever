package com.example.cooked_fever.appliances;

import android.graphics.*;
import android.view.MotionEvent;
import java.util.*;
import android.util.Log;
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

        // ADDS
        appliances.add(new CocaColaMaker(200, screenHeight - 300));
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
        float x = event.getX();
        float y = event.getY();

        for (Appliance appliance : appliances) {
            if (appliance.onClick(x, y)) {
                return true;
            }
        }
        return false;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }
}