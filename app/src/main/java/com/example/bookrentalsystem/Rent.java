package com.example.bookrentalsystem;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rent_table")
public class Rent {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String username;
    private String title;
    private String pickupDate;
    private String returnDate;

    public Rent(String username, String title, String pickupDate, String returnDate) {
        this.username = username;
        this.title = title;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getReturnDate() {
        return returnDate;
    }
}
