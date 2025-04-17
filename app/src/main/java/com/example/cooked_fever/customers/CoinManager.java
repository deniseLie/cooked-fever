package com.example.cooked_fever.customers;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.example.cooked_fever.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class CoinManager {
    private List<Coin> uncollectedCoinList = new ArrayList<>();
    private final Context context;

    public CoinManager(Context context) {
        this.context = context;
    }

    public Coin addNewCoins(Context context, int customerID, float x, float y, int coinAmount) {
        Coin newCoin = new Coin(context, customerID, x, y, coinAmount);
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
    }

    public void reset() {
        uncollectedCoinList.clear(); // or however you're tracking floating coins
    }

}
