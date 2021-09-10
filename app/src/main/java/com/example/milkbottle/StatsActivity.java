package com.example.milkbottle;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
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

public class StatsActivity extends AppCompatActivity {

    MainViewModel viewModel;
    final Calendar cal = Calendar.getInstance();
    final Date[] currDate = {cal.getTime()};
    SimpleDateFormat cycleFormat = new SimpleDateFormat("HH시간 mm분");
    Button dateBtn;
    Button dailyBtn, weeklyBtn, monthlyBtn;
    Button perMonthBtn;
    Button perWeightBtn;
    Button inputBtn;
    TextView period;
    TextView avgVal;
    TextView cycleData;
    TextView whatType;
    TextView recommandData;
    NumberPicker numberPicker;
    int type = 0;//체중별 - 1, 개월수별 - 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //초기화
        LiveData<List<MilkData>> milkData;
        viewModel = ViewModelProviders.of(StatsActivity.this).get(MainViewModel.class);
        dateBtn = (Button) findViewById(R.id.dateBtn);
        perMonthBtn = (Button) findViewById(R.id.perMonth);
        perWeightBtn = (Button) findViewById(R.id.perWeight);
        dailyBtn = (Button) findViewById(R.id.day);
        weeklyBtn = (Button) findViewById(R.id.week);
        monthlyBtn = (Button) findViewById(R.id.month);
        inputBtn = (Button) findViewById(R.id.input);
        period = (TextView) findViewById(R.id.period);
        avgVal = (TextView) findViewById(R.id.avgVal);
        cycleData = (TextView) findViewById(R.id.cycleData);
        whatType = (TextView) findViewById(R.id.whatType);
        recommandData = (TextView) findViewById(R.id.recommendData);
        numberPicker = (NumberPicker) findViewById(R.id.number_picker);

        String[] monthData = new String[]{"0~1개월","1~2개월", "2~3개월","3~4개월","4~5개월","5~8개월","9~10개월"};
        String[] weightData = new String[]{"~3.6kg","~4kg", "~4.5kg","~5kg","~5.5kg","~6.3kg"};


        //액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //날짜버튼 오늘 날짜 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String today = sdf.format(cal.getTime());
        dateBtn.setText(today);

