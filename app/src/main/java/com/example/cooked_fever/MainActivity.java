package com.example.cooked_fever;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import com.example.cooked_fever.utils.SoundUtils;


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
        SoundUtils.init(getApplicationContext());

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
            SoundUtils.startBGM(getApplicationContext());

            gameView.setOnReady(() -> {
                gameView.getGame().restart();
                handler.post(checkGameOverRunnable);
            });
        });

        // Restart Game
        restartButton.setOnClickListener(v -> {
            restartOverlay.setVisibility(View.GONE);
            SoundUtils.startBGM(getApplicationContext()); // just in case bgm stops after game over

            gameView.setOnReady(() -> {
                gameView.getGame().restart();
                handler.post(checkGameOverRunnable);
            });
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundUtils.stopBGM();
    }

    private void showRestartOverlay() {
        restartOverlay.setVisibility(View.VISIBLE);
    }
}
