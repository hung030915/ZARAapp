package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an1.R;
import com.example.du_an1.SeeSanPham;

import java.util.List;

public class Adapter_kichco extends RecyclerView.Adapter<Adapter_kichco.ViewHolder> {
    List<String> list;
    Context context;
int i = -1;
    public Adapter_kichco(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(((Activity) context).getLayoutInflater().inflate(R.layout.item_kichco, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.ten.setText(list.get(position));
        String cam = "#FF4800";
        String vang = "#FFC107";
        if (position==i){
            SeeSanPham sanPham = (SeeSanPham) context;
            sanPham.setKichCo(list.get(i));
            holder.mau.setBackgroundColor(Color.parseColor(cam));
        }else {
            holder.mau.setBackgroundColor(Color.parseColor(vang));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ten;
        LinearLayout mau;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ten = itemView.findViewById(R.id.tv_kichco_show1);
            mau = itemView.findViewById(R.id.cv_kichco);
        }
    }
}
