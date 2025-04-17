package com.example.cooked_fever.game;

/**
 * A class representing the game loop of the demo.
 */
public class GameThread extends Thread {
    private boolean isRunning = false;

    private final Game game;

    public GameThread(final Game game) {
        this.game = game;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    public void stopLoop() {
        isRunning = false;
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();

        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;

            game.update();
            game.draw();

            game_sleep(elapsedTime);
        }
    }

    private void game_sleep(long elapsedTime) {
        long targetFrameTime = 16; // target ~60 FPS
        long sleepTime = targetFrameTime - elapsedTime;

        if (sleepTime > 0) {
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
