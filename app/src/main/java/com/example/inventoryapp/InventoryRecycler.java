package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryRecycler extends RecyclerView.Adapter<InventoryRecycler.ViewHolder> {

    private final List<InventoryItem> items;
    private final Context context;
    private final InventoryDatabase databaseHelper;

    public InventoryRecycler(Context context, List<InventoryItem> items, InventoryDatabase dbHelper) {
        this.context = context;
        this.items = items;
        this.databaseHelper = dbHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, quantity;
        Button increaseButton, decreaseButton;
        public ViewHolder(@NonNull View v) {
            super(v);
            description = v.findViewById(R.id.itemDescription);
            quantity = v.findViewById(R.id.itemQuantity);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    // Connects the inventory to the recycler
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InventoryItem item = items.get(position);
        holder.description.setText(item.getDescription());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        // Increase quantity of item by 1
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            databaseHelper.updateItemQuantity(item.getId(), newQuantity);
            notifyItemChanged(position);
        });

        // Decrease quantity of item by 1
        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = Math.max(0, item.getQuantity() - 1);
            item.setQuantity(newQuantity);
            databaseHelper.updateItemQuantity(item.getId(), newQuantity);
            notifyItemChanged(position);
            // Sends a SMS notification when stock is set to 0 through the inventory main page
            SmsHelper.sendLowStockSms(v.getContext(), item.getDescription());
            Log.d("TEST", "TEST SMS");
        });


        holder.itemView.setOnClickListener(v -> {
            // Opens the item details activity
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });
    }

    // Getter for the size of items
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Updates the list
    public void updateList(List<InventoryItem> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }
}
