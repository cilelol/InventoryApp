package com.example.inventoryapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryRepository {
    private static InventoryRepository instance;
    private APIService apiService;
    private HashMap<String, InventoryItem> inventoryMap;
    private InventoryChangeListener listener;


    private InventoryRepository(Context context) {
        apiService = RetrofitClient.getInstance().create(APIService.class);
        inventoryMap = new HashMap<>();
    }

    public interface InventoryChangeListener {
        void onInventoryChanged(List<InventoryItem> items);
    }

    public static InventoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new InventoryRepository(context.getApplicationContext());
        }
        return instance;
    }

    // Notify inventory changes
    private void notifyChange() {
        if (listener != null) {
            listener.onInventoryChanged(new ArrayList<>(inventoryMap.values()));
        }
    }
    // Set listener
    public void setListener(InventoryChangeListener listener) {
        this.listener = listener;
        listener.onInventoryChanged(new ArrayList<>(inventoryMap.values()));
    }
    // Gets all the items from the API and puts them into the hashmap
    public void getItemsFromAPI() {
        Log.d("DEBUG", "API CALL STARTED");
        apiService.getAllItems().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inventoryMap.clear();
                    for (InventoryItem item : response.body()) {
                        inventoryMap.put(item.getUPC(), item);
                    }
                    notifyChange();
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                Log.e("DEBUG", "API FAILED", t);
            }
        });
    }
    /*
    Gets all items to display in the recycler view
    public List<InventoryItem> getAllItems() {
        return new java.util.ArrayList<>(inventoryMap.values());
    }

    */
    // Gets an item that matches the UPC
    public InventoryItem getItemByUPC(String UPC) {
        return inventoryMap.get(UPC);
    }

    // Deletes an item from the database and the HashMap
    public void deleteItem(String UPC) {
        apiService.deleteItem(UPC).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                inventoryMap.remove(UPC);
                notifyChange();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Ensure UPCs are unique and not re-added
    public void addOrUpdateItem(String UPC, String description, int quantity) {
        InventoryItem item = new InventoryItem(UPC, description, quantity);
        if (inventoryMap.containsKey(UPC)) {
            // If the item exists in the database, update the item
            apiService.updateItem(UPC, item).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    inventoryMap.put(UPC, item);
                    notifyChange();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } else {
            // If the item does not exist, add the item
            apiService.addItem(item).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    inventoryMap.put(UPC, item);
                    notifyChange();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    // Update quantity when item scanned
    public void updateItemQuantity(String UPC, int quantity) {
        InventoryItem item = inventoryMap.get(UPC);
        if (item != null) {
            item.setQuantity(quantity);
            apiService.updateItem(UPC, item).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    inventoryMap.put(UPC, item);
                    notifyChange();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
