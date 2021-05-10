package com.example.milkbottle;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MilkData.class}, version =10 )
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MilkDAO milkDao(); //milkdata 조작
}
