package com.example.bookrentalsystem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE username = :username")
    User getUserByUsername(String username);
}
