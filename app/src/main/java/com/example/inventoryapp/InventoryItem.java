package com.example.inventoryapp;

// Class for each inventory item
public class InventoryItem {
    private String UPC;
    private String description;
    private int quantity;

    // Default constructor for Retrofit
    public InventoryItem() {
    }

    public InventoryItem(String UPC, String description, int quantity) {
        this.UPC = UPC;
        this.description = description;
        this.quantity = quantity;
    }

    // Getter for item UPC
    public String getUPC() {
        return UPC;
    }
    // Getter for item description
    public String getDescription() {
        return description;
    }
    // Setter for item description
    public void setDescription(String description) {
        this.description = description;
    }
    // Getter for item quantity
    public int getQuantity() {
        return quantity;
    }
    // Setter for item quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
