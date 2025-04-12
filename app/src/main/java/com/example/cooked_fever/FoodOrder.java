package com.example.cooked_fever;

public class FoodOrder {
    private String itemName;
    private boolean isPrepared;

    public FoodOrder(String itemName) {
        this.itemName = itemName;
        this.isPrepared = false;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void prepare() {
        isPrepared = true;
    }
}