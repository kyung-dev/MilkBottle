package com.example.milkbottle;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MilkDAO {
    @Query("SELECT * FROM MilkData") // milkData에서 모든 값 불러오는 것
    LiveData<List<MilkData>> getAll();

    @Insert
    void insert(MilkData milk);

    @Update
    void update(MilkData milk);

//    @Delete
//    void delete(MilkData milk);


    @Query("DELETE FROM MilkData")
    int deleteAll();

    @Query("DELETE FROM MilkData WHERE id = :id")
    int delete(int id);

    @Query("SELECT currDate from MilkData /* ORDER BY userName ASC*/")
    LiveData<List<String>> getDate();


}
