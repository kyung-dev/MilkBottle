package com.example.milkbottle;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsActivity extends AppCompatActivity {

    Button dateBtn;
    Button perMonth;
    Button perWeight;
    Button input;
    TextView avgVal;
    TextView whatType;
    TextView recommandData;
    int type = 0;//체중별 - 1, 개월수별 - 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //초기화
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<MilkData>> milkData;
        dateBtn = (Button) findViewById(R.id.dateBtn);
        perMonth = (Button) findViewById(R.id.perMonth);
        perWeight = (Button) findViewById(R.id.perWeight);
        input = (Button) findViewById(R.id.input);
        avgVal = (TextView) findViewById(R.id.avgVal);
        whatType = (TextView) findViewById(R.id.whatType);
        recommandData = (TextView) findViewById(R.id.recommendData);
        final Calendar cal = Calendar.getInstance();
        final Date[] currDate = {cal.getTime()};

        //액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //날짜버튼 오늘 날짜 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String today = sdf.format(cal.getTime());
        dateBtn.setText(today);

        //날짜버튼 datePicker
        dateBtn.setOnClickListener(v->{
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(StatsActivity.this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                    String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
                    Toast.makeText(StatsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    calendar.set(Calendar.YEAR,year);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.DATE,date);

                    String select = sdf.format(calendar.getTime()); // 선택한 날짜로 날짜버튼 세팅
                    dateBtn.setText(select);

                    // 평균섭취량 선택한 날짜로 갱신
                    viewModel.quantityAVG(dayToday(calendar.getTime()).get(0),dayToday(calendar.getTime()).get(1)).observe(StatsActivity.this, avg -> {
                        if(avg!=null)
                            avgVal.setText(Float.toString(avg) + "cc");
                    });

                    };


            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

            dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
            dialog.show();






        });

        // 평균섭취량
        viewModel.quantityAVG(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, avg -> {
            if(avg!=null)
                avgVal.setText(Float.toString(avg) + "cc");
        });

        //개월수 별 권장 섭취량
       perMonth.setOnClickListener(v->{
            whatType.setText("개월수");
            recommandData.setText("");
            type=0;
        });

        //체중 별 권장 섭취량
        perWeight.setOnClickListener(v->{
            whatType.setText("체중");
            recommandData.setText("");
            type=1;
        });

        input.setOnClickListener(v->{
            if (type ==0 ){
                recommandData.setText("개월수권장량");
            }else {
                recommandData.setText("체중권장량");
            }
        });
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

}
