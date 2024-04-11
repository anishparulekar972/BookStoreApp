package com.example.bookrentalsystem;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase{

    public abstract UserDAO userDAO();

    private static UserDatabase dbinstance;

    public static synchronized UserDatabase getInstance(Context context) {
        if (dbinstance == null) {
            dbinstance = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class,
                            "userDatabase").allowMainThreadQueries()
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbinstance;
    }

    public static void initializeUsers(UserDatabase database) {
        UserDAO userDAO = database.userDAO();

        // Check if users already exist to avoid duplicates
        if (userDAO.getUserByUsername("alice5") == null) {
            userDAO.insert(new User("alice5", "csumb100"));
        }
        if (userDAO.getUserByUsername("Brian7") == null) {
            userDAO.insert(new User("Brian7", "123abc"));
        }
        if (userDAO.getUserByUsername("chris12") == null) {
            userDAO.insert(new User("chris12", "CHRIS12"));
        }
    }
}
