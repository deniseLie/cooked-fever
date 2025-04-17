package com.example.cooked_fever.customers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.example.cooked_fever.R;
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

    private Bitmap sprite;

    private final Context context;

    // Log
    private final String LOG_TAG = this.getClass().getSimpleName();

    public Customer(Context context, float x, float y, List<String> foodItems) {
        this.context = context;
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
        this.sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_idle);
        sprite = Bitmap.createScaledBitmap(
                sprite,
                (int)(sprite.getWidth() * 0.8f),  // 60% width
                (int)(sprite.getHeight() * 0.8f), // 60% height
                true
        );
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

    public Boolean serveItem(String itemName) {
        if (isServed) return false;

        for (FoodOrder order : orderList) {
            if (order.getItemName().equals(itemName) && !order.isPrepared()) {
                Log.d("Customer", "serving " + itemName);
                order.prepare();
                return true;
            }
        }
        return false;
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
        Log.d("CustomerDraw", "Drawing at x=" + x + " | sprite width=" + sprite.getWidth());

        // Draw the sprite (scaled)
        if (sprite != null) {
            float scale = 1.5f;
            int newWidth = (int) (sprite.getWidth() * scale);
            int newHeight = (int) (sprite.getHeight() * scale);
            Bitmap scaled = Bitmap.createScaledBitmap(sprite, newWidth, newHeight, true);

            float spriteX = x - newWidth / 2f;
            float spriteY = y - newHeight / 2f;
            canvas.drawBitmap(scaled, spriteX, spriteY, null);
        }

        // Draw the patience bar
        Paint patienceBar = new Paint();
        patienceBar.setColor(Color.RED);
        float barWidth = Math.max(0, (float) patience / MAX_PATIENCE) * 100;
        canvas.drawRect(x - 50, y - 70, x - 50 + barWidth, y - 60, patienceBar);

        // ✨ NEW: draw order bubble
        drawOrderBubble(canvas);
    }
    private void drawOrderBubble(Canvas canvas) {
        if (orderList.isEmpty()) return;

        int bubbleWidth = 150;
        int bubbleHeight = 60 + (orderList.size() * 70); // expand based on order count
        float bubbleX = x + 70; // right of sprite
        float bubbleY = y - 100;

        Paint bubblePaint = new Paint();
        bubblePaint.setColor(Color.WHITE);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAlpha(220);

        canvas.drawRoundRect(new RectF(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight), 20, 20, bubblePaint);

        for (int i = 0; i < orderList.size(); i++) {
            String name = orderList.get(i).getItemName();
            int resId = getDrawableResourceId(name);
            if (resId != 0) {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resId);
                Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, 64, 64, true); // prevent explosion

                int iconX = (int)(bubbleX + 15); // left padding inside bubble
                int iconY = (int)(bubbleY + 20 + i * 70); // stack vertically

                canvas.drawBitmap(scaledIcon, iconX, iconY, null);
            }
        }
    }
    private int getDrawableResourceId(String name) {
        switch (name) {
            case "Burger":
                return R.drawable.burger_completed;
            case "Hotdog":
                return R.drawable.hotdog_completed;
            case "Cola":
                return R.drawable.cola_machine_cup_filled;
            default:
                return 0;
        }
    }
}
