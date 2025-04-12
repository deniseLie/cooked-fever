package com.example.cooked_fever;

import android.graphics.*;


public class FoodSource {
    private String foodSourceName;
//    private Boolean isPrepared;
//    private Boolean isBadlyCooked;
    private float x, y;

    public FoodSource (float x, float y, String foodSourceName) {
        this.x = x;
        this.y = y;
        this.foodSourceName = foodSourceName;
    }

    // Getter
    public String getFoodSourceName () {return this.foodSourceName;}
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(32f);
        text.setAntiAlias(true);

        canvas.drawText(this.foodSourceName, x - 60, y + 80, text);


//        // Draw patience bar
//        Paint patienceBar = new Paint();
//        patienceBar.setColor(Color.RED);
//        float barWidth = Math.max(0, (float) patience / MAX_PATIENCE) * 100;
//        canvas.drawRect(x - 50, y - 70, x - 50 + barWidth, y - 60, patienceBar);
//
//        // Draw orders
//        for (int i = 0; i < orderList.size(); i++) {
//            FoodOrder o = orderList.get(i);
//            text.setColor(o.isPrepared() ? Color.GRAY : Color.BLACK);
//            canvas.drawText(o.getItemName(), x - 40, y + 120 + (i * 30), text);
//        }
    }
}
