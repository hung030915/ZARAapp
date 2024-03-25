package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.du_an1.R;
import com.example.du_an1.ShowMore;
import com.example.du_an1.model.Hang;
import com.example.du_an1.model.SanPham;

import java.util.List;

public class Adapter_cuahang extends RecyclerView.Adapter<Adapter_cuahang.ViewHolder> {
    List<Hang> list;
    Context context;
    Adapter_itemCuaHang itemCuaHang;

    public Adapter_cuahang(List<Hang> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(((Activity) context).getLayoutInflater().inflate(R.layout.item_cuahang, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (list.get(position).getSanPham().size()==0){
            return;
        }
        holder.tenHang.setText(list.get(position).getTenHang());
        itemCuaHang=new Adapter_itemCuaHang(list.get(position).getSanPham(),context);
        holder.rcv_list.setAdapter(itemCuaHang);
        LinearLayoutManager manager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.rcv_list.setLayoutManager(manager);
        holder.xemthem.setText("Xem thêm");
        holder.xemthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowMore.class);
                List<SanPham> phamList = list.get(position).getSanPham();
                String [] s = new String[]{list.get(position).getMaHang(),list.get(position).getTenHang()};
                intent.putExtra("list",  s);
                ((Activity)context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tenHang;
        TextView xemthem;
        RecyclerView rcv_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenHang = itemView.findViewById(R.id.tv_tenhang);
            xemthem = itemView.findViewById(R.id.ll_xemthem_moi);
            rcv_list = itemView.findViewById(R.id.rcv_list_sp_khach);
        }
    }
}
