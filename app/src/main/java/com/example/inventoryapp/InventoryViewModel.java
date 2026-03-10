package com.example.inventoryapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {
    private InventoryRepository repository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = new InventoryRepository(application);
    }

    public List<InventoryItem> getAllItems() {
        return repository.getAllItems();
    }

    public void addItem(InventoryItem item) {
        repository.addItem(item);
    }

    public void updateItem(InventoryItem item) {
        repository.updateItem(item);
    }

    public void deleteItem(int id) {
        repository.deleteItem(id);
    }
}
