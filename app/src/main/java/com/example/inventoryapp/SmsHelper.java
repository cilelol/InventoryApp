package com.example.inventoryapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class SmsHelper {

    // Helper to send sms messages when stock hits 0
    public static void sendLowStockSms(Context context, String itemName) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String phoneNumber = prefs.getString("user_phone", null);

        // If no phone number was saved, ex: when permission is denied; nothing to send SMS notifications to
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.d("SMS", "No phone number saved.");
            return;
        }

        // Sends a SMS notification to user stating which item is out of stock
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Alert: The item '" + itemName + "' is now out of stock.";
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SMS", "SMS sent to " + phoneNumber);
            Toast.makeText(context, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
    }
}
