package com.example.bookrentalsystem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface BookDao {
    @Insert
    void insert(Book... books);

    @Update
    void update(Book book);



    @Query("SELECT * FROM books")
    List<Book> getAllBooks();

    @Query("SELECT * FROM books WHERE title = :bookTitle")
    Book getBookByTitle(String bookTitle);

}
