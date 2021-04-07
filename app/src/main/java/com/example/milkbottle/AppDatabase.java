package com.example.milkbottle;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MilkData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MilkDAO milkDao(); //milkdata 조작
}
