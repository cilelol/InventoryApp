package com.example.inventoryapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {
    private InventoryRepository repository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = InventoryRepository.getInstance(application);
    }

    public List<InventoryItem> getAllItems() {
        return repository.getAllItems();
    }

    public InventoryItem getItemByUPC(String UPC) {
        return repository.getItemByUPC(UPC);
    }

    public void addOrUpdateItem(String UPC, String description, int quantity) {
        repository.addOrUpdateItem(UPC, description, quantity);
    }
    public void addItem(InventoryItem item) {
        repository.addItem(item);
    }

    public void updateItem(InventoryItem item) {
        repository.updateItem(item);
    }

    public void updateItemQuantity(String UPC, int quantity) {
        repository.updateItemQuantity(UPC, quantity);
    }

    public void deleteItem(String UPC) {
        repository.deleteItem(UPC);
    }
}
