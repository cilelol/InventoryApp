package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "login.db";
    private static final int VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public LoginDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME
                + " TEXT UNIQUE, " + COL_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Adds user to the login database
    public long addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_USERS, null, values);

    }

    // Checks username and password with what is stored in the database
    public boolean checkUserAndPass(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        boolean exists = false;
        String sql = "SELECT * FROM " + TABLE_USERS + " WHERE username=? and password=?";
        try (Cursor cursor = db.rawQuery(sql, new String[] {username, password})) {
            return cursor.moveToFirst();
        }
    }

    // Checks just the user
    public boolean checkUser(String username) {
        SQLiteDatabase db = getReadableDatabase();

        boolean exists = false;
        String sql = "SELECT * FROM " + TABLE_USERS + " WHERE username=?";
        try (Cursor cursor = db.rawQuery(sql, new String[] {username})) {
            return cursor.moveToFirst();
        }
    }
}
