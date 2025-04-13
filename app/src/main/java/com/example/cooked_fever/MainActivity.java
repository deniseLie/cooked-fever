package com.example.cooked_fever;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

        gameView = findViewById(R.id.game_view);
        mainMenu = findViewById(R.id.main_menu);
        restartOverlay = findViewById(R.id.restart_overlay);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restart_button);

        // Start Game
        startButton.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            gameView.setVisibility(View.VISIBLE);
            gameView.getGame().restart(); // just in case
            handler.post(checkGameOverRunnable); // start polling game over
        });

        // Restart Game
        restartButton.setOnClickListener(v -> {
            gameView.getGame().restart();
            restartOverlay.setVisibility(View.GONE);
            handler.post(checkGameOverRunnable); // start polling again
        });
    }

    private void showRestartOverlay() {
        restartOverlay.setVisibility(View.VISIBLE);
    }
}
