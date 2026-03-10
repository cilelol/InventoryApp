package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private InventoryRecycler adapter;
    private List<InventoryItem> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button addItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(InventoryViewModel.class);
        recyclerView = findViewById(R.id.inventoryRecyclerView);
        addItemButton = findViewById(R.id.addItemButton);

        // Grid with 2 columns
        GridLayoutManager glm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(glm);

        adapter = new InventoryRecycler(this, items, viewModel);
        recyclerView.setAdapter(adapter);

        addItemButton.setOnClickListener(v -> {
            // Opens item details activity so a new item can be added
            Intent intent = new Intent(InventoryActivity.this, ItemDetailsActivity.class);
            startActivity(intent);
        });

        loadItems();
    }

    // On resume for when coming back from details
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    // Loads the items into the list
    private void loadItems() {
        List<InventoryItem> list = viewModel.getAllItems();
        adapter.updateList(list);
    }
}