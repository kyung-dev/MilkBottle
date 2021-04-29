package com.example.milkbottle;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class DateValueFormatter extends ValueFormatter {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Date> dateList;

    public DateValueFormatter(List<Date> dateList) {
        this.dateList = dateList;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int axisValue = (int) value;
        if (axisValue >= 0 && axisValue < dateList.size()) {
            return dateFormat.format(dateList.get(axisValue));
        } else {
            return "";
        }
    }
}