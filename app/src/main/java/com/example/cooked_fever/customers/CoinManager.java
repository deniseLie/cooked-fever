package com.example.cooked_fever.customers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.example.cooked_fever.R;
import com.example.cooked_fever.utils.SoundUtils;
import com.example.cooked_fever.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class CoinManager {
    private List<Coin> uncollectedCoinList = new ArrayList<>();
    private int collectedCoins;
    private final Context context;

    public CoinManager(Context context) {
        this.context = context;
        collectedCoins = 0;
    }

    public Coin addNewCoins(Context context, int customerID, float x, int coinAmount) {
        Coin newCoin = new Coin(context, customerID, x, 400, coinAmount);
//        Log.d("CoinManager", "Created: coin" + customerID + " amt: " + coinAmount);
        return newCoin;
    }
    public void addCoin (Coin coin) {
        synchronized(uncollectedCoinList) {
            uncollectedCoinList.add(coin);
//            Log.d("CoinManager" ,"coin added: " + coin.getCoinID() + " amt: " + coin.getCoinAmount());
        }
    }
    public void removeCoin (Coin coin) {
        synchronized(uncollectedCoinList) {
            uncollectedCoinList.remove(coin);

            // Clear references
            coin = null;
        }
    }
    public void collectCoin(int amount) {
        collectedCoins += amount;
        SoundUtils.playCoin();
    }
    public Coin handleTouch(MotionEvent event) {
        synchronized(uncollectedCoinList) {
            float x = event.getX();
            float y = event.getY();
            for (Coin coin : uncollectedCoinList) {
                if (coin.onClick(x, y)) {  // Check if the click is within the bounds of the food item

//                    SoundUtils.playCoin();
                    return coin; // Return food item
                }
            }
            return null;
        }
    }
    public void deductCoin(int amount) {
        collectedCoins = Math.max(0, collectedCoins - amount);
    }

    public void draw(Canvas canvas, Context context) {
        synchronized(uncollectedCoinList) {
            for (Coin coin : uncollectedCoinList) {
                coin.draw(canvas, context); // Draw each food item
            }
        }
        float barWidth = 30;
        float barLength = 1000;
        float barLeft = 800;
        float barTop = 90;

        // Behind the Bar
        Paint barBackground = new Paint();
        barBackground.setColor((Color.YELLOW));
        canvas.drawRect(barLeft - 50, barTop - 7, barLeft+barLength+20, barTop+37, barBackground);

        Paint fullBar = new Paint();
        fullBar.setColor((Color.BLUE));
        canvas.drawRect(barLeft, barTop , barLeft+barLength, barTop+barWidth, fullBar);
        // Draw Coin stack
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin_stack);
        if (bitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
            canvas.drawBitmap(scaled, barLeft-75, barTop-35, null);
        } else {
            // fallback if bitmap can't load
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(barLeft, barTop, 50, paint);
        }
        // Draw progress bar
        Paint progressBar = new Paint();
        progressBar.setColor(Color.GREEN);
        float barProgressLength = Math.max(0, (float) collectedCoins / 48) * 1000;
        barProgressLength = barProgressLength >= 1000 ? 1000 : barProgressLength;
        canvas.drawRect(barLeft, barTop , barLeft + barProgressLength, barTop+30, progressBar);

        Paint text = new Paint();
        text.setColor(Color.YELLOW);
        text.setTextSize(32f);
        text.setAntiAlias(true);
        canvas.drawText("" + collectedCoins, barLeft+50, barTop+25, text);
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }

    public void reset() {
        collectedCoins = 0;
        uncollectedCoinList.clear();
    }

//    public void reset() {
//        uncollectedCoinList.clear(); // or however you're tracking floating coins
//    }

}
