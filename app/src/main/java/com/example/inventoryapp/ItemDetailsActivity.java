package com.example.inventoryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    private InventoryDatabase dbHelper;
    private EditText itemDescriptionEdit, barcodeInput;
    private TextView itemQuantityView;
    private Button increaseButton, decreaseButton, applyButton, deleteButton;
    private String itemUPC = null;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        dbHelper = new InventoryDatabase(this);
        itemDescriptionEdit = findViewById(R.id.itemDescriptionEdit);
        itemQuantityView = findViewById(R.id.itemQuantity);
        increaseButton = findViewById(R.id.increaseButton);
        decreaseButton = findViewById(R.id.decreaseButton);
        applyButton = findViewById(R.id.applyButton);
        deleteButton = findViewById(R.id.deleteButton);
        barcodeInput = findViewById(R.id.itemUPCEdit);

        // Gets the item UPC
        if (getIntent() != null && getIntent().hasExtra("item_UPC")) {
            itemUPC = getIntent().getStringExtra("item_UPC");
        }

        // If the item exists
        if (itemUPC != null) {
            InventoryItem item = dbHelper.getItemByUPC(itemUPC);
            if (item != null) {
                itemDescriptionEdit.setText(item.getDescription());
                barcodeInput.setText(item.getUPC());
                quantity = item.getQuantity();
                itemQuantityView.setText(String.valueOf(quantity));
            }
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
            quantity = 0;
            itemQuantityView.setText(String.valueOf(quantity));
        }

        // Increases the quantity of the item by 1
        increaseButton.setOnClickListener(v -> {
            quantity++;
            itemQuantityView.setText(String.valueOf(quantity));
        });

        // Decreases the quantity of the item by 1
        decreaseButton.setOnClickListener(v -> {
            if (quantity > 0) quantity--;
            itemQuantityView.setText(String.valueOf(quantity));
            // Sens an SMS notification when stock is 0 through the details activity
            if (quantity == 0) {
                SmsHelper.sendLowStockSms(this, "Low Inventory");
                Log.d("TEST", "TEST SMS");
            }
        });

        // Changes the item description when apply is pressed
        applyButton.setOnClickListener(v -> {
            String desc = itemDescriptionEdit.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(ItemDetailsActivity.this, "Please enter a description for the item.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (itemUPC == null) {
                String UPC = barcodeInput.getText().toString().trim();

                if (UPC.isEmpty()) {
                    Toast.makeText(this, "Please scan or enter a UPC", Toast.LENGTH_SHORT).show();
                    return;
                }

                long result = dbHelper.addItem(UPC, desc, quantity);

                if (result != -1) {
                    Toast.makeText(this, "Item has been added.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error adding this item.", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean ok = dbHelper.updateItem(itemUPC, desc, quantity);
                if (ok) {
                    Toast.makeText(this, "Item has been updated.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error updating this item.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Deletes the item when the delete button is pressed
        deleteButton.setOnClickListener(v -> {
            if (itemUPC != null) {
                int deleted = dbHelper.deleteItem(itemUPC);
                if (deleted > 0) {
                    Toast.makeText(this, "Item has been deleted.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error deleting this item.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Scanner functionality
        barcodeInput.setOnEditorActionListener((v, actionId, event) -> {
            // Scanning a Barcode converts the Barcode to a string, that being our UPC, so we need to get the input from the scanner as text
            String UPC = barcodeInput.getText().toString().trim();
            if (!UPC.isEmpty()) {
                handleScannedBarcode(UPC);
                barcodeInput.setText("");
            }

            return true;
        });
    }
    // Scanning an item goes into the edit text UPC field
    @Override
    protected void onResume() {
        super.onResume();

        barcodeInput.requestFocus();
    }

    private void handleScannedBarcode(String UPC) {
        InventoryItem item = dbHelper.getItemByUPC(UPC);

        if (item != null) {
            dbHelper.updateItem(item.getUPC(), item.getDescription(), item.getQuantity() + 1);
        }
    }
}
