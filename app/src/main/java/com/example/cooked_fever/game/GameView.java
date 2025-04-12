package com.example.cooked_fever.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.util.Log;
import java.util.function.Consumer;

import com.example.cooked_fever.*;

/**
 * A class representing a view for the game activity.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final Game game = new Game(this::sendNotification, this::useCanvas);
    private GameThread gameThread;
    private final String LOG_TAG = this.getClass().getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    GameView(final Context context) {
        super(context);
        Log.d("GameView", "Surface created, starting game thread");
        setKeepScreenOn(true);
        getHolder().addCallback(this);
        setFocusable(View.FOCUSABLE);

        // On touch listener
        setOnTouchListener((view, event) -> {
            Log.d("TouchEvent", "Action: " + event.getAction());
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                game.click(event);
            }
            return true;
        });
    }

    private void sendNotification() {
        NotificationPublisher.showNotification(getContext());
    }

    private boolean useCanvas(final Consumer<Canvas> onDraw) {
        boolean result = false;
        try {
            final SurfaceHolder holder = getHolder();
            final Canvas canvas = holder.lockCanvas();
            try {
                // passes the canvas to onDraw (in this case draw method from Game class)
                onDraw.accept(canvas);
            } finally {
                try {
                    holder.unlockCanvasAndPost(canvas);
                    result = true;
                } catch (final IllegalStateException e) {
                    // Do nothing
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        if ((gameThread == null) || (gameThread.getState() == Thread.State.TERMINATED)) {
            gameThread = new GameThread(game);
        }
        final Rect rect = getHolder().getSurfaceFrame(); // to get a reference to screen width and height
        game.resize(rect.width(), rect.height());
        gameThread.startLoop();
    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int format, final int width, final int height) {
        game.resize(width, height);
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
        gameThread.stopLoop();
        gameThread = null;
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        game.draw(); //initial draw at the creation of SurfaceView
    }
}
