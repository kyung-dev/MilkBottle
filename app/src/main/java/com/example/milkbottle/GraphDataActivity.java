package com.example.milkbottle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphDataActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    MyRecyclerAdapter mRecyclerAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_data);

        ArrayList<MilkData> arrayData = new ArrayList<MilkData>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //데이터를 recyclerview에 세팅
        viewModel.getAll().observe(this, allData->{
            arrayData.addAll(allData);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        });

        //액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        // 어댑터 초기화
        mRecyclerAdapter = new MyRecyclerAdapter();
        mRecyclerAdapter.setList(arrayData);

        //recyclerview 초기화
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }
}