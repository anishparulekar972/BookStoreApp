package com.example.bookrentalsystem;

import static java.lang.Boolean.TRUE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = BookDatabase.getDatabase(this);

        addBookList();
    }

    public void goToCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void goToPlaceHold(View v){
        Intent intent = new Intent(this, PlaceHoldActivity.class);
        startActivity(intent);
    }

    public void goToManageSystem(View v){
        Intent intent = new Intent(this, ManageSystemActivity.class);
        startActivity(intent);
    }

    public void goToCancelHold(View v){
        Intent intent = new Intent(this, CancelHoldActivity.class);
        startActivity(intent);
    }


    private void addBookList() {
        new AddBooksAsyncTask(db).execute();
    }

    private static class AddBooksAsyncTask extends AsyncTask<Void, Void, Void> {

        private BookDatabase db;

        AddBooksAsyncTask(BookDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<Book> bookList = db.bookDao().getAllBooks();

                Log.d("AddBooksAsyncTask", "Book list size:" + bookList.size());

                // If there is no data, add five records as an example.
                if (bookList.size() <= 0) {
                    Book[] defaultBooks = new Book[3];
                    defaultBooks[0] = new Book("Hot Java", "J Gross", 1.50, "+++++++++++++++++++++++++++++++");
                    defaultBooks[1] = new Book("Fun Java", "Y Byun", 2.00, "+++++++++++++++++++++++++++++++");
                    defaultBooks[2] = new Book("Algorithm for Java", "K Alice", 2.25, "+++++++++++++++++++++++++++++++");

                    Log.d("AddBooksAsyncTask", "Inserting default books");

                    db.bookDao().insert(defaultBooks);

                    Log.d("AddBooksAsyncTask", "Default books inserted");
                }
            }catch (Exception e){
                Log.e("AddBooksAsyncTask", "Error in doInBackground", e);
            }
            return null;
        }
    }
}