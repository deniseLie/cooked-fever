package com.example.cooked_fever.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.util.*;
import java.util.function.Consumer;

import com.example.cooked_fever.*;

/**
 * A class representing a view for the game activity.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Game game;
    private GameThread gameThread;


    private Runnable onReadyCallback;

    public void setOnReady(Runnable onReady) {
        this.onReadyCallback = onReady;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.game = new Game(context, this::useCanvas);
        init(context);
    }

    public GameView(Context context) {
        super(context);
        this.game = new Game(context, this::useCanvas);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setKeepScreenOn(true);
        getHolder().addCallback(this);
        setFocusable(View.FOCUSABLE);

        // On touch listener
        setOnTouchListener((view, event) -> {

            if (game.isGameOver()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float touchX = event.getX();
                    float touchY = event.getY();

                    // Only allow interaction inside restart button bounds
                    float centerX = getWidth() / 2f;
                    float restartButtonY = getHeight() / 2f + 100;

                    float buttonWidth = 400;
                    float buttonHeight = 100;

                    RectF restartRect = new RectF(
                            centerX - buttonWidth / 2f,
                            restartButtonY - buttonHeight / 2f,
                            centerX + buttonWidth / 2f,
                            restartButtonY + buttonHeight / 2f
                    );

                    if (restartRect.contains(touchX, touchY)) {
                        game.restart();
                    }
                }
                return true; // Block all other input while game over
            }

            // Only allow other interactions if game is not over
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                game.click(event);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                game.drag(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                game.release(event);
            }

            invalidate(); // Force redraw
            return true;
        });
    }

    public Game getGame() {
        return game;
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

        if (onReadyCallback != null) {
            onReadyCallback.run();
            onReadyCallback = null; // prevent double calls
        }

        gameThread.startLoop();
    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int format, final int width, final int height) {
        final Rect rect = getHolder().getSurfaceFrame();
        game.resize(rect.width(), rect.height());
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
