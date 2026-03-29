package com.example.inventoryapp;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public class InventoryRepository {
    private static InventoryRepository instance;
    private InventoryDatabase database;
    private HashMap<String, InventoryItem> inventoryMap;

    private InventoryRepository(Context context) {

        database = new InventoryDatabase(context.getApplicationContext());
        inventoryMap = new HashMap<>();
        loadItems();
    }

    public static InventoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new InventoryRepository(context);
        }
        return instance;
    }

    // Loads all items from the database into the HashMap
    private void loadItems() {
        List<InventoryItem> items = database.getAllItems();
        inventoryMap.clear();

        // Put all items into HashMap
        for (InventoryItem item : items) {
            inventoryMap.put(item.getUPC(), item);
        }
    }
    // Gets all items to display in the recycler view
    public List<InventoryItem> getAllItems() {
        return new java.util.ArrayList<>(inventoryMap.values());
    }

    // Gets an item that matches the UPC
    public InventoryItem getItemByUPC(String UPC) {
        return inventoryMap.get(UPC);
    }

    // Add Item to database and HashMap
    public void addItem(InventoryItem item) {
        // Ensure no duplicate UPCS are added
        if (inventoryMap.containsKey(item.getUPC())) {
            updateItem(item);
            return;
        }
        database.addItem(item.getUPC(), item.getDescription(), item.getQuantity());
        inventoryMap.put(item.getUPC(), item);
    }

    // Updates an item in the database and the hHashMap
    public void updateItem(InventoryItem item) {
        database.updateItem(item.getUPC(), item.getDescription(), item.getQuantity());
        inventoryMap.put(item.getUPC(), item);
    }

    // Deletes an item from the database and the HashMap
    public void deleteItem(String UPC) {
        database.deleteItem(UPC);
        inventoryMap.remove(UPC);
    }

    // Ensure UPC' are unique and not re-added
    public void addOrUpdateItem(String UPC, String description, int quantity) {
        InventoryItem exists = inventoryMap.get(UPC);

        if (exists != null) {
            exists.setDescription(description);
            exists.setQuantity(quantity);
            database.updateItem(UPC, description, quantity);
        } else {
            InventoryItem newItem = new InventoryItem(UPC, description, quantity);
            inventoryMap.put(UPC, newItem);
            database.addItem(UPC, description, quantity);
        }
    }

    // Update quantity when item scanned
    public void updateItemQuantity(String UPC, int quantity) {
        InventoryItem item = inventoryMap.get(UPC);

        if (item != null) {
            item.setQuantity(quantity);
            database.updateItem(UPC, item.getDescription(), quantity);
        }
    }
}
