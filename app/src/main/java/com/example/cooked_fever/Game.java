package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.example.cooked_fever.Customer;
import com.example.cooked_fever.Appliance;

/**
 * A class representing the main logic of this demo
 */

public class Game {

    private final Runnable sendNotification;
    private final Consumer<Consumer<Canvas>> canvasUser;

    private final Paint customerPaint = new Paint();
    private final Paint appliancePaint = new Paint();
    private final Paint textPaint = new Paint();

    private final List<Customer> customers = new ArrayList<>();
    private final List<Appliance> appliances = new ArrayList<>();

    private long lastUpdateTime = System.currentTimeMillis();
    private long lastCustomerSpawn = System.currentTimeMillis();

    private int screenWidth = 1080;
    private int screenHeight = 1920;

    private final Random random = new Random();

    public Game(Runnable sendNotification, Consumer<Consumer<Canvas>> canvasUser) {
        this.sendNotification = sendNotification;
        this.canvasUser = canvasUser;

        customerPaint.setColor(Color.MAGENTA);
        appliancePaint.setColor(Color.BLUE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setAntiAlias(true);

        // Create 3 fake kitchen appliances
        for (int i = 0; i < 3; i++) {
            appliances.add(new Appliance(200 + i * 300, screenHeight - 300));
        }
    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    public void update() {
        long now = System.currentTimeMillis();
        long deltaTime = now - lastUpdateTime;
        lastUpdateTime = now;

        // Move customers across the screen (real-time element)
        for (Customer c : customers) {
            c.x += (deltaTime / 10.0f);  // Speed based on time
        }

        // Remove customers who exit screen
        Iterator<Customer> iter = customers.iterator();
        while (iter.hasNext()) {
            Customer c = iter.next();
            if (c.x > screenWidth) {
                iter.remove();
                sendNotification.run(); // Send a notification when a customer leaves
            }
        }

        // Spawn new customer every 5 seconds (interval-based)
        if (now - lastCustomerSpawn >= 5000) {
            spawnCustomer();
            lastCustomerSpawn = now;
        }

        // Update appliances (cooking timers)
        for (Appliance appliance : appliances) {
            appliance.update();
        }
    }

    public void draw() {
        canvasUser.accept(canvas -> {
            if (canvas == null) return;

            canvas.drawColor(Color.DKGRAY); // Background

            // Draw appliances
            for (Appliance appliance : appliances) {
                appliance.draw(canvas);
            }

            // Draw customers
            for (Customer customer : customers) {
                canvas.drawRect(customer.x, customer.y, customer.x + 100, customer.y + 100, customerPaint);
                canvas.drawText("😋", customer.x + 25, customer.y + 65, textPaint);
            }

            canvas.drawText("Customers: " + customers.size(), 30, 60, textPaint);
        });
    }

    public void click(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        for (Appliance appliance : appliances) {
            if (appliance.hitbox.contains((int) x, (int) y)) {
                appliance.startCooking(); // Start cooking asynchronously
            }
        }
    }

    public long getSleepTime() {
        return 16; // ~60fps
    }

    private void spawnCustomer() {
        int y = 100 + random.nextInt(screenHeight - 500);
        customers.add(new Customer(0, y));
    }
}