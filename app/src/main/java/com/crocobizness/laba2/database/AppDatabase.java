package com.crocobizness.laba2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AudioRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase appDatabase;

    public abstract AudioRecordDao audioRecordDao();

    public static AppDatabase getDatabase(Context context){
        if (appDatabase == null){
            synchronized (AppDatabase.class){
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,"app_database")
                            .build();
                }
            }
        }

        return appDatabase;
    }
}
