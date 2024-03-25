package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.R;
import com.example.du_an1.fragment.Fragment_gioHang;
import com.example.du_an1.model.Don;
import com.example.du_an1.model.DonHang;
import com.example.du_an1.model.GioHang;
import com.example.du_an1.model.Hang;
import com.example.du_an1.model.SanPham;
import com.example.du_an1.model.ThongBao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Adapter_giohang extends RecyclerView.Adapter<Adapter_giohang.ViewHolder> {
    List<GioHang> list_gio;
    List<SanPham> list_sanPham;
    List<Hang> list_hang;
    Context context;
    Fragment_gioHang gioHang;
    FirebaseFirestore db;
    FirebaseUser user ;

    public Adapter_giohang(List<GioHang> list_gio, List<SanPham> list_sanPham, List<Hang> list_hang, Context context, Fragment_gioHang gioHang) {
        this.list_gio = list_gio;
        this.list_sanPham = list_sanPham;
        this.list_hang = list_hang;
        this.context = context;
        this.gioHang = gioHang;
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(((Activity) context).getLayoutInflater().
                inflate(R.layout.item_giohang, parent, false));
    }
//    private String laylink(String maSP) {
//        String link = "";
//        for (SanPham s : list_sanPham) {
//            if (maSP.equals(s.getMaSp())) {
//                link = s.getAnh();
//                return link;
//            }
//        }
//        return link;
//    }
    private String laylink(String maSP) {
        String link = "";
        for (SanPham s : list_sanPham) {
            if (maSP.equals(s.getMaSp())) {
                link = s.getAnh();
                return link;
            }
        }
        return link;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String link = laylink(list_gio.get(position).getMaSanPham());
        gioHang.tinhTong();
        if (link.isEmpty()) {
            return;
        }
        Glide.with(context).load(link).
                error(R.drawable.baseline_crop_original_24).into(holder.anh);
        SanPham sp = getSanPham(list_gio.get(position).getMaSanPham());
        if (sp == null) {
            return;
        }
        holder.tenSP.setText(sp.getTenSP());
        holder.giaSP.setText("Giá: " + sp.getGia() + " đ");
        String tenHang = getTenLoai(sp.getMaHang());
        if (tenHang == null) {
            return;
        }
        holder.loaiSP.setText("Loại: " + tenHang + "");
        holder.soLuong.setText("Số lượng: " + list_gio.get(position).getSoLuong() + "");
        holder.kichCo.setText("Kích cỡ: " + list_gio.get(position).getKichCo() + "");


        holder.xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xoa(list_gio.get(position).getMaGio());
            }
        });

        holder.mua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                them(position);
            }
        });

    }

    private void them(int p) {
        List<Don> listDon = new ArrayList<>();
        listDon.add(new Don(list_gio.get(p).getMaSanPham(),list_gio.get(p).getSoLuong()));
        String maDon = UUID.randomUUID().toString();
        Calendar lich = Calendar.getInstance();
        int ngay = lich.get(Calendar.DAY_OF_MONTH);
        int thang =lich.get(Calendar.MONTH)+1;
        int nam = lich.get(Calendar.YEAR);
        String ngayMua = nam+"/"+thang+"/"+ngay;
        db.collection("donHang").document(maDon).set(new DonHang(maDon,user.getUid(),listDon,new Date().getTime(),0,TongGiaSP(p),ngayMua))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(context, "Đơn hàng đang chờ nhân viên xác nhận", Toast.LENGTH_SHORT).show();
                    xoa(list_gio.get(p).getMaGio());
                }else {
                    Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Long TongGiaSP(int p) {
        for (SanPham g : list_sanPham){
            if (list_gio.get(p).getMaSanPham().equals(g.getMaSp())){
                return (list_gio.get(p).getSoLuong()*g.getGia());
            }
        }
        return 0l;
    }

    private void xoa(String maGio) {
        db.collection("gioHang").document(maGio).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                    guiThongBao();
                }else {
                    Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void guiThongBao() {
        String id = UUID.randomUUID().toString();
        db.collection("thongBao").document(id).set(new ThongBao(id,user.getUid(),"Có đơn hàng mới của "+user.getUid(),2,new Date().getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                }
            }
        });
    }
    private String getTenLoai(String maHang) {
        for (Hang s : list_hang) {
            if (maHang.equals(s.getMaHang())) {
                return s.getTenHang();
            }
        }
        return null;
    }

    private SanPham getSanPham(String maSP) {
        for (SanPham s : list_sanPham) {
            if (maSP.equals(s.getMaSp())) {
                return s;
            }
        }
        return null;
    }



    @Override
    public int getItemCount() {
        return list_gio.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView anh;
        TextView tenSP, loaiSP, kichCo, soLuong, giaSP, mua, xoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            anh = itemView.findViewById(R.id.imv_anh_sp_gioHang);
            tenSP = itemView.findViewById(R.id.tv_tensp_gioHang);
            giaSP = itemView.findViewById(R.id.tv_giasp_giohang);
            loaiSP = itemView.findViewById(R.id.tv_thuonghieu_gioHang);
            kichCo = itemView.findViewById(R.id.tv_kichcosp_giohang);
            soLuong = itemView.findViewById(R.id.tv_soluongsp_giohang);
            mua = itemView.findViewById(R.id.tv_mua_giohang);
            xoa = itemView.findViewById(R.id.tv_xoa_giohang);
        }
    }
}
