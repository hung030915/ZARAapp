package com.example.du_an1.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.R;
import com.example.du_an1.model.SanPham;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class Adapter_Top10 extends RecyclerView.Adapter<Adapter_Top10.viewHolder> {

    Context context;
    List<SanPham> list_sanPham;
    FirebaseFirestore db;
    List<HashMap<String, Object>> list_top10;

    public Adapter_Top10(Context context, List<SanPham> list_sanPham, List<HashMap<String, Object>> list_top10) {
        this.context = context;
        this.list_sanPham = list_sanPham;
        this.list_top10 = list_top10;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_top10sp, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SanPham sanPham = maSP(list_top10.get(position).get("maSP").toString());
        Log.e("TAG",""+sanPham.getAnh());
        if (sanPham==null){
            return;
        }
        Log.e("TAG",""+sanPham.getTenSP());
        Glide.with(context).load(sanPham.getAnh()).error(R.drawable.baseline_crop_original_24).into(holder.anh);
        holder.tv_tenSp.setText("Tên sản phẩm: "+sanPham.getTenSP());
        holder.tv_thuonghieu.setText("Hãng: "+sanPham.getTenHang());
        holder.tv_soLuong.setText("Số lượng: "+list_top10.get(position).get("soLuong").toString());
    }

    public SanPham maSP(String masp) {
        SanPham sanPham = new SanPham();
        for (SanPham sp : list_sanPham) {
            if (masp.equals(sp.getMaSp())) {
                return sp;
            }

        }
        return sanPham;
    }


    @Override
    public int getItemCount() {
        return list_top10.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView anh;
        TextView tv_tenSp, tv_thuonghieu, tv_soLuong;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            anh = itemView.findViewById(R.id.imgv_anhtop10);
            tv_tenSp = itemView.findViewById(R.id.tv_TENSP);
            tv_thuonghieu = itemView.findViewById(R.id.tv_THUONGHIEU);
            tv_soLuong = itemView.findViewById(R.id.tv_SOLUONG);
        }
    }
}
