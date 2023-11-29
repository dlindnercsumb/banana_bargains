package com.example.bananabargains.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public final static String DATABASE_NAME = "BananaBargains.db";
    public final static String USER_TABLE = "USER_TABLE";
    public final static String CART_TABLE = "CART_TABLE";
    public final static String BANANA_TABLE = "BANANA_TABLE";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract BananaBargainsDAO BananaBargainsDAO();

    public static AppDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (LOCK) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

}
