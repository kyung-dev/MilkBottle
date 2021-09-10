package com.example.milkbottle;

import java.util.Date;

public class MilkGraphData {
    Float milkQuantity;
    Date milkDate;

    public MilkGraphData(){}

    public MilkGraphData(Float milkQuantity, Date milkDate){
        this.milkQuantity=milkQuantity;
        this.milkDate=milkDate;
    }

    public void setMilkQuantity(Float milkQuantity) {
        this.milkQuantity = milkQuantity;
    }

    public void setMilkDate(Date milkDate) {
        this.milkDate = milkDate;
    }

    public Date getMilkDate() {
        return milkDate;
    }

    public Float getMilkQuantity() {
        return milkQuantity;
    }
}
