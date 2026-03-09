package com.example.inventoryapp;

// Class for each inventory item
public class InventoryItem {
    private int id;
    private String description;
    private int quantity;

    public InventoryItem(int id, String description, int quantity) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
    }

    public InventoryItem(String description, int quantity) {
        this(-1, description, quantity);
    }
    // Getter for item ID
    public int getId() {
        return id;
    }
    // Setter for item ID
    public void setId(int id) {
        this.id = id;
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
