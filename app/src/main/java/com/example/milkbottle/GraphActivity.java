package com.example.milkbottle;

import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    static int dataLen =0;
    private List<Date> dateList = new ArrayList<>();
    private List<Float> dateListF = new ArrayList<>();
    private String[] days = {"mon", "tue", "wed", "thu","fri","sat","sun"};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        lineChart = (LineChart)findViewById(R.id.chart);

        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");



        viewModel.getAll().observe(this, milkData ->{
            dataLen = milkData.size();
            MilkData milkList;

            for(MilkData milkDate : milkData){
                long dateTime = milkDate.getCurrDate().getTime();
                int quantity = milkDate.getQuantity();
                entries.add(new Entry(dateTime, quantity));
                dateList.add(milkDate.getCurrDate());
                dateListF.add(milkDate.getCurrFloat());
            }
            Log.d("dataSize","entries-"+entries.size());

            LineDataSet lineDataSet = new LineDataSet(entries, "분유량");
            lineDataSet.setLineWidth(2);
            lineDataSet.setCircleRadius(6);
            lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setCircleColors(Color.BLUE);
            lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
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
            xAxis.setValueFormatter(new LineChartXAxisValueFormatter());

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


        });

    }

}
