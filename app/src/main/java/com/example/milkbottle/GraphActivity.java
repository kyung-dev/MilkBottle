package com.example.milkbottle;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    static int dataLen = 0;
    Button daliy, weekly, monthly,datePicker;
    final static long refernce_timestamp =1609340400823L;
    static Date pickDate = null;
    private DatePickerDialog.OnDateSetListener callbackMethod;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        lineChart = (LineChart) findViewById(R.id.chart);
        daliy = (Button) findViewById(R.id.daliy);
        weekly = (Button) findViewById(R.id.weekly);
        monthly = (Button) findViewById(R.id.monthly);
        datePicker = (Button) findViewById(R.id.datePicker);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        List<Entry> entries = new ArrayList<>();
        final Calendar cal = Calendar.getInstance();
        final Date[] currDate = {cal.getTime()};

        viewModel.getAll().observe(this, milkData -> {
            Log.d("DB","date data "+milkData.toString());
//            test.setText(milkData.toString());
        });

        datePicker.setOnClickListener(v->{
            Calendar calendar = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                        String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
                        Toast.makeText(GraphActivity.this, msg, Toast.LENGTH_SHORT).show();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DATE,date);
                        currDate[0] = calendar.getTime();
                        viewModel.getDay(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(GraphActivity.this, milkData -> {
                            drawChart(daliyView(milkData));
                        });
                    }

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();

            });

        viewModel.getDay(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, milkData -> {
            drawChart(daliyView(milkData));
        });

        daliy.setOnClickListener(v -> {
            Log.d("dateTime","daily  "+currDate[0]);
            viewModel.getDay(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, milkData -> {
                drawChart(daliyView(milkData));
            });
        });
        weekly.setOnClickListener(v -> {
            Log.d("dateTime","weekly  "+currDate[0]);
            viewModel.getDay(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this, milkData -> {
                drawChart(weeklyView(milkData));
            });
        });

        monthly.setOnClickListener(v -> {
            viewModel.getDay(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this, milkData -> {
                drawChart(monthlyView(milkData));
            });
        });
    }

    public void drawChart(List<Entry> entry){
        LineDataSet lineDataSet = new LineDataSet(entry, "분유량");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setColor(Color.parseColor("#CDDC39"));
        lineDataSet.setCircleColor(Color.parseColor("#CDDC39"));
        lineDataSet.setCircleColors(Color.parseColor("#CDDC39"));
        lineDataSet.setCircleHoleColor(Color.parseColor("#FFFFFF"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);


        //x축 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);


        //y축 설정
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.invalidate();

    }

    public List<Entry> daliyView(List<MilkData> milkData) {
        List<Entry> entries = new ArrayList<>();
        for (MilkData milkDate : milkData) {
            long dateTime = milkDate.getCurrDate().getTime()-refernce_timestamp;
            Float quantity = milkDate.getQuantity();
            entries.add(new Entry(dateTime, quantity));
        }
        lineChart.getAxisLeft().setLabelCount(entries.size());
        lineChart.getXAxis().setValueFormatter(new HourAxisValueFormatter(refernce_timestamp));
        lineChart.invalidate();
        return entries;
    }

    public List<Entry> weeklyView(List<MilkData> milkData) {
        List<Entry> entries = new ArrayList<>();

        for (MilkData milkDate : milkData) {
            long dateTime = milkDate.getCurrDate().getTime()-refernce_timestamp;
            Float quantity = milkDate.getQuantity();
            entries.add(new Entry(dateTime, quantity));
        }
        lineChart.getAxisLeft().setLabelCount(entries.size());
        lineChart.getXAxis().setValueFormatter(new DayAxisValueFormatter(refernce_timestamp));
        lineChart.invalidate();
        return entries;
    }

    public List<Entry> monthlyView(List<MilkData> milkData) {
        List<Entry> entries = new ArrayList<>();
        for (MilkData milkDate : milkData) {
            long dateTime = milkDate.getCurrDate().getTime()-refernce_timestamp;
            Float quantity = milkDate.getQuantity();
            entries.add(new Entry(dateTime, quantity));
        }
        lineChart.getAxisLeft().setLabelCount(entries.size());
        lineChart.getXAxis().setValueFormatter(new MonthAxisValueFormatter(refernce_timestamp));
        lineChart.invalidate();
        return entries;
    }

    public List<Date> dayToday(Date day){
        List<Date> daliy = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR, 0);
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 1 );
        Date day1 =cal.getTime();
        daliy.add(0,day1);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        Date day2 =cal.getTime();
        daliy.add(1,day2);

        return daliy;
    }

    public List<Date> dayToWeekly(Date day){
        List<Date> weekly = new ArrayList<>();
        int date;

        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(day);
        date = cal.get(Calendar.DAY_OF_WEEK);

       switch (date){
           case 1 :
               weekly.add(0, day);
               break;
           case 2 :
               cal.add(Calendar.DATE,-1);
               weekly.add(0,cal.getTime());
               break;
           case 3 :
               cal.add(Calendar.DATE,-2);
               weekly.add(0,cal.getTime());
               break;
           case 4 :
               cal.add(Calendar.DATE,-3);
               weekly.add(0,cal.getTime());
               break;
           case 5 :
               cal.add(Calendar.DATE,-4);
               weekly.add(0,cal.getTime());
               break;
           case 6 :
               cal.add(Calendar.DATE,-5);
               weekly.add(0,cal.getTime());
               break;
           case 7 :
               cal.add(Calendar.DATE,-6);
               weekly.add(0,cal.getTime());
               break;
       }

        cal.setTime(weekly.get(0));
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 1 );
        weekly.add(0, cal.getTime());

        cal.add(Calendar.DATE,+6);
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        weekly.add(1,cal.getTime());

        return weekly;
    }

    public List<Date> dayToMonthly(Date date){
        List<Date> monthly = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DATE,1);
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 1 );
        monthly.add(0,cal.getTime());

        cal.set(Calendar.MONTH,11);
        cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        monthly.add(1,cal.getTime());

        return monthly;
    }


}



