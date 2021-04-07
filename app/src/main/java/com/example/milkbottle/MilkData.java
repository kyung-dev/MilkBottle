package com.example.milkbottle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MilkData {
    @PrimaryKey(autoGenerate = true) //알아서 primary key 지정
    private int id; //데이터베이스에서 primary key
    private String befoData;

    public MilkData(String befoData) {
        this.befoData = befoData;
    }

    public String getBefoData() {
        return befoData;
    }

    public void setBefoData(String befoData) {
        this.befoData = befoData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {  //내용을 확인 할 수 있도록
        return "MilkData{" +
                "id=" + id +
                ", befoData='" + befoData + '\'' +
                '}';
    }
}
