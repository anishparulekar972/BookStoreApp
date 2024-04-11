package com.example.bookrentalsystem;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Rent.class, LogEntry.class}, version = 2)
public abstract class RentDatabase extends RoomDatabase{
    public abstract RentDAO rentDAO();

    private static RentDatabase instance;

    public static synchronized RentDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RentDatabase.class, "rent_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
