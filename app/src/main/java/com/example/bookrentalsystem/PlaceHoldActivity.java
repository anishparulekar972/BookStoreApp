package com.example.bookrentalsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceHoldActivity extends AppCompatActivity {


    private BookDatabase db;
    private TextView pickupDate, returnDate;
    private Button btnShowDatePickerPickUp,btnShowDatePickerReturn;
    private String pickupDateSt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_hold);
        btnShowDatePickerPickUp = findViewById(R.id.btnShowDatePickerPickUp);
        db = BookDatabase.getDatabase(this);

        pickupDate = findViewById(R.id.textPickupDate);
        returnDate = findViewById(R.id.textReturnData);
        btnShowDatePickerReturn = findViewById(R.id.btnShowDatePickerReturn);
        btnShowDatePickerPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(pickupDate);
            }
        });

        btnShowDatePickerReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(returnDate);
            }
        });


    }

    public static String calculateDateDifferenceString(String dateString1, String dateString2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse the strings into Date objects
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);

            // Calculate the difference in milliseconds
            long difference = date2.getTime() - date1.getTime();

            // Calculate the difference in days
            int differenceInDays = (int) (difference / (24 * 60 * 60 * 1000));

            // Build the result string
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < Math.abs(differenceInDays); i++) {
                if (differenceInDays > 0) {
                    result.append('+');
                } else if (differenceInDays < 0) {
                    result.append('-');
                } else {
                    result.append('=');
                }
            }

            return result.toString();
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the parsing exception
            return "Invalid date format";
        }
    }

    public static boolean isDateDifferenceNotEqualSevenDays(String dateString1, String dateString2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse the strings into Date objects
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);

            // Calculate the difference in milliseconds
            long difference = Math.abs(date1.getTime() - date2.getTime());

            // Calculate the difference in days
            long differenceInDays = difference / (24 * 60 * 60 * 1000);

            // Set a threshold for acceptable difference (e.g., 7 days)
            int thresholdDays = 7;

            // Check if the difference is greater than the threshold
            return differenceInDays < thresholdDays && differenceInDays > 0;
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the parsing exception
            return false; // Return false if parsing fails
        }
    }
    private void showDatePickerDialog(TextView textView) {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String text = textView.getId() == pickupDate.getId() ? "Pickup Date :" : "Return Date :";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // The user has selected a date
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        if (textView.getId() == returnDate.getId()) {
                            Date currentDate = new Date();
                            if (!isDateDifferenceNotEqualSevenDays(pickupDateSt, selectedDate)&& !pickupDateSt.equals(selectedDate)) {
                                LinearLayout containerLayout = findViewById(R.id.containerLayout);
                                containerLayout.removeAllViews();
                                Toast.makeText(PlaceHoldActivity.this, "Can not rent book for more than 7 days " + " " + pickupDate.getText().toString() + " " + selectedDate, Toast.LENGTH_LONG).show();
                                return;
                            }


                            LinearLayout containerLayout = findViewById(R.id.containerLayout);
                            containerLayout.removeAllViews();
                            List<Book> bookList = db.bookDao().getAllBooks();
                            ArrayList<Book> availableBooks = new ArrayList<>();
                            if (bookList.size() > 0) {
                                for (Book book : bookList) {
                                    int pickupDate = Integer.parseInt(pickupDateSt.split("-")[2]);
                                    int returnDate = Integer.parseInt(selectedDate.split("-")[2]);


                                    boolean bookAvailable = true;

                                    for (int i = pickupDate; i <= returnDate; i++) {
                                        if (book.getAvailability().charAt(i - 1) == '-') {
                                            bookAvailable = false;
                                            break;
                                        }
                                    }
                                    if (bookAvailable) {
                                        availableBooks.add(book);
                                    }
                                }
                                if (availableBooks.isEmpty()) {
                                    Toast.makeText(PlaceHoldActivity.this, "No books available during duration", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            if (availableBooks.size() > 0) {
                                for (Book book : availableBooks) {
                                    Button newButton = new Button(PlaceHoldActivity.this);
                                    newButton.setText(book.getTitle()); // Set the text for the button
                                    newButton.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));

                                    // Set an OnClickListener for the newButton if needed
                                    newButton.setOnClickListener(v -> {
                                        // Perform an action when the newButton is clicked
                                        Intent login = new Intent(PlaceHoldActivity.this, Logindo.class);
                                        login.putExtra("bookName", book.getTitle() + "," + pickupDateSt + "," + selectedDate);
                                        startActivity(login);
                                    });

                                    // Add the newButton to the containerLayout
                                    containerLayout.addView(newButton);
                                }
                            } else {
                                Toast.makeText(PlaceHoldActivity.this, "Sorry there are no books available", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            pickupDateSt = selectedDate;
                        }
                        textView.setText(text + " " + selectedDate);
                    }

        },
                year,
                month,
                day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}
