package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.R;
import com.example.du_an1.fragment.QuanLyGiay;
import com.example.du_an1.model.Hang;
import com.example.du_an1.model.SanPham;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Adapter_sanpham extends RecyclerView.Adapter<Adapter_sanpham.ViewDolder>{
List<SanPham> list;
Context context;
QuanLyGiay quanLyGiay;
    Dialog dialog;
    private EditText thuongHieu,ten,gia,list_kichco,namSX,soLuong,edt_hang;
    private ImageView anh;
    private SanPham sanPham;
    String id="",linkImage="";
    private Adapter_hang adapter;
    private int change = 0;
    int quyen = 0;
    FirebaseFirestore db;
    public Adapter_sanpham(List<SanPham> list, Context context, QuanLyGiay quanLyGiay, int i) {
        this.list = list;
        this.context = context;
        this.quanLyGiay = quanLyGiay;
        quyen = i;
    }

    @NonNull
    @Override
    public ViewDolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        return new ViewDolder(inflater.inflate(R.layout.item_quan_ly_giay,parent,false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewDolder holder, int position) {
        Glide.with(context).load(list.get(position).getAnh()).error(R.drawable.logo3).into(holder.anh);
        holder.ten.setText(list.get(position).getTenSP());
        holder.gia.setText("Giá: "+Integer.parseInt(list.get(position).getGia()+"")+" VND");
        holder.soluong.setText("Số lượng: "+Integer.parseInt(list.get(position).getSoLuong()+"")+"");
        if (quyen==1){
            holder.delete.setVisibility(View.GONE);
            holder.update.setVisibility(View.GONE);
        }
        db = FirebaseFirestore.getInstance();
        db.collection("hang").document(list.get(position).getMaHang())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()==false){
                            return;
                        }
                        holder.thuonghieu.setText("Thương hiệu: "+documentSnapshot.getString("tenHang"));
                    }
                });

        holder.namxuatban.setText("Năm sản xuất: "+list.get(position).getNamSX());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Cảnh báo").setIcon(R.drawable.cancel).setMessage("Nếu bạn xác nhận dữ liệu sẽ mất mãi mãi");
                builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        db.collection("sanPham").document(list.get(position).getMaSp()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           if (task.isComplete()){
                               Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                           }else {
                               Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                           }
                            }
                        });
                    }
                });
                builder1.create().show();
            }
        });

holder.update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    Update(position);
    }
});
    }

    private void Update(int position) {
        SanPham sanPham = list.get(position);
        sanPham.tenHang(quanLyGiay);
        quanLyGiay.them("Sửa sản phẩm",list.get(position).getMaSp(),sanPham,"Sửa","Sửa thành công");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewDolder extends RecyclerView.ViewHolder {
ImageView anh;
TextView ten,gia,soluong,thuonghieu,namxuatban;
ImageButton update,delete;
        public ViewDolder(@NonNull View itemView) {
            super(itemView);
            anh = itemView.findViewById(R.id.imv_anhsp);
            ten = itemView.findViewById(R.id.tv_Tensp);
            gia = itemView.findViewById(R.id.tv_Gia);
            soluong = itemView.findViewById(R.id.tv_Soluong);
            thuonghieu = itemView.findViewById(R.id.tv_Thuonghieu);
            namxuatban = itemView.findViewById(R.id.tv_Namsx);
            update = itemView.findViewById(R.id.ibtn_update);
            delete = itemView.findViewById(R.id.ibtn_delete);
        }
    }
    List<Hang> list_Hang;




}
