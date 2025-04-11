package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Customer {
    float x, y;

    public Customer(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 50, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(36f);
        text.setAntiAlias(true);
        canvas.drawText("Customer", x - 60, y + 80, text);
    }
}
