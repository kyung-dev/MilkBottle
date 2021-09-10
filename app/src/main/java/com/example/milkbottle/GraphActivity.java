package com.example.milkbottle;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity{

    //변수 선언
    private BarChart chart; //그래프
    Button daily, weekly, monthly, dateBtn, grahpDataBtn; // 일, 주, 월, 날짜선택 버튼
    MainViewModel viewModel;
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat monthInfoFormat = new SimpleDateFormat("yyyy년 MM월");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy년");
    SimpleDateFormat today = new SimpleDateFormat("MMdd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat infoFormat = new SimpleDateFormat("MM월 dd일");

    TextView infoPeriod,infoData;
    BarDataSet barDataSet;
    BarData barData;
    XAxis xAxis;
    YAxis yAxis;
    Date[] currDate;

    ArrayList day = new ArrayList<>();
    ArrayList week = new ArrayList<>();
    ArrayList month = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //변수, 컴포넌트 초기화
        chart = (BarChart) findViewById(R.id.chart);
        daily = (Button) findViewById(R.id.daliy);
        weekly = (Button) findViewById(R.id.weekly);
        monthly = (Button) findViewById(R.id.monthly);
        dateBtn = (Button) findViewById(R.id.datePicker);
        grahpDataBtn = (Button) findViewById(R.id.graphDataBtn);
        infoPeriod = (TextView) findViewById(R.id.infoPeriod);
        infoData = (TextView) findViewById(R.id.infoData);
        final Calendar cal = Calendar.getInstance();
        currDate = new Date[]{cal.getTime()};

        entrySetting(cal.getTime());


        //액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //날짜버튼 해당날짜 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String today = sdf.format(cal.getTime());
        dateBtn.setText(today);

        //모든 데이터 보기 화면으로 전환
        grahpDataBtn.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), GraphDataActivity.class);
            startActivity(intent);
        });

        // 날짜선택
        dateBtn.setOnClickListener(v->{
                DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                        String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
                        Toast.makeText(GraphActivity.this, msg, Toast.LENGTH_SHORT).show();
                        cal.set(Calendar.YEAR,year);
                        cal.set(Calendar.MONTH,month);
                        cal.set(Calendar.DATE,date);
                        currDate[0] = cal.getTime();

                        //날짜버튼 갱신
                        String select = sdf.format(cal.getTime());
                        dateBtn.setText(select);
                        entrySetting(cal.getTime());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
            dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();
            });

        // 초기그래프화면 그래프 그리기


        // 일별 그래프화면
        daily.setOnClickListener(v -> {
            drawGraph(day);
            setInfoData(1);
        });

        // 주별 그래프화면
        weekly.setOnClickListener(v -> {
            drawGraph(week);
            setInfoData(2);
        });

        // 월펼 그래프화면
        monthly.setOnClickListener(v -> {
            drawGraph(month);
            setInfoData(3);
        });
    }

    public void setInfoData(int select){
        infoData.setText("");
        infoPeriod.setText("");
        final String[] date = {""};
        //일별
        if(select==1){
            viewModel.getByDay(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this,dayList->{
                if (dayList.size()>=1)
                    infoPeriod.append(infoFormat.format(dayToWeekly(dayList.get(0).getCurrDate()).get(0))+" ~ "+infoFormat.format(dayToWeekly(dayList.get(0).getCurrDate()).get(1)));
                for(MilkData milk : dayList){
                    if(!date[0].equals(today.format(milk.getCurrDate()))){
                        infoData.append(dayFormat.format(milk.getCurrDate())+"일\n");
                    }
                    infoData.append("   "+milk.getQuantity().toString()+"cc ("+time.format(milk.getCurrDate())+")\n\n");
                    date[0] = today.format(milk.getCurrDate());

                }
            });
        }
        //주별
        else if(select==2){
            viewModel.getByDay(weekToMonthly(currDate[0]).get(0),weekToMonthly(currDate[0]).get(1)).observe(this,dayList->{
                if (dayList.size()>=1)
                    infoPeriod.append(infoFormat.format(weekToMonthly(dayList.get(0).getCurrDate()).get(0))+" ~ "+infoFormat.format(weekToMonthly(dayList.get(0).getCurrDate()).get(1)));
                for(MilkData milk : dayList){
                    if(!date[0].equals(today.format(milk.getCurrDate()))){
                        infoData.append("\n  "+dayFormat.format(milk.getCurrDate())+"일\n");
                    }
                    infoData.append("   "+milk.getQuantity().toString()+"cc ("+time.format(milk.getCurrDate())+")\n");
                    date[0] = today.format(milk.getCurrDate());
                }
            });

        }
        //월별
        else if(select==3){
            viewModel.getByDay(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this,dayList->{
                if (dayList.size()>=1)
                    infoPeriod.append(infoFormat.format(dayToMonthly(dayList.get(0).getCurrDate()).get(0))+" ~ "+infoFormat.format(dayToMonthly(dayList.get(0).getCurrDate()).get(1)));
                for(MilkData milk : dayList){
                    if(!date[0].equals(today.format(milk.getCurrDate()))){
                        infoData.append("\n  "+infoFormat.format(milk.getCurrDate())+"\n");
                    }
                    infoData.append("   "+milk.getQuantity().toString()+"cc ("+time.format(milk.getCurrDate())+")\n");
                    date[0] = today.format(milk.getCurrDate());
                }
            });
        }
    }

    //그래프 그리기
    public void drawGraph(ArrayList entries) {
        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(600);
        YAxis yAxisRight = chart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);



        Description description = new Description();
        description.setText("");

        barDataSet = new BarDataSet(entries, "일/주/월 별 평균 분유 섭취량 (1회당)");
        barDataSet.setColor(R.color.light_gray);
        barData = new BarData(barDataSet);
        chart.setDescription(description);
        chart.getXAxis().setLabelCount(entries.size());
        chart.setData(barData);
        chart.invalidate();

    }

    public void entrySetting(Date date){
        viewModel = ViewModelProviders.of(GraphActivity.this).get(MainViewModel.class);
        Calendar cal = Calendar.getInstance();
        Date day1 = dayToWeekly(date).get(0);
        day.clear();
        week.clear();
        month.clear();

        //일별 entry 데이터 추가
        for(int i=0;i<7;i++){
            Date time = day1;
            cal.setTime(day1);
            cal.add(Calendar.DATE,1);
            day1=cal.getTime();

            viewModel.quantityAVG(time,day1).observe(this,avgData->{
                if(avgData!=null){
                    day.add(new BarEntry(Integer.parseInt(dayFormat.format(time)), avgData));
                }
                else{
                    day.add(new BarEntry(Integer.parseInt(dayFormat.format(time)),0));
                }
            });
        }

        day1=weekToMonthly(date).get(0);
        //주별 entry 데이터 추가
        for(int i=0;i<5;i++){
            Date time = day1;
            cal.setTime(day1);
            cal.add(Calendar.DATE,7);
            day1=cal.getTime();
            if(!monthFormat.format(time).equals((monthFormat.format(day1)))) {
                break;
            }

            int weekNum = i+1;
            viewModel.quantityAVG(time,day1).observe(this,avgData->{
                if(avgData!=null){
                    week.add(new BarEntry(weekNum, avgData));
                }
                else{
                    week.add(new BarEntry(weekNum,0));
                }
            });
        }

        day1=dayToMonthly(date).get(0);
        //월별 entry 데이터 추가
        for(int i=0;i<12;i++){
            Date time = day1;
            cal.setTime(day1);
            cal.add(Calendar.MONTH,1);
            day1=cal.getTime();

            viewModel.quantityAVG(time,day1).observe(this,avgData->{
                if(avgData!=null){
                    month.add(new BarEntry(Integer.parseInt(monthFormat.format(time)), avgData));
                }
                else{
                    month.add(new BarEntry(Integer.parseInt(monthFormat.format(time)),0));
                }
            });
        }
    }


    // 하루 시작, 끝 반환
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

    // 해당 주의 시작, 끝 반환
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

    //해당 날짜의 1일 반환
    public List<Date> weekToMonthly(Date date){
        List<Date> week = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.DATE,1);
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 1 );
        week.add(0,cal.getTime());

        cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        week.add(1,cal.getTime());

        return week;
    }

    // 해당 월의 시작, 끝 반환
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



