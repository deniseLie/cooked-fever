package com.example.cooked_fever.customers;

//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
import android.util.Log;
import android.graphics.*;
import android.content.*;
import com.example.cooked_fever.R;

public class Coin {
    private String coinID;
    private int coinAmount;
    private final Rect hitbox;

    private float x, y;
    private float originalX, originalY;
    private boolean isDragged = false; // Track if the item is being dragged
    private boolean isDraggable = false; // Track whether item allow being dragged

    private final Paint paint = new Paint();
    private final Paint text = new Paint();

    public Coin (Context context, int customerID, float x, float y, int coinAmount) {
        hitbox = new Rect((int)x, (int)y, (int)x + 200, (int)y + 200);
        this.coinID =  "coin"+ customerID;
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
        this.coinAmount = coinAmount;

//        Log.d("Coin", "Created coins: " + this.coinAmount);

        // Load bitmap based on item name
        int resId = 0;
    }

    // Getter
    public String getCoinID() {return coinID;}
    public int getCoinAmount() {return coinAmount;}
    public Rect getHitbox() {return hitbox;}
    public float getX() {return x;}
    public float getY() {return y;}
    public float getOriginalX() {return originalX;}
    public float getOriginalY() {return originalY;}
    public Boolean getIsDragged() {return isDragged;}
    public Boolean getIsDraggable() {return isDraggable;}

    // Setter
    public void setCoinAmount(int amount) {this.coinAmount = amount;}
    public void setX(float x) {this.x = x;}
    public void setY(float y) {this.y = y;}
    public void setIsDragged(Boolean status) {this.isDragged = status;}

    public boolean onClick(float clickX, float clickY) {
        // Calculate the distance from the clicked point to the center of the circle (x, y)
        float dx = clickX - this.x; // x coordinate of the circle's center
        float dy = clickY - this.y; // y coordinate of the circle's center
        float distance = (float) Math.sqrt(dx * dx + dy * dy); // Euclidean distance

        // Check if the click is within the circle's radius
        return distance <= 50; // 50 is the radius of the circle
    }

    public void draw(Canvas canvas, Context context) {
//        int resId = R.drawable.coin_stack;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin_stack);

        if (bitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            canvas.drawBitmap(scaled, x - 50, y - 50, null);
        } else {
            // fallback if bitmap can't load
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(x, y, 50, paint);
        }

//        Paint text = new Paint();
//        text.setColor(Color.BLACK);
//        text.setTextSize(32f);
//        text.setAntiAlias(true);
//
//        canvas.drawText(this.coinID, x - 60, y + 80, text);
//        canvas.drawText("Amount: " + this.coinAmount, x - 60, y + 100, text);
    }


}
