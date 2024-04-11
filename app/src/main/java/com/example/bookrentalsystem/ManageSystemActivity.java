package com.example.bookrentalsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageSystemActivity extends AppCompatActivity {

    private BookDao bookDao;

    private int loginAttempts = 0;


    private BookDatabase bookDatabase;

    private RentDatabase rentDatabase;

    private RentDAO rentDAO;

    private static final String EXPECTED_USERNAME = "Admin2";
    private static final String EXPECTED_PASSWORD = "Admin2";
    private boolean isUserLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_system);

        // Initialize the database
        bookDatabase = BookDatabase.getDatabase(this);
        bookDao = bookDatabase.bookDao();

        rentDatabase = RentDatabase.getInstance(this);
        rentDAO = rentDatabase.rentDAO();



        // Login functionality
        login();

        // Button to add a new book
        Button addBookButton = findViewById(R.id.btnAddBook);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLoggedIn) {
                    showAddBookDialog();
                } else {
                    Toast.makeText(ManageSystemActivity.this, "Please login first", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Button to display log information
        Button logInfoButton = findViewById(R.id.btnDisplayLog);
        logInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLoggedIn) {
                    displayLogInformation();
                } else {
                    Toast.makeText(ManageSystemActivity.this, "Please login first", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button displayBooksButton = findViewById(R.id.btnDisplayBooks);
        displayBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLoggedIn) {
                    new DisplayBooksAsyncTask(getApplicationContext(), bookDao).execute();
                } else {
                    Toast.makeText(ManageSystemActivity.this, "Please login first", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login() {
        // Retrieve login information from user input
        EditText usernameEditText = findViewById(R.id.editUsername);
        EditText passwordEditText = findViewById(R.id.editPassword);

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUsername = usernameEditText.getText().toString();
                String enteredPassword = passwordEditText.getText().toString();

                // Verify the entered username and password
                if (enteredUsername.equals(EXPECTED_USERNAME) && enteredPassword.equals(EXPECTED_PASSWORD)) {
                    isUserLoggedIn = true;
                    Toast.makeText(ManageSystemActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ManageSystemActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                    loginAttempts++;

                    if (loginAttempts > 2) {
                        // Display the main menu
                        // For demonstration, we'll navigate to the main menu activity
                        // Replace "MainMenuActivity.class" with the actual activity class for your main menu
                        startActivity(new Intent(ManageSystemActivity.this, MainActivity.class));
                        finish(); // Close the current activity
                    }
                }
            }
        });
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Book");
        builder.setMessage("Do you have a new book to add?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Let the librarian enter book information
                showEnterBookInfoDialog();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                // Go back to the main menu (you can implement your main menu logic here)
            }
        });
        builder.show();
    }

    private void showEnterBookInfoDialog() {
        // Use a custom layout for the dialog to get user input (title, author, fee, and availability)
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_book_custom, null);

        // Retrieve input fields from the custom layout
        EditText titleEditText = dialogView.findViewById(R.id.editTitle);
        EditText authorEditText = dialogView.findViewById(R.id.editAuthor);
        EditText feeEditText = dialogView.findViewById(R.id.editFee);
        // Add more fields as needed (e.g., availability)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Book Information");
        builder.setView(dialogView);
        builder.setPositiveButton("Add Book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Retrieve entered book information
                String title = titleEditText.getText().toString();
                String author = authorEditText.getText().toString().trim();
                String feeText = feeEditText.getText().toString();
                CheckBox availabilityCheckBox = dialogView.findViewById(R.id.checkAvailability);
                // Retrieve availability and other information as needed

                if (title.isEmpty() || author.isEmpty() || feeText.isEmpty()) {
                    Toast.makeText(ManageSystemActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                }

                if (author.equals("")) {
                    Toast.makeText(ManageSystemActivity.this, "Author field cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                }

                double fee;
                try{
                    fee = Double.parseDouble(feeText);
                }catch (NumberFormatException e){
                    Toast.makeText(ManageSystemActivity.this, "Invalid fee value", Toast.LENGTH_LONG).show();
                    return;
                }
                Book existingBook = bookDatabase.bookDao().getBookByTitle(title);
                if (existingBook != null) {
                    // Display an error message if the book title already exists
                    Toast.makeText(ManageSystemActivity.this, "Book with the same title already exists", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // Add the new book to the database asynchronously using AsyncTask
                Book newBook = new Book(title, author, fee, "+++++++++++++++++++++++++++++++");  // Assuming availability is set to true
                new AddBookAsyncTask(bookDatabase).execute(newBook);

                // Log the operation
                LogUtility.logOperation(ManageSystemActivity.this, "Add New Book", "Admin2");
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addNewBook() {
        // Retrieve book information from user input
        EditText titleEditText = findViewById(R.id.editTitle);
        EditText authorEditText = findViewById(R.id.editAuthor);
        EditText feeEditText = findViewById(R.id.editFee);

        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        double fee = Double.parseDouble(feeEditText.getText().toString());


        // Add the new book to the database asynchronously using AsyncTask
        Book newBook = new Book(title, author, fee, "+");

        new AddBookAsyncTask(bookDatabase).execute(newBook);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        LogEntry log = new LogEntry("Add book",timeStamp,"Book added!");
        RentDatabase.getInstance(this).rentDAO().insertLog(log);
        // Logged the operation
    }

    private static class AddBookAsyncTask extends AsyncTask<Book, Void, Void> {
        private BookDatabase bookDatabase;

        AddBookAsyncTask(BookDatabase bookDatabase) {
            this.bookDatabase = bookDatabase;
        }

        @Override
        protected Void doInBackground(Book... books) {
            try {
                // Perform the database operation on a background thread
                bookDatabase.bookDao().insert(books);
            } catch (Exception e) {
                Log.e("AsyncTask", "Error in background task: " + e.getMessage());
            }
            return null;
        }
    }
    private void displayLogInformation() {
        // The implementation remains the same
        List<LogEntry> allLogEntries = RentDatabase.getInstance(this).rentDAO().getAllLogEntries();

        // Concatenate log messages into a single string
        StringBuilder logMessageBuilder = new StringBuilder();
        for (LogEntry logEntry : allLogEntries) {
            logMessageBuilder.append("Operation Type: ").append(logEntry.getOperationType()).append("\n")
                    .append(logEntry.getMessage()).append("\n")
                    .append("Date and Time: ").append(logEntry.getTimestamp()).append("\n\n");
        }

        // Show the concatenated log messages in a single Toast
        Toast.makeText(this, logMessageBuilder.toString(), Toast.LENGTH_LONG).show();
    }


    private static class DisplayBooksAsyncTask extends AsyncTask<Void, Void, List<Book>> {
        private Context context;
        private BookDao bookDao;

        DisplayBooksAsyncTask(Context context, BookDao bookDao) {
            this.context = context;
            this.bookDao = bookDao;
        }

        @Override
        protected List<Book> doInBackground(Void... voids) {
            // Perform the database operation on a background thread
            return bookDao.getAllBooks();
        }

        @Override
        protected void onPostExecute(List<Book> allBooks) {
            // Display the books using Toast on the main thread
            StringBuilder booksInfo = new StringBuilder("All Books:\n");
            for (Book book : allBooks) {
                booksInfo.append("Title: ").append(book.getTitle()).append("\n");
                booksInfo.append("Author: ").append(book.getAuthor()).append("\n");
                String formattedFee = String.format("Fee: $%.2f\n\n", book.getFee());
                booksInfo.append(formattedFee);
            }

            // Use the application context passed in the constructor to display the Toast
            Toast.makeText(context, booksInfo.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
