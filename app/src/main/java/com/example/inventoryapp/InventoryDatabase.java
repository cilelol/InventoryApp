package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class InventoryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;

    public static final String TABLE_INVENTORY = "inventory";
    public static final String COL_UPC = "UPC";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_QUANTITY = "quantity";

    public InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COL_UPC + " TEXT PRIMARY KEY, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_QUANTITY + " INTEGER)";
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Add items to inventory database, only if the UPC does not already exist in the database
    public long addItem(String UPC, String description, int quantity) {
        if (UPC == null || UPC.isEmpty()) return -1;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_UPC, UPC);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_QUANTITY, quantity);

        // Ensure no duplicates are inserted
        try {
            return db.insert(TABLE_INVENTORY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Gets all items in the inventory database
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_UPC + ", " + COL_DESCRIPTION + ", " + COL_QUANTITY +
                " FROM " + TABLE_INVENTORY + " ORDER BY " + COL_UPC + " DESC";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                do {
                    String UPC = cursor.getString(0);
                    String desc = cursor.getString(1);
                    int qty = cursor.getInt(2);
                    list.add(new InventoryItem(UPC, desc, qty));
                }
                while (cursor.moveToNext());
            }
        }
        return list;
    }

    // Get a single item from inventory database by the item UPC
    public InventoryItem getItemByUPC(String UPC) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_UPC + ", " + COL_DESCRIPTION + ", " + COL_QUANTITY +
                " FROM " + TABLE_INVENTORY + " WHERE " + COL_UPC + "=? LIMIT 1";
        try (Cursor cursor = db.rawQuery(sql, new String[]{ UPC })) {
            if (cursor.moveToFirst()) {
                return new InventoryItem(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
            }
        }
        return null;
    }

    // Update an item in the inventory database
    public boolean updateItem(String UPC, String description, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_QUANTITY, quantity);
        int rows = db.update(TABLE_INVENTORY, values, COL_UPC + "=?", new String[]{ UPC });
        return rows > 0;
    }

    // Delete an item in the inventory database
    public int deleteItem(String UPC) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_INVENTORY, COL_UPC + "=?", new String[]{ UPC });
    }
}
