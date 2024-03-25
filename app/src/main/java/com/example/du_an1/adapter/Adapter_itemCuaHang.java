package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.R;
import com.example.du_an1.SeeSanPham;
import com.example.du_an1.model.SanPham;

import java.util.List;

public class Adapter_itemCuaHang extends RecyclerView.Adapter<Adapter_itemCuaHang.ViewHolder> {
    List<SanPham> list;
    Context context;

    public Adapter_itemCuaHang(List<SanPham> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(((Activity) context).getLayoutInflater()
                .inflate(R.layout.item_cuahang_sanpham, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getAnh()).
                error(R.drawable.baseline_crop_original_24).into(holder.anh);
        holder.ten.setText(list.get(position).getTenSP());
        holder.gia.setText("Giá: " + list.get(position).getGia());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SeeSanPham.class);
                intent.putExtra("sanpham",list.get(position).getMaSp());
                ((Activity)context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView anh;
        TextView ten, gia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            anh = itemView.findViewById(R.id.imv_anh_sp_cuahang);
            ten = itemView.findViewById(R.id.tv_tensp_cuahang);
            gia = itemView.findViewById(R.id.tv_giasp_cuahang);
        }
    }
}
