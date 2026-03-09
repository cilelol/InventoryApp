package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {
    private TextView textView;
    private EditText editTextPhone;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        textView = findViewById(R.id.textStatus);
        editTextPhone = findViewById(R.id.editPhone);
        button = findViewById(R.id.buttonSend);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedPhone = prefs.getString("user_phone", "");
        if (!savedPhone.isEmpty()) {
            editTextPhone.setText(savedPhone);
        }

        button.setOnClickListener(v -> {
            String phone = editTextPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                textView.setText("Please enter a phone number.");
                return;
            }

            // Save phone number
            prefs.edit().putString("user_phone", phone).apply();

            if (ActivityCompat.checkSelfPermission(SmsActivity.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "Test SMS", null, null);

                    textView.setText("SMS sent");
                    Log.d("TEST", "SMS sent to " + phone);
                    Toast.makeText(SmsActivity.this, "Phone number saved and SMS sent", Toast.LENGTH_SHORT).show();

                    // Continue to the main inventory layout
                    startActivity(new Intent(SmsActivity.this, InventoryActivity.class));
                    finish();

                } catch (Exception e) {
                    Log.e("TEST", "Failed to send SMS: " + e.getMessage());
                    textView.setText("Failed to send SMS.");
                }
            } else {
                ActivityCompat.requestPermissions(SmsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 123);
                button.setEnabled(false);
            }
        });

        // Request permission if not already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TEST", "Permission has been granted");
                textView.setText("You can send SMS!");
                button.setEnabled(true);

            } else {
                Log.d("TEST", "Permission not granted");
                textView.setText("SMS working");
                button.setEnabled(false);

                // Go to inventory activity if permission is denied
                startActivity(new Intent(SmsActivity.this, InventoryActivity.class));
                finish();
            }
        }
    }
}
