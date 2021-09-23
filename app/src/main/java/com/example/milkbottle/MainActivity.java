package com.example.milkbottle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    //컴포넌트언,변수 선언
    Button menuBtn;
    Button beforeBtn;
    Button afterBtn;
    Button recordBtn; //데이터 삽입버튼
    EditText beforeTxt;
    EditText afterTxt;
    TextView lateQuan;
    BluetoothSPP bt;
    float befoData, afterData, quantity; //DB저장변수
    Date currDate;
    Float currFloat;
    long lateMilk;

    //알람관련 변수
    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;


    //DB 데이터 저장 테스트
    public void test(){
        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Calendar cal = Calendar.getInstance();
        cal.set(2021,1,10,3,49,47);
        Date day = cal.getTime();

        befoData = (float)  0.10;
        afterData = (float) 0.05;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

        cal.set(2021,2,11,8,49,47);
        day = cal.getTime();

        befoData = (float)  0.15;
        afterData = (float) 0.04;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

        cal.set(2021,3,12,11,49,47);
        day = cal.getTime();

        befoData = (float)  0.18;
        afterData = (float) 0.03;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션 바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

//        test();

        //컴포넌트, 변수 초기화
        bt = new BluetoothSPP(this);
        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        menuBtn = (Button) findViewById(R.id.menu);
        beforeBtn = (Button) findViewById(R.id.beforVal);
        afterBtn = (Button) findViewById(R.id.afterVal);
        recordBtn = (Button) findViewById(R.id.recode);
        beforeTxt = (EditText) findViewById(R.id.avgVal);
        afterTxt = (EditText) findViewById(R.id.afterData);
        lateQuan = (TextView) findViewById(R.id.lateQuan);
        lateMilk=0;

        //알림 관련 변수 초기화
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
        mCalender = new GregorianCalendar();
        Log.d("0912", mCalender.getTime().toString());

        //최근 분유 데이터 세팅
        setLateQuan(viewModel);

        //메뉴버튼
        menuBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(getApplicationContext(),v);

                getMenuInflater().inflate(R.menu.option_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.name1: //블루투스
                                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) { // 현재 버튼의 상태에 따라 연결이 되어있으면 끊고, 반대면 연결
                                    bt.disconnect();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                                }
                                break;
                            case R.id.name2: //그래프화면 전환
                                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.name3: // 통계화면 전환
                                Intent intent2 = new Intent(getApplicationContext(), StatsActivity.class);
                                //통계화면 날짜버튼 세팅값
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = new Date();
                                String time = dateFormat.format(date);
                                intent2.putExtra("date",time);
                                startActivity(intent2);
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        //기록 버튼 클릭 시 DB에 insert
        recordBtn.setOnClickListener(v -> {
            befoData= Float.parseFloat(beforeTxt.getText().toString());
            afterData = Float.parseFloat(afterTxt.getText().toString());
            currDate = new Date(System.currentTimeMillis());
            currFloat = (float)System.currentTimeMillis();
            quantity = befoData - afterData;
            viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));
            Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            setLateQuan(viewModel);

        });

//        deleteBtn.setOnClickListener(v -> {
//            viewModel.deleteAll(new MilkData(befoData, afterData, quantity, currDate, currFloat));
//        });


        //먹기 전 분유량 가져오기
        beforeBtn.setOnClickListener(v -> {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                public void onDataReceived(byte[] data, String message) {
                    beforeTxt.setText(message);
                }
            });
        });

        //먹은 후 분유량 가져오기
        afterBtn.setOnClickListener(v -> {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                public void onDataReceived(byte[] data, String message) {
                    afterTxt.setText(message);
                }
            });
        });

        //블루투스 연결 관련
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가라면
            // 사용불가라고 토스트 띄워줌
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            // 화면 종료
            finish();
        }

        // 데이터를 받았는지 감지하는 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            //데이터 수신되면
            public void onDataReceived(byte[] data, String message) {
            }
        });

        // 블루투스가 잘 연결이 되었는지 감지하는 리스너
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setLateQuan(MainViewModel viewModel){
        //최근분유섭취량 세팅
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd일 HH시간 mm분");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH시간 mm분");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secFormat = new SimpleDateFormat("ss");

        viewModel.latedata().observe(this, late->{
            Calendar cal = Calendar.getInstance();
            Calendar predict = Calendar.getInstance(); // 다음 분유 알림 시간

            Date day1 = cal.getTime(); //현재 시간
            Date day2 = late.get(0).getCurrDate(); //최근 분유 섭취시간
            Date day3 = late.get(1).getCurrDate(); //최근 직전 분유 섭취시간
            Date lateQuanSet = null;
            Date alarmSet = null;
            long date1=0;
            long date2=0;
            long date3=0;
            lateMilk=day2.getTime();

            date1 = day1.getTime();
            date2 = day2.getTime();
            date3 = day3.getTime();
            lateQuanSet = new Date(date1-date2);
            alarmSet = new Date(date3-date2);

            predict.setTime(alarmSet);
            predict.add(Calendar.DATE,Integer.parseInt(dayFormat.format(cal.getTime())));
            predict.add(Calendar.HOUR,Integer.parseInt(hourFormat.format(cal.getTime())));
            predict.add(Calendar.MINUTE,Integer.parseInt(minFormat.format(cal.getTime())));
            predict.add(Calendar.SECOND,Integer.parseInt(secFormat.format(cal.getTime())));

            cal.setTime(lateQuanSet);
//            cal.add(Calendar.DATE,1);
            cal.add(Calendar.HOUR,-9);

            //알림 시간 세팅
            setAlarm(predict);

            //최근분유시간 세팅
            if(day2.getDay()==day1.getDay())
                lateQuan.setText(dateFormat2.format(cal.getTime())+"전 "+Float.toString(late.get(0).getQuantity())+"cc");
            else
                lateQuan.setText(dateFormat.format(cal.getTime())+"전 "+Float.toString(late.get(0).getQuantity())+"cc");
        });
    }

    public void setAlarm(Calendar predictTime){
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(MainActivity.this, AlarmRecevier.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, 0);
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.SECOND,10);
        alarmManager.set(AlarmManager.RTC, predictTime.getTimeInMillis(), pendingIntent);

    }
    // 앱 중단시 (액티비티 나가거나, 특정 사유로 중단시)
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }
    // 앱이 시작하면
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { // 앱의 상태를 보고 블루투스 사용 가능하면
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 새로운 액티비티 띄워줌, 거기에 현재 가능한 블루투스 정보 intent로 넘겨
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) { // 블루투스 사용 불가
                // setupService() 실행하도록
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기끼리
                // 셋팅 후 연결되면 setup()으로
            }
        }
    }

    // 새로운 액티비티 (현재 액티비티의 반환 액티비티?)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 아까 응답의 코드에 따라 연결 가능한 디바이스와 연결 시도 후 ok 뜨면 데이터 전송
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) { // 연결시도
            if (resultCode == Activity.RESULT_OK) // 연결됨
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) { // 연결 가능
            if (resultCode == Activity.RESULT_OK) { // 연결됨
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else { // 사용불가
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}