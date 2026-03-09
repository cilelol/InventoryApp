package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class InventoryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;

    public static final String TABLE_INVENTORY = "inventory";
    public static final String COL_ID = "id";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_QUANTITY = "quantity";

    public InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_QUANTITY + " INTEGER)";
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Add items to inventory database
    public long addItem(String description, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_QUANTITY, quantity);

        // Returns -1 on failure to insert
        return db.insert(TABLE_INVENTORY, null, values);
    }

    // Gets all items in the inventory database
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_ID + ", " + COL_DESCRIPTION + ", " + COL_QUANTITY +
                " FROM " + TABLE_INVENTORY + " ORDER BY " + COL_ID + " DESC";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String desc = cursor.getString(1);
                    int qty = cursor.getInt(2);
                    list.add(new InventoryItem(id, desc, qty));
                }
                while (cursor.moveToNext());
            }
        }
        return list;
    }

    // Get a single item from inventory database
    public InventoryItem getItemById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_ID + ", " + COL_DESCRIPTION + ", " + COL_QUANTITY +
                " FROM " + TABLE_INVENTORY + " WHERE " + COL_ID + "=? LIMIT 1";
        try (Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(id) })) {
            if (cursor.moveToFirst()) {
                return new InventoryItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
            }
        }
        return null;
    }

    // Update an item in the inventory database
    public boolean updateItem(int id, String description, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_QUANTITY, quantity);
        int rows = db.update(TABLE_INVENTORY, values, COL_ID + "=?", new String[]{ String.valueOf(id) });
        return rows > 0;
    }

    // Delete an item in the inventory database
    public int deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_INVENTORY, COL_ID + "=?", new String[]{ String.valueOf(id) });
    }

    // Updates the item quantity
    public void updateItemQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        db.update(TABLE_INVENTORY, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
