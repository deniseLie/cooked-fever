package com.example.cooked_fever.customers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

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
        Coin newCoin = new Coin(context, customerID, x, 500, coinAmount);
        Log.d("CoinManager", "Created: coin" + customerID + " amt: " + coinAmount);
        return newCoin;
    }
    public void addCoin (Coin coin) {
        synchronized(uncollectedCoinList) {
            uncollectedCoinList.add(coin);
            Log.d("CoinManager" ,"coin added: " + coin.getCoinID() + " amt: " + coin.getCoinAmount());
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
    }
    public Coin handleTouch(MotionEvent event) {
        synchronized(uncollectedCoinList) {
            float x = event.getX();
            float y = event.getY();
            for (Coin coin : uncollectedCoinList) {
                if (coin.onClick(x, y)) {  // Check if the click is within the bounds of the food item
                    return coin; // Return food item
                }
            }
            return null;
        }
    }

    public void draw(Canvas canvas, Context context) {
        synchronized(uncollectedCoinList) {
            for (Coin coin : uncollectedCoinList) {
                coin.draw(canvas, context); // Draw each food item
            }
        }

        Paint fullBar = new Paint();
        fullBar.setColor((Color.RED));
        canvas.drawRect(700, 90 , 1700, 120, fullBar);

        // Draw patience bar
        Paint progressBar = new Paint();
        progressBar.setColor(Color.YELLOW);
        float barWidth = Math.max(0, (float) collectedCoins / 48) * 1000;
        barWidth = barWidth >= 1000 ? 1000 : barWidth;
        canvas.drawRect(700, 90 , 700 + barWidth, 120, progressBar);


    }

    public int getCollectedCoins() {
        return collectedCoins;
    }

    public void reset() {
        collectedCoins = 0;
        uncollectedCoinList.clear();
    }

}
