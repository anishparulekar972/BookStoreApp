package com.example.bookrentalsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CancelHoldActivity extends AppCompatActivity {

    private BookDatabase db;

    private int loginAttempts = 0;

    private UserDatabase userDatabase;

    private EditText editTextUsername;
    private EditText editTextPassword;

    private LinearLayout linearLayoutReservations;

    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_hold);

        userDatabase = UserDatabase.getInstance(this);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        UserDatabase.initializeUsers(userDatabase);
        linearLayoutReservations = findViewById(R.id.linearlayoutReservations);
        cancel = findViewById(R.id.btnLogin);

        db = BookDatabase.getDatabase(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClick(view);
            }
        });
    }

    public void onLoginButtonClick(View view) {
        // Retrieve entered username and password
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        // Perform validation using Room database
        if (isValidCredentials(username, password)) {
            // Credentials are valid, perform further actions (e.g., navigate to another activity)
            // For demonstration, we'll show a toast message
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            displayRentedBooks(username);
        } else {
            // Credentials are invalid, show an error message
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            loginAttempts++;

            if (loginAttempts > 2) {
                // Display the main menu
                // For demonstration, we'll navigate to the main menu activity
                // Replace "MainMenuActivity.class" with the actual activity class for your main menu
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Close the current activity
            }
        }
    }

    private boolean isValidCredentials(String username, String password) {
        // Use Room database to check if the entered credentials are valid
        User user = userDatabase.userDAO().getUserByUsername(username);

        return user != null && user.getPassword().equals(password);
    }


    private void displayRentedBooks(String username) {
        List<Rent> rentedBooks = RentDatabase.getInstance(this).rentDAO().getRentsByUsername(username);
        linearLayoutReservations.removeAllViews();


        if (rentedBooks.isEmpty()) {
            Toast.makeText(this, "There is no reservation for the username: " + username, Toast.LENGTH_LONG).show();
        }

        // Create a list of strings to display in the ListView
        for (Rent rent : rentedBooks) {
            Button bookButton = new Button(this);
            String buttonText = "Title: " + rent.getTitle() +
                    ", Pickup Date: " + rent.getPickupDate() +
                    ", Return Date: " + rent.getReturnDate();
            bookButton.setText(buttonText);


            //Book book = db.bookDao().getBookByTitle(rent.getTitle());

            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click, e.g., navigate to book details
                    cancelHold(rent);
                }
            });
            // Set the adapter for the ListView
            linearLayoutReservations.addView(bookButton);
        }
    }


    private void cancelHold(Rent rent) {
        int pickupDate = Integer.parseInt(rent.getPickupDate().split("-")[2]);
        int returnDate = Integer.parseInt(rent.getReturnDate().split("-")[2]);


        String username = rent.getUsername();
        String bookTitle = rent.getTitle();
        int difference = ((returnDate - pickupDate) + 1);
        Book book = db.bookDao().getBookByTitle(bookTitle);

        if (book != null) {
            StringBuilder rented = new StringBuilder(book.getAvailability());
            StringBuilder resultRented = new StringBuilder();
            for (int i = 0; i < difference; i++) {
                resultRented.append("+");
            }

            rented.replace(pickupDate - 1, returnDate, String.valueOf(resultRented));
            Toast.makeText(this, "Rented: " + rented, Toast.LENGTH_LONG).show();
            book.setAvailability(String.valueOf(rented));
            db.bookDao().update(book);
            RentDatabase.getInstance(this).rentDAO().delete(rent);
            displayRentedBooks(username);

            logCancelHoldOperation(username, bookTitle, rent.getPickupDate(), rent.getReturnDate());

            Toast.makeText(this, "Hold cancelled on book: " + bookTitle, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Book not found for cancellation", Toast.LENGTH_LONG).show();
        }
    }

    private void logCancelHoldOperation(String username, String bookTitle, String pickupDate, String returnDate) {
        String operationType = "Cancel hold";
        String operationDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String logMessage = String.format("\nOperation type: %s\nCustomer's username: %s\nBook title: %s\nPickup date: %s\nReturn date: %s\nOperation date and time: %s",
                operationType, username, bookTitle, pickupDate, returnDate, operationDateTime);

        // Log the cancel hold operation
        LogUtility.logOperation(this, operationType, username);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        LogEntry log = new LogEntry("Cancel Hold", timeStamp,"Username:" + username+"\tBook title:" + bookTitle+"\tPickup date:" +pickupDate+"\tReturn date:" + returnDate);
        RentDatabase.getInstance(this).rentDAO().insertLog(log);
    }
}