        //기간 세팅
        period.setText(today);

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
                    currDate[0]=calendar.getTime();
                 };


            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

            dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
            dialog.show();
        });

        //일 평균 섭취량
        dailyBtn.setOnClickListener(v->{
            setCycle(1);
            period.setText(sdf.format(currDate[0]));
            viewModel.quantityAVG(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, avg -> {
                if(avg!=null) {
                    avgVal.setText(String.format("%.1f",avg) + "cc ");
                }
                else
                    avgVal.setText("0cc");
            });

            viewModel.quantitySUM(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, sum ->{
                if(sum!=null) {
                    avgVal.append("( "+String.format("%.1f",sum)+"cc/");

                }
                else
                    avgVal.append("(0/");
            });
            viewModel.quantityCOUNT(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this, count->{
                int quantity=0;
                if(count!=null)
                    quantity = Math.round(count);
                avgVal.append(Integer.toString(quantity)+"회)");
            });

        });

        //주 평균섭취량
        weeklyBtn.setOnClickListener(v->{
            setCycle(2);
            period.setText(sdf.format(dayToWeekly(currDate[0]).get(0))+" ~ "+sdf.format(dayToWeekly(currDate[0]).get(1)));
            viewModel.quantityAVG(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this, avg -> {
                if(avg!=null) {
                    avgVal.setText(String.format("%.1f",avg) + "cc ");
                }
                else
                    avgVal.setText("0cc");
            });

            viewModel.quantitySUM(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this, sum ->{
                if(sum!=null) {
                    avgVal.append("( "+String.format("%.1f",sum)+"cc/");
                }else
                    avgVal.append("(0/");
            });
            viewModel.quantityCOUNT(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this, count->{
                int quantity=0;
                if(count!=null)
                    quantity = Math.round(count);
                avgVal.append(Integer.toString(quantity)+"회)");
            });

        });

        //월 평균 섭취량
        monthlyBtn.setOnClickListener(v->{
            setCycle(3);
            period.setText(sdf.format(dayToMonthly(currDate[0]).get(0))+" ~ "+sdf.format(dayToMonthly(currDate[0]).get(1)));
            viewModel.quantityAVG(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this, avg -> {
                if(avg!=null) {
                    avgVal.setText(String.format("%.1f",avg) + "cc ");
                }
                else
                    avgVal.setText("0cc");
            });

            viewModel.quantitySUM(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this, sum ->{
                if(sum!=null) {
                    avgVal.append("( "+String.format("%.1f",sum)+"cc/");
                }else
                    avgVal.append("(0/");
            });
            viewModel.quantityCOUNT(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this, count->{
                int quantity=0;
                if(count!=null)
                    quantity = Math.round(count);
                avgVal.append(Integer.toString(quantity)+"회)");
            });
        });

        //개월수 별 권장 섭취량
       perMonthBtn.setOnClickListener(v->{
            whatType.setText("개월수");
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(monthData.length-1);
            numberPicker.setDisplayedValues(monthData);
            recommandData.setText("");

       });

        //체중 별 권장 섭취량
        perWeightBtn.setOnClickListener(v->{
            whatType.setText("체중");
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(weightData.length-1);
            numberPicker.setDisplayedValues(weightData);
            recommandData.setText("");
            type=1;
        });

        inputBtn.setOnClickListener(v->{
            if (type ==0 ){
                recommandData.setText(recommend(monthData[numberPicker.getValue()]));
            }else {
                recommandData.setText(recommend(weightData[numberPicker.getValue()]));
            }
        });
    }

    public void setCycle(int select){
        Date day1 = currDate[0];
        Date day2 =  null;
        Calendar cal = Calendar.getInstance();
        //일별 주기
        cycleData.setText("");
        if(select==1) {
            viewModel.getByDay(dayToday(currDate[0]).get(0),dayToday(currDate[0]).get(1)).observe(this,milkList->{
                long date1=0;
                long date2=0;
                long sum=0;
                if(milkList.size()>=2) {
                    date1 = milkList.get(0).getCurrDate().getTime();
                    for (MilkData milk : milkList) {
                        date2 = milk.getCurrDate().getTime();
                        sum += (date2 - date1);
                        date1 = date2;
                    }
                    Date temp =new Date(sum / (milkList.size()-1));
                    cal.setTime(temp);
                    cal.add(Calendar.DATE,-1);
                    cal.add(Calendar.HOUR,-9);
                    cycleData.append(cycleFormat.format(cal.getTime()));
                }
            });
        }
        //주별 주기
        else if(select==2) {
            viewModel.getByDay(dayToWeekly(currDate[0]).get(0),dayToWeekly(currDate[0]).get(1)).observe(this,milkList->{
                long date1=0;
                long date2=0;
                long sum=0;
                if(milkList.size()>=2) {
                    date1 = milkList.get(0).getCurrDate().getTime();
                    for (MilkData milk : milkList) {
                        date2 = milk.getCurrDate().getTime();
                        sum += (date2 - date1);
                        date1 = date2;
                    }
                    Date temp =new Date(sum / (milkList.size()-1));
                    cal.setTime(temp);
                    cal.add(Calendar.DATE,-1);
                    cal.add(Calendar.HOUR,-9);
                    cycleData.append(cycleFormat.format(cal.getTime()));
                }
            });
        }
        //월별 주기
        else if(select==3) {
            viewModel.getByDay(dayToMonthly(currDate[0]).get(0),dayToMonthly(currDate[0]).get(1)).observe(this,milkList->{
                long date1=0;
                long date2=0;
                long sum=0;
                if(milkList.size()>=2) {
                    date1 = milkList.get(0).getCurrDate().getTime();
                    for (MilkData milk : milkList) {
                        date2 = milk.getCurrDate().getTime();
                        sum += (date2 - date1);
                        date1 = date2;
                    }
                    Date temp =new Date(sum / (milkList.size()-1));
                    cal.setTime(temp);
                    cal.add(Calendar.DATE,-1);
                    cal.add(Calendar.HOUR,-9);
                    cycleData.append(cycleFormat.format(cal.getTime()));
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

    private String recommend(String input){
        switch (input){
            case "0~1개월":
                return "60~90cc (하루 7~8회, 900cc이하)";
            case "1~2개월":
                return "120~160cc (하루 5~6회, 900cc이하)";
            case "2~3개월":
                return "120~160cc (하루 5~6회, 900cc이하)";
            case "3~4개월":
                return "160~200cc (하루 5회, 900cc이하)";
            case "4~5개월":
                return "180~200cc (하루 5회, 900cc이하)";
            case "5~8개월":
                return "200~240cc (하루 4~5회, 1000cc이하)";
            case "9~10개월":
                return "180~210cc (하루 4회, 1000cc이하)";


            case "~3.6kg":
                return "~60cc (하루 500~600cc)";
            case "~4kg":
                return "~90cc (하루 720cc)";
            case "~4.5kg":
                return "~120cc (하루 720~800cc)";
            case "~5kg":
                return "~150cc (하루 850cc)";
            case "~5.5kg":
                return "~180cc (하루 960cc)";
            case "~6.3kg":
                return "200cc (하루 1100cc)";
        }
        return null;
    }

}
