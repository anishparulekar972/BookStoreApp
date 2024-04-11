package com.example.bookrentalsystem;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "books")
public class Book {
    @PrimaryKey
    @NonNull
    private String title;

    private String author;
    private double fee;
    private String availability;


    public Book(String title, String author, double fee, String availability)
    {
        this.title = title;
        this.author = author;
        this.fee = fee;
        this.availability = availability;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public double getFee(){
        return fee;
    }

    public void setfee(double fee){
        this.fee = fee;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability){
        this.availability = availability;
    }
}
