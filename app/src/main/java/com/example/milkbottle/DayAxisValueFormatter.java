package com.example.milkbottle;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DayAxisValueFormatter extends ValueFormatter {

    private long referenceTimestamp; // minimum timestamp in your data set
    private final DateFormat mDataFormat;
    private Date mDate;

    DayAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("dd", Locale.getDefault());
        this.mDataFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.mDate = new Date();
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long convertedTimestamp = (long) value;

        // Retrieve original timestamp
        long originalTimestamp = referenceTimestamp + convertedTimestamp;
        // Convert timestamp to hour:minute
        return getHour(originalTimestamp);
    }

    private String getHour(long timestamp) {
        try {
            mDate.setTime(timestamp);
            return mDataFormat.format(mDate) +"일";
        } catch (Exception ex) {
            return "xx";
        }
    }

}
