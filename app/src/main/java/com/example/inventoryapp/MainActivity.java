package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private LoginDatabase dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        dbHandler = new LoginDatabase(this);

    }
    // When login is clicked, database checks whether the user exists, and if so, bring them to the inventory layout
    public void onLoginClick(View view) {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username and password, then click login.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHandler.checkUserAndPass(username, password)) {
            Toast.makeText(this, "Login was successful!", Toast.LENGTH_SHORT).show();

            boolean firstLogin = getSharedPreferences("MyPrefs", MODE_PRIVATE).getBoolean(username + "_firstLogin", true);
            Intent intent;
            if (firstLogin) {
                // Send to SMS screen on first login to prompt user for permission
                intent = new Intent(this, SmsActivity.class);

                // Mark first login as done so user does not go through this screen every time
                getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean(username + "_firstLogin", false).apply();
            } else {
                intent = new Intent(this, InventoryActivity.class);
            }

            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "Invalid username or password. If you do not already have an account, " +
                    "enter desired username and password then press register.", Toast.LENGTH_LONG).show();
        }
    }
    // Registers the user with the provided username and password after clicking register. If the username is already in use, prompts user to select a new username
    public void onRegisterClick(View view) {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username and password, then click login.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHandler.checkUser(username)) {
            Toast.makeText(this, "Username already exists, Please log in or choose another username.", Toast.LENGTH_SHORT).show();
        }
        else {
            long result = dbHandler.addUser(username, password);

            if (result != -1) {
                Toast.makeText(this, "Account created successfully. Please log in", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
