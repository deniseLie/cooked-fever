package com.example.cooked_fever;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cooked_fever.game.GameView;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private LinearLayout mainMenu;
    private LinearLayout restartOverlay;
    private Button startButton;
    private Button restartButton;

    private final Handler handler = new Handler();
    private final Runnable checkGameOverRunnable = new Runnable() {
        @Override
        public void run() {
            if (gameView.getGame().isGameOver()) {
                showRestartOverlay();
            } else {
                handler.postDelayed(this, 500); // Check again in 0.5 seconds
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate called");

        gameView = findViewById(R.id.game_view);
        mainMenu = findViewById(R.id.main_menu);
        restartOverlay = findViewById(R.id.restart_overlay);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restart_button);

        // Start Game
        startButton.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            gameView.setVisibility(View.VISIBLE);
            gameView.setOnReady(() -> {
                gameView.getGame().restart();
                handler.post(checkGameOverRunnable);
            });
        });

        // Restart Game
        restartButton.setOnClickListener(v -> {
            restartOverlay.setVisibility(View.GONE);

            gameView.setOnReady(() -> {
                gameView.getGame().restart();
                handler.post(checkGameOverRunnable);
            });
        });
    }

    private void showRestartOverlay() {
        int starCount = gameView.getGame().getRating(); // implement this method
        setStars(starCount);
        restartOverlay.setVisibility(View.VISIBLE);
    }

    private void setStars(int count) {
        ImageView star1 = findViewById(R.id.star1);
        ImageView star2 = findViewById(R.id.star2);
        ImageView star3 = findViewById(R.id.star3);

        // Use temporary drawables for now
        int filled = android.R.drawable.btn_star_big_on;
        int empty = android.R.drawable.btn_star_big_off;

        star1.setImageResource(count >= 1 ? filled : empty);
        star2.setImageResource(count >= 2 ? filled : empty);
        star3.setImageResource(count >= 3 ? filled : empty);
    }
}
