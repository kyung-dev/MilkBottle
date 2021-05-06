package com.example.milkbottle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.room.TypeConverter;

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

public class MainActivity extends AppCompatActivity {

    Button beforeBtn;
    Button afterBtn;
    Button recordBtn; //데이터 삽입버튼
    Button deleteBtn; //데이터 삭제버튼

    EditText beforeTxt;
    EditText afterTxt;
    TextView test;

    Button graphBtn;

    int befoData, afterData, quantity;
    Date date;
    Date currDate;
    Float currFloat;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);



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


        graphBtn = (Button) findViewById(R.id.graph);
        graphBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(intent);
        });

        test.setMovementMethod(ScrollingMovementMethod.getInstance());

        //버튼 클릭 시 DB에 insert
        recordBtn.setOnClickListener(v -> {
            befoData= Integer.parseInt(beforeTxt.getText().toString());
            afterData = Integer.parseInt(afterTxt.getText().toString());
            currDate = new Date(System.currentTimeMillis());
            currFloat = (float)System.currentTimeMillis();
            quantity = befoData - afterData;
            viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));
        });

        deleteBtn.setOnClickListener(v -> {
            viewModel.deleteAll(new MilkData(befoData, afterData, quantity, currDate, currFloat));
        });
    }

}