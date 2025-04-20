package com.example.cooked_fever.customers;

import android.graphics.*;
import android.content.*;
import com.example.cooked_fever.R;

public class Coin {
    private String coinID;
    private int coinAmount;
    private final Rect hitbox;
    private float x, y;
    private float originalX, originalY;
    private final Paint paint = new Paint();

    public Coin (Context context, int customerID, float x, float y, int coinAmount) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
        this.coinID =  "coin"+ customerID;
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
        this.coinAmount = coinAmount;
    }

    // Getter
    public int getCoinAmount() {return coinAmount;}
    public Rect getHitbox() {return hitbox;}
    public float getX() {return x;}
    public float getY() {return y;}

    // Setter
    public void setX(float x) {this.x = x;}
    public void setY(float y) {this.y = y;}

    public boolean onClick(float clickX, float clickY) {
        // Calculate the distance from the clicked point to the center of the circle (x, y)
        float dx = clickX - this.x; // x coordinate of the circle's center
        float dy = clickY - this.y; // y coordinate of the circle's center
        float distance = (float) Math.sqrt(dx * dx + dy * dy); // Euclidean distance

        // Check if the click is within the circle's radius
        return distance <= 50; // 50 is the radius of the circle
    }

    public void draw(Canvas canvas, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin_stack);

        if (bitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            canvas.drawBitmap(scaled, x - 50, y - 50, null);
        } else {
            // fallback if bitmap can't load
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(x, y, 50, paint);
        }
    }
}
