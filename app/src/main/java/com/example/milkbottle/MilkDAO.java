package com.example.milkbottle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MilkDAO {
    @Query("SELECT * FROM MilkData") // milkData에서 모든 값 불러오는 것
    List<MilkData> getAll();

    @Insert
    void insert(MilkData milk);

    @Update
    void update(MilkData milk);

    @Delete
    void delete(MilkData milk);
}
