package com.example.cooked_fever.customers;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import com.example.cooked_fever.food.*;

public class CustomerManager {
    private final List<Customer> customers = new ArrayList<>();


    public CustomerManager() {

    }

    public void addCustomer(int x, int y, List<String> orders) {
        customers.add(new Customer(x, y, orders));
    }
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
    public List<Customer> getCustomerList() {
        return customers;
    }

    public Customer handleTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (Customer customer : customers) {
            if (customer.isCustomerHitbox((int)x, (int)y)) {  // Check if the click is within the bounds of the food item
                return customer;
            }
        }
        return null;
    }

    public void receiveItem (Customer customer, FoodItem foodItem) {
        customer.serveItem(foodItem.getFoodItemName());
    }
    public void draw(Canvas canvas) {
        for (Customer customer : customers) {
            customer.draw(canvas);
        }
    }
}
