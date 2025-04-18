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


        // NEW: draw order bubble
        drawOrderBubble(canvas);
    }
    private void drawOrderBubble(Canvas canvas) {
        if (orderList.isEmpty()) return;

        // Fixed bubble size
        int bubbleWidth = 180;
        int bubbleHeight = 200;
        float bubbleX = x + 70;
        float bubbleY = y - 100;

        // Bubble background
        Paint bubblePaint = new Paint();
        bubblePaint.setColor(Color.WHITE);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAlpha(230);
        canvas.drawRoundRect(new RectF(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight), 20, 20, bubblePaint);

        // Icon layout (vertical)
        int iconSize = 50;
        int spacing = 12;
        int totalHeight = orderList.size() * iconSize + (orderList.size() - 1) * spacing;
        float iconStartY = bubbleY + (bubbleHeight - totalHeight) / 2f;
        float iconX = bubbleX + 30;  // moved more towards center

        for (int i = 0; i < orderList.size(); i++) {
            int resId = getDrawableResourceId(orderList.get(i).getItemName());
            if (resId != 0) {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resId);
                Bitmap scaled = Bitmap.createScaledBitmap(icon, iconSize, iconSize, true);
                float iconY = iconStartY + i * (iconSize + spacing);
                canvas.drawBitmap(scaled, iconX, iconY, null);
            }
        }

        // Timer bar (right side, padded more inward, wider)
        float barWidth = 16;
        float barX = bubbleX + bubbleWidth - barWidth - 15;  // padded inward
        float barY = bubbleY + 20;
        float barHeight = bubbleHeight - 40;

        // Border around the timer bar
        Paint outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(1);
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, outlinePaint);

        // Fill level (gradient from green → red)
        float fillRatio = Math.max(0f, patience / (float) MAX_PATIENCE);
        float filledHeight = barHeight * fillRatio;

        int red, green;
        if (fillRatio > 0.5f) {
            float t = (fillRatio - 0.5f) * 2;
            red = (int)(255 * (1 - t));
            green = 255;
        } else {
            float t = fillRatio * 2;
            red = 255;
            green = (int)(255 * t);
        }

        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.rgb(red, green, 0));
        canvas.drawRect(barX, barY + (barHeight - filledHeight), barX + barWidth, barY + barHeight, fillPaint);
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
