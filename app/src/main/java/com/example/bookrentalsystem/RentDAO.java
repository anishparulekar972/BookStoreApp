package com.example.bookrentalsystem;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RentDAO {
    @Insert
    void insert(Rent rent);

    @Insert
    void insertLog(LogEntry logEntry);
    @Update
    void update(Rent rent);

    @Delete
    void delete(Rent rent);

    @Query("SELECT * FROM rent_table")
    List<Rent> getAllRents();

    @Query("SELECT * FROM rent_table WHERE username = :username")
    List<Rent> getRentsByUsername(String username);

    @Query("SELECT * FROM log_entries")
    List<LogEntry> getAllLogEntries();
}
