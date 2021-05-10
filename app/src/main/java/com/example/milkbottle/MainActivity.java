package com.example.milkbottle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.room.TypeConverter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    Button beforeBtn;
    Button afterBtn;
    Button recordBtn; //데이터 삽입버튼
    Button deleteBtn; //데이터 삭제버튼

    EditText beforeTxt;
    EditText afterTxt;
    TextView test;
    TextView bluetoothTest;

    Button graphBtn;
    Button bluetoothBtn;

    float befoData, afterData, quantity;
    Date date;
    Date currDate;
    Float currFloat;

    BluetoothSPP bt;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        bt = new BluetoothSPP(this);


        //UI 갱신
       viewModel.getAll().observe(this, milkData -> {
            test.setText(milkData.toString());
        });

        beforeBtn = (Button) findViewById(R.id.beforVal);
        afterBtn = (Button) findViewById(R.id.afterVal);
        recordBtn = (Button) findViewById(R.id.recode);
        deleteBtn = (Button) findViewById(R.id.delete);
        beforeTxt = (EditText) findViewById(R.id.beforData);
        afterTxt = (EditText) findViewById(R.id.afterData);
        test = (TextView) findViewById(R.id.test);
        bluetoothTest = (TextView) findViewById(R.id.textView);

        graphBtn = (Button) findViewById(R.id.graph);
        graphBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(intent);
        });

        bluetoothBtn = (Button) findViewById(R.id.bluetooth);
        bluetoothBtn.setOnClickListener(v -> {




                });


        test.setMovementMethod(ScrollingMovementMethod.getInstance());

        //버튼 클릭 시 DB에 insert
        recordBtn.setOnClickListener(v -> {
            befoData= Float.parseFloat(beforeTxt.getText().toString());
            afterData = Float.parseFloat(afterTxt.getText().toString());
            currDate = new Date(System.currentTimeMillis());
            currFloat = (float)System.currentTimeMillis();
            quantity = befoData - afterData;
            viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));
        });

        deleteBtn.setOnClickListener(v -> {
            viewModel.deleteAll(new MilkData(befoData, afterData, quantity, currDate, currFloat));
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신

            public void onDataReceived(byte[] data, String message) {
                bluetoothTest.setText(message);

            }
        });


        //먹기 전 분유량 가져오기
        beforeBtn.setOnClickListener(v -> {
            Log.d("bluetoothTest","afterData-1");

            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                public void onDataReceived(byte[] data, String message) {
                    Log.d("bluetoothTest","afterData-2");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.d("bluetoothTest","beforeData-"+message);
                    beforeTxt.setText(message);
                }
            });
            Log.d("bluetoothTest","afterData-3");
        });
        Log.d("bluetoothTest","afterData-");

        //먹은 후 분유량 가져오기
        afterBtn.setOnClickListener(v -> {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                public void onDataReceived(byte[] data, String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    afterTxt.setText(message);
                    Log.d("bluetoothTest","afterData-"+message);

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
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show(); // 토스트로 데이터 띄움
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

        bluetoothBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) { // 현재 버튼의 상태에 따라 연결이 되어있으면 끊고, 반대면 연결
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
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
                setup();
            }
        }
    }
    // 블루투스 사용 - 데이터 전송
    public void setup() {
        Button btnSend = findViewById(R.id.dataSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
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
                setup();
            } else { // 사용불가
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}