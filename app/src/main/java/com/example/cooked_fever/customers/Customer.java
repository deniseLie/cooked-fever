package com.example.cooked_fever.customers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.cooked_fever.food.FoodOrder;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    // Public Variables
    public int id;
    public int patience; // Decreases over time
    public volatile boolean isServed = false;

    // Private Variables
    private float x, y;
    private List<FoodOrder> orderList;
    private long arrivalTime; // To manage patience
    private final int MAX_PATIENCE = 30000; // 10 seconds in milliseconds
    private int slotIndex = -1;
    private final Rect hitbox;
    private int reward;

    // Log
    private final String LOG_TAG = this.getClass().getSimpleName();

    public Customer(float x, float y, List<String> foodItems) {
        this.hitbox = new Rect((int)x-12, (int)y-12, (int)x + 25, (int)y + 25);
        this.x = x;
        this.y = y;
        this.orderList = new ArrayList<>();
        for (String item : foodItems) {
            orderList.add(new FoodOrder(item));
        }
        this.patience = MAX_PATIENCE;
        this.reward = 2 * orderList.size();
        this.arrivalTime = System.currentTimeMillis();
    }

    public int getReward(){
        return reward;
    }

    public void update() {

        // Decrease patience based on time
        long now = System.currentTimeMillis();
        patience = MAX_PATIENCE - (int) (now - arrivalTime);

        // Check if all orders are prepared
        boolean allPrepared = true;
        for (FoodOrder order : orderList) {
            if (!order.isPrepared()) {
                allPrepared = false;
                break;
            }
        }

        if (allPrepared) {
            isServed = true;
        } else if (patience <= 0) {
            // if not served in time, customer leaves
            leave();
        }
    }

    public void serveItem(String itemName) {
        if (isServed) return;

        for (FoodOrder order : orderList) {
            if (order.getItemName().equals(itemName) && !order.isPrepared()) {
                Log.d("Customer", "serving " + itemName);
                order.prepare();
                break;
            }
        }
    }

    public void setSlotIndex(int index) {
        this.slotIndex = index;
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }

    // Leave if served or no more patience
    public boolean shouldLeave() {
        return isServed || patience <= 0;
    }

    private void leave() {
        // Remove from queue or trigger a "leave" animation/state
        isServed = true; // Still mark as served to remove from screen, but with penalty
    }
    public Rect getHitbox() {
        return hitbox;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public boolean isCustomerHitbox(int x, int y) {
        Log.d("TableTop", "Table " + x);
        if (hitbox.contains(x, y)) {
            return true;
        }
        return false;
    }

    // Draw Customer
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText("Customer", x - 60, y + 80, text);

        // Draw patience bar
        Paint patienceBar = new Paint();
        patienceBar.setColor(Color.RED);
        float barWidth = Math.max(0, (float) patience / MAX_PATIENCE) * 100;
        canvas.drawRect(x - 50, y - 70, x - 50 + barWidth, y - 60, patienceBar);

        // Draw orders
        for (int i = 0; i < orderList.size(); i++) {
            FoodOrder o = orderList.get(i);
            text.setColor(o.isPrepared() ? Color.GRAY : Color.BLACK);
            canvas.drawText(o.getItemName(), x - 40, y + 120 + (i * 30), text);
        }
    }
}
