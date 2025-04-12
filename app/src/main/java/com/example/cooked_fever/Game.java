package com.example.cooked_fever;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.example.cooked_fever.appliances.TableTop;
import com.example.cooked_fever.customers.Customer;
import com.example.cooked_fever.Appliance;
import com.example.cooked_fever.CocaColaMaker;

/**
 * A class representing the main logic of this demo
 */

public class Game {

    private final Runnable sendNotification;
    private final Consumer<Consumer<Canvas>> canvasUser;

    private final Paint customerPaint = new Paint();
    private final Paint appliancePaint = new Paint();
    private final Paint textPaint = new Paint();

    private long lastUpdateTime = System.currentTimeMillis();
    private long lastCustomerSpawn = System.currentTimeMillis();

    private int screenWidth = 1080;
    private int screenHeight = 1920;

    private final Random random = new Random();
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Var game Logic
    private final String[] foodItems = {"Cola"};

    // Customer Static Variables
    private final int MAX_CUSTOMER = 5;
    private final int CUSTOMER_SPACING = 220;
    private final int START_X = 200;
    private final int CUSTOMER_Y = 200;

    // List
    private final List<Customer> customers = new ArrayList<>();
    private boolean[] customerSlots = new boolean[MAX_CUSTOMER];
    private final List<Appliance> appliances = new ArrayList<>();
    private final List<TableTop> tableTops = new ArrayList<>();

    public Game(Runnable sendNotification, Consumer<Consumer<Canvas>> canvasUser) {
        this.sendNotification = sendNotification;
        this.canvasUser = canvasUser;

        // Pain sprites
        customerPaint.setColor(Color.MAGENTA);
        appliancePaint.setColor(Color.BLUE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setAntiAlias(true);

    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // Create kitches appliance
        appliances.clear();
        appliances.add(new CocaColaMaker(200, screenHeight - 300));

        // Create tabletops in 2 columns (3 rows)
        int leftX = 900;
        int rightX = 1300; // Adjust as needed for spacing between columns
        int plateWidth = 400;
        int plateHeight = 250;
        int baseY = screenHeight - 500;
        int rowGap = 250;

        tableTops.clear();

        for (int i = 0; i < 6; i++) {
            int row = i % 3; // 0-2

            int x = i < 3 ? leftX : rightX;
            int y = baseY - row * rowGap;

            tableTops.add(new TableTop(x, y, plateWidth, plateHeight, i));
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        long deltaTime = now - lastUpdateTime;
        lastUpdateTime = now;

       // Update customers
        Iterator<Customer> iter = customers.iterator();
        while (iter.hasNext()) {
            Customer c = iter.next();
            c.update();

            // If Customer need to leave
            if (c.shouldLeave()) {
                customerSlots[c.getSlotIndex()] = false;
                iter.remove();
            }
        }

        // Spawn new customer every 5 seconds and have enough space
        if (now - lastCustomerSpawn >= 5000 && customers.size() < MAX_CUSTOMER) {
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

            // Draw TableTops
            for (TableTop tableTop : tableTops) {
                tableTop.draw(canvas);
            }

            // Draw customers
            for (Customer customer : customers) {
                customer.draw(canvas);
            }

            canvas.drawText("Customers: " + customers.size(), 30, 60, textPaint);
        });
    }

    public void click(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // Appliance interaction
        for (Appliance appliance : appliances) {
            if (appliance.onClick(x, y)) {
                // If the appliance interacted, stop checking others
                return;
            }
        }

        // Customer interaction
        for (Customer customer : customers) {
            if (x >= customer.getX() && x <= customer.getX() + 100 &&
                    y >= customer.getY() && y <= customer.getY() + 100) {
                Log.d("Game", "Serve Customer");
                customer.serveItem("Cola"); // Assuming you’ll implement this method
                break;
            }
        }

        // TableTop interaction
        for (TableTop tabletop : tableTops) {

            if (x >= customer.getX() && x <= customer.getX() + 100 &&
                    y >= customer.getY() && y <= customer.getY() + 100) {
                Log.d("Game", "Serve Customer");
                customer.serveItem("Cola"); // Assuming you’ll implement this method
                break;
            }
        }
    }

    public long getSleepTime() {
        return 16; // ~60fps
    }

    private void spawnCustomer() {

        // Get slot index
        int slotIndex = getNextAvailableSlot();
        if (slotIndex == -1) return;    // No available slots

        // Customer spawn
        int x = START_X + slotIndex * CUSTOMER_SPACING;
        int y = CUSTOMER_Y;

        // Random number of items between 1–3
        int orderCount = 1 + random.nextInt(3);
        List<String> order = new ArrayList<>();

        for (int i = 0; i < orderCount; i++) {
            order.add(foodItems[random.nextInt(foodItems.length)]);
        }

        Customer customer = new Customer(x, y, order);
        customer.setSlotIndex(slotIndex);   // save slot index
        customerSlots[slotIndex] = true;

        customers.add(customer);
    }

    // Helper function to find empty slot index
    private int getNextAvailableSlot() {
        for (int i = 0; i < MAX_CUSTOMER; i++) {
            if (!customerSlots[i]) return i;
        }
        return -1;
    }
}