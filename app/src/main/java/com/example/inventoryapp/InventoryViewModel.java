package com.example.inventoryapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class InventoryViewModel extends AndroidViewModel {
    private InventoryRepository repository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = InventoryRepository.getInstance(application);
    }

    public InventoryItem getItemByUPC(String UPC) {
        return repository.getItemByUPC(UPC);
    }

    public void addOrUpdateItem(String UPC, String description, int quantity) {
        repository.addOrUpdateItem(UPC, description, quantity);
    }

    public void updateItemQuantity(String UPC, int quantity) {
        repository.updateItemQuantity(UPC, quantity);
    }

    public void deleteItem(String UPC) {
        repository.deleteItem(UPC);
    }

    public void refresh() {
        repository.getItemsFromAPI();
    }

    public void setListener(InventoryRepository.InventoryChangeListener listener) {
        repository.setListener(listener);
    }
}
