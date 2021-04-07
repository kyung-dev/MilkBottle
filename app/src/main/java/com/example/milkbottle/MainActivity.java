package com.example.milkbottle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button beforeBtn;
    Button afterBtn;
    Button recordBtn; //데이터 삽입버튼

    EditText beforeTxt;
    EditText afterTxt;
    TextView test;

    Button graphBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "milk-db")
                .allowMainThreadQueries()   //여기서 db는 무조건 백그라운드로 동작하지 않으면 에러가 뜨기 때문에 간단하게 작성하기 위해 이 구문 삽입
                .build();                   //(main thread에서 db저장해도 이상 없도록 하는 코드)


        beforeBtn = (Button) findViewById(R.id.beforVal);
        afterBtn = (Button) findViewById(R.id.afterVal);
        recordBtn = (Button) findViewById(R.id.recode);
        beforeTxt = (EditText) findViewById(R.id.beforData);
        afterTxt = (EditText) findViewById(R.id.afterData);
        test = (TextView) findViewById(R.id.test);

        graphBtn = (Button) findViewById(R.id.graph);
        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(intent);
            }
        });

        test.setText(db.milkDao().getAll().toString());

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.milkDao().insert(new MilkData(beforeTxt.getText().toString()));
                test.setText(db.milkDao().getAll().toString());

            }
        });

    }

}