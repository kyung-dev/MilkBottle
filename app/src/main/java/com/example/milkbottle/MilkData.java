package com.example.milkbottle;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity
public class MilkData {
    @PrimaryKey(autoGenerate = true) //알아서 primary key 지정
    private int id; //데이터베이스에서 primary key

    @ColumnInfo(name = "befoData")
    private int befoData;

    @ColumnInfo(name = "afterdata")
    private int afterData;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "currDate")
    private Date currDate;

    @ColumnInfo(name = "currFloat")
    private Float currFloat;




    public MilkData(int befoData, int afterData, int quantity, Date currDate, float currFloat) {
        this.befoData = befoData;
        this.afterData = afterData;
        this.quantity = quantity;
        this.currDate = currDate;
        this.currFloat = currFloat;
    }

    public int getId() {
        return id;    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBefoData() {
        return befoData;
    }

    public void setBefoData(int befoData) {
        this.befoData = befoData;
    }

    public int getAfterData() {
        return afterData;
    }

    public void setAfterData(int afterData) {
        this.afterData = afterData;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getCurrDate() {
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
    }

    public Float getCurrFloat() {
        return currFloat;
    }

    public void setCurrDate(Float currFloat) {
        this.currFloat = currFloat;
    }


    @Override
    public String toString() {  //내용을 확인 할 수 있도록
        return "MilkData{" +
                "id=" + id +
                ", befoData='" + befoData + '\'' +
                ", afterData='" + afterData + '\'' +
                ", quantity='" + quantity + '\'' +
                ", currDate='" + currDate + '\'' +
                ", currFloat='" + currFloat + '\'' +
                '}';
    }
}
