package com.example.inventoryapp;

import android.content.Context;

import java.util.List;

public class InventoryRepository {
    private InventoryDatabase database;

    public InventoryRepository(Context context) {
        database = new InventoryDatabase(context);
    }

    public List<InventoryItem> getAllItems() {
        return database.getAllItems();
    }

    public void addItem(InventoryItem item) {
        database.addItem(item.getUPC(), item.getDescription(), item.getQuantity());
    }

    public void updateItem(InventoryItem item) {
        database.updateItem(item.getUPC(), item.getDescription(), item.getQuantity());
    }

    public void deleteItem(String UPC) {
        database.deleteItem(UPC);
    }
}
