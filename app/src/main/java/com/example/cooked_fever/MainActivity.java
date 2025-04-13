package com.example.cooked_fever;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.example.cooked_fever.game.GameActivity;

/**
 * A class representing an activity of the main menu.
 */
public class MainActivity extends Activity {
    private final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate method called");

        // If layout fails to load, this will help catch it.
        try {
            setContentView(R.layout.activity_main);
            Log.d(LOG_TAG, "Layout set successfully");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to set layout", e);
        }

        // Optional: Uncomment to hide status bar
        // hideStatusBar();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController wic = getWindow().getInsetsController();
            if (wic != null) {
                wic.hide(WindowInsets.Type.statusBars());
                Log.d(LOG_TAG, "Status bar hidden");
            }
        }
    }

    public void buttonClicked(View view) {
        Log.d(LOG_TAG, "Button clicked: attempting to launch GameActivity");

        try {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            Log.d(LOG_TAG, "GameActivity started");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to start GameActivity", e);
        }
    }
}
