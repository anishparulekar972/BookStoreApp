package com.example.bookrentalsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logindo extends AppCompatActivity {

    Button goBack, submit;
    EditText userName, password;

    TextView textView, bookNameText, bookAuthorText, bookRateText, rentDurationText;
    private boolean attempts = false;
    private boolean attemptsDuplicate = false;
    private BookDatabase db;

    private UserDatabase userDatabase;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_do);
        Intent intent = getIntent();
        String[] intentString = intent.getStringExtra("bookName").split(",");

        db = BookDatabase.getDatabase(this);
        submit = findViewById(R.id.buttonSubmit);
        userName = findViewById(R.id.userNameText);
        password = findViewById(R.id.PasswordText);
        textView = findViewById(R.id.textBookName);
        bookAuthorText = findViewById(R.id.textAuthor);

        bookRateText = findViewById(R.id.textRate);
        rentDurationText = findViewById(R.id.rentDuration);

        Book book = db.bookDao().getBookByTitle(intentString[0]);
        textView.setText("Name: " + book.getTitle());
        bookAuthorText.setText("Author: " + book.getAuthor());
        bookRateText.setText("Rate: $" + new String(String.valueOf(book.getFee())));
        rentDurationText.setText("Duration: " + intentString[1] + " - " + intentString[2]);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userName.length()>0 && password.length()>0){
                    authorize(intentString, book);
                }

            }
        });
    }

    private void authorize (String[] intenString, Book book){
        User user = UserDatabase.getInstance(this).userDAO().getUserByUsername(userName.getText().toString());
        if (user != null){
            if (user.getPassword().equals(password.getText().toString()) && user.getUsername().equals(userName.getText().toString())){
                placeHold(intenString, book, user);
                return;
            }

        }
        if (attempts){
            attempts=false;
            Intent main = new Intent(Logindo.this, MainActivity.class);
            startActivity(main);
        }
        Toast.makeText(Logindo.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
        attempts=true;
    }

    private void placeHold(String[] intentString, Book book, User user) {
        int pickupDate = Integer.parseInt(intentString[1].split("-")[2]);
        int returnDate = Integer.parseInt(intentString[2].split("-")[2]);
        int difference = ((returnDate - pickupDate) + 1);
        double fee = difference * book.getFee();

        StringBuilder rented = new StringBuilder(book.getAvailability());
        StringBuilder resultRented = new StringBuilder();
        for (int i = 0; i < difference; i++) {
            resultRented.append("-");
        }
        rented.replace(pickupDate-1, returnDate, String.valueOf(resultRented));
        book.setAvailability(String.valueOf(rented));
        db.bookDao().update(book);
        String message = "User: "+ userName.getText().toString() + "\nPickup Date: " + intentString[1] + "\nReturn Date: " + intentString[2] + "\nBook Title: " + book.getTitle() + "\nAmount owed: $" + String.format("%.2f", fee);
        Toast.makeText(Logindo.this, message, Toast.LENGTH_LONG).show();

        Rent rent = new Rent(user.getUsername(), book.getTitle(), intentString[1], intentString[2]);
        RentDatabase.getInstance(this).rentDAO().insert(rent);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        LogEntry newlog = new LogEntry("Place Hold",timeStamp,"Username: " + userName.getText().toString()+
               "\nPick up date: "+  intentString[1]+
                "\nReturn Date: " + intentString[2]+
               "\nbook title: " +  book.getTitle()+
                "\tRate: $" + String.valueOf(fee));
        RentDatabase.getInstance(this).rentDAO().insertLog(newlog);

        Intent main = new Intent(Logindo.this, MainActivity.class);
        startActivity(main);

    }
}
