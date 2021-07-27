package com.example.milkbottle;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppDatabase db;

    public MainViewModel(@NonNull Application application) {
        super(application);

        db = Room.databaseBuilder(application, AppDatabase.class, "milk-db")
            .fallbackToDestructiveMigration()   //DB구조 바꿀때 사용, 기존 데이터 날아감 (기존 데이터 지키려면 migration)
            .build();
    }
    public LiveData<List<MilkData>> getAll() {
        return db.milkDao().getAll();
    }


    public LiveData<List<MilkData>> getByDay(Date day1, Date day2){
        return db.milkDao().getByday(day1, day2);
    }

    public LiveData<Float> quantityAVG(Date day1, Date day2){
        return db.milkDao().quantityAVG(day1, day2);
    }

    public LiveData<Float> lateQuan(){
        return db.milkDao().lateQuan();
    }

    public void insert(MilkData milkData){
        new InsertAsyncTask(db.milkDao()).execute(milkData);
    }


    public void deleteAll(){
        new DeleteAsyncTask(db.milkDao()).execute();
    }

    private static class InsertAsyncTask extends AsyncTask<MilkData, Void, Void> {
        private MilkDAO mMilkDao;

        public InsertAsyncTask(MilkDAO milkDao) {
            this.mMilkDao = milkDao;
        }

        @Override
        protected Void doInBackground(MilkData... milkDataList) {
            mMilkDao.insert(milkDataList[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<MilkData, Void, Void> {
        private MilkDAO mMilkDao;

        public DeleteAsyncTask(MilkDAO milkDao) {
            this.mMilkDao = milkDao;
        }

        @Override
        protected Void doInBackground(MilkData... milkDataList) {
            mMilkDao.deleteAll();
            return null;
        }
    }


}
