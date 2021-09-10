package com.example.milkbottle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private ArrayList<MilkData> dataList;

    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(dataList.get(position));
    }

    public void setList(ArrayList<MilkData> list){
        this.dataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.d("데이터크기"," : "+dataList.size());
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView quantity;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quantity = (TextView) itemView.findViewById(R.id.quantity);
            date = (TextView) itemView.findViewById(R.id.date);
        }

        void onBind(MilkData item){
            quantity.setText(Float.toString(item.getQuantity())+"cc");
            date.setText(item.getDate());
        }
    }
}
