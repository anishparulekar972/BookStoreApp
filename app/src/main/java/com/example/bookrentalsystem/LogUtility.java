package com.example.bookrentalsystem;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogUtility {

    private static final List<String> allLogMessages = new ArrayList<>();
    private static final List<String> adminLogMessages = new ArrayList<>();

    public static void logOperation(Context context, String operationType, String username) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logMessage = String.format("%s - Operation: %s, Username: %s", timeStamp, operationType, username);

        // Store log message globally
        allLogMessages.add(logMessage);


        // Store log message for admin-specific operations
        if ("Admin2".equalsIgnoreCase(username)) {
            adminLogMessages.add(logMessage);

            // Show log information using Toast only for admin
            Toast.makeText(context, logMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public static void logPlaceHoldOperation(Context context, String username, String pickupDate, String returnDate, String bookTitle, String totalAmount) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logMessage = String.format("\n%s - Place Hold: Username: %s, Pickup Date: %s, Return Date: %s, Book Title: %s, Total Amount: %s",
                timeStamp, username, pickupDate, returnDate, bookTitle, totalAmount);

        allLogMessages.add(logMessage);
    }

    public static List<String> getAdminLogMessages() {
        return adminLogMessages;
    }

    public static List<String> getAllLogMessages() {
        return allLogMessages;
    }
}



