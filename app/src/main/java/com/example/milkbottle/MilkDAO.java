package com.example.milkbottle;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface MilkDAO {
    @Query("SELECT * FROM MilkData ORDER BY currFloat ASC") // milkData에서 모든 값 불러오는 것
    LiveData<List<MilkData>> getAll();


    @Insert
    void insert(MilkData milk);

    @Update
    void update(MilkData milk);

//    @Delete
//    void delete(MilkData milk);

    @Query("SELECT * FROM MilkData WHERE currDate BETWEEN :day1 AND :day2 ORDER BY currFloat ASC")
    LiveData<List<MilkData>> getByday(Date day1, Date day2);

    @Query("SELECT AVG(quantity) FROM MilkData WHERE currDate BETWEEN :day1 AND :day2 ORDER BY currFloat ASC")
    LiveData<Float> quantityAVG(Date day1, Date day2);

    @Query("SELECT SUM(quantity) FROM MilkData WHERE currDate BETWEEN :day1 AND :day2 ORDER BY currFloat ASC")
    LiveData<Float> quantitySUM(Date day1, Date day2);

    @Query("SELECT COUNT(quantity) FROM MilkData WHERE currDate BETWEEN :day1 AND :day2 ORDER BY currFloat ASC")
    LiveData<Float> quantityCOUNT(Date day1, Date day2);

    @Query("SELECT * FROM MilkData ORDER BY currDate DESC limit 1")
    LiveData<MilkData> lateData();

    @Query("DELETE FROM MilkData")
    int deleteAll();

    @Query("DELETE FROM MilkData WHERE id = :id")
    int delete(int id);

    @Query("SELECT currDate from MilkData /* ORDER BY userName ASC*/")
    LiveData<List<Date>> getDate();


}
