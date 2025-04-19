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
            Log.d("CustomerServe", "Checking: " + order.getItemName() + " vs " + itemName);
            if (order.getItemName().equals(itemName) && !order.isPrepared()) {
                Log.d("CustomerServe", "Accepted: " + itemName);
                order.prepare();
                return true;
            }
        }

        Log.d("CustomerServe", "Rejected: " + itemName);
        return false;
    }

    public Boolean isServed() {
        boolean allPrepared = true;
        for (FoodOrder order : orderList) {
            if (!order.isPrepared()) {
                allPrepared = false;
                return allPrepared;
            }
        }
        return allPrepared;
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
    public boolean isCustomerHitbox(int touchX, int touchY) {
        float spriteScale = 1.2f;
        int spriteWidth = (int) (sprite.getWidth() * spriteScale);
        int spriteHeight = (int) (sprite.getHeight() * spriteScale);

        // Bounding box around the sprite
        Rect spriteBounds = new Rect(
                (int)(x - spriteWidth / 2f),
                (int)(y - spriteHeight / 2f),
                (int)(x + spriteWidth / 2f),
                (int)(y + spriteHeight / 2f)
        );

        // Bounding box around the bubble (fixed values same as drawOrderBubble)
        int bubbleWidth = 180;
        int bubbleHeight = 260;
        float bubbleX = x + 70;
        float bubbleY = y - 100;

        Rect bubbleBounds = new Rect(
                (int)(bubbleX),
                (int)(bubbleY),
                (int)(bubbleX + bubbleWidth),
                (int)(bubbleY + bubbleHeight)
        );

        return spriteBounds.contains(touchX, touchY) || bubbleBounds.contains(touchX, touchY);
    }

    // Draw Customer
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        Log.d("CustomerDraw", "Drawing at x=" + x + " | sprite width=" + sprite.getWidth());

        // Draw the sprite (scaled)
        if (sprite != null) {
            float scale = 1.2f;
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

        // Bubble size and position
        int bubbleWidth = 180;
        int bubbleHeight = 260;
        float bubbleX = x + 70;
        float bubbleY = y - 100;

        RectF bubbleRect = new RectF(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight);

        // Bubble background
        Paint bubblePaint = new Paint();
        bubblePaint.setColor(Color.rgb(255, 253, 200)); // light yellow
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAlpha(235);
        canvas.drawRoundRect(bubbleRect, 20, 20, bubblePaint);

        // Bubble border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);
        borderPaint.setColor(Color.DKGRAY);
        canvas.drawRoundRect(bubbleRect, 20, 20, borderPaint);

        // Icons: centered vertically
        int iconSize = (int)(80 * 0.8f);
        int spacing = 12;
        int totalHeight = orderList.size() * (iconSize + spacing) - spacing;
        int startY = (int)(bubbleY + (bubbleHeight - totalHeight) / 2f);

        for (int i = 0; i < orderList.size(); i++) {
            String itemName = orderList.get(i).getItemName();
            int iconX = (int)(bubbleX + (bubbleWidth - iconSize - 20) / 2f);  // more padding
            int iconY = startY + i * (iconSize + spacing);

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), getDrawableResourceId(itemName));
            if (icon != null) {
                Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, iconSize, iconSize, true);

                if (orderList.get(i).isPrepared()) {
                    Paint greyPaint = new Paint();
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0); // grayscale
                    greyPaint.setColorFilter(new ColorMatrixColorFilter(matrix));
                    greyPaint.setAlpha(120); // semi-transparent
                    canvas.drawBitmap(scaledIcon, iconX, iconY, greyPaint);
                } else {
                    canvas.drawBitmap(scaledIcon, iconX, iconY, null);
                }
            }
        }

        // Timer bar
        float barWidth = 14f;
        float barX = bubbleRect.right - barWidth - 8f;
        float barTop = bubbleRect.top + 10f;
        float barBottom = bubbleRect.bottom - 10f;

        float patiencePercent = Math.max(0, (float)patience / MAX_PATIENCE);
        float filledHeight = (barBottom - barTop) * patiencePercent;
        float filledTop = barBottom - filledHeight;

        // Background of timer bar
        Paint barBg = new Paint();
        barBg.setColor(Color.LTGRAY);
        canvas.drawRect(barX, barTop, barX + barWidth, barBottom, barBg);

        // Foreground of timer bar
        Paint barFill = new Paint();
        barFill.setColor(Color.rgb((int)((1 - patiencePercent) * 255), (int)(patiencePercent * 255), 0)); // green → red
        canvas.drawRect(barX, filledTop, barX + barWidth, barBottom, barFill);

        // Optional: 1px outline on the bar
        Paint outlinePaint = new Paint();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(1f);
        outlinePaint.setColor(Color.DKGRAY);
        canvas.drawRect(barX, barTop, barX + barWidth, barBottom, outlinePaint);
    }


    private int getDrawableResourceId(String name) {
        switch (name) {
            case "Burger":
                return R.drawable.burger_completed;
            case "Hotdog":
                return R.drawable.hotdog_completed;
            case "Cola":
                return R.drawable.cup_filled;
            case "Fries":
                return R.drawable.cooked_fries;

            default:
                return 0;
        }
    }
}
