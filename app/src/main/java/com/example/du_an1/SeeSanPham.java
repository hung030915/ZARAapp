package com.example.du_an1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.du_an1.adapter.Adapter_kichco;
import com.example.du_an1.model.GioHang;
import com.example.du_an1.model.SanPham;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class SeeSanPham extends AppCompatActivity {

    RecyclerView rcv_list;
    SanPham sanPham = new SanPham();
    ;
    List<String> list_co;
    TextView ten, gia, nam;
    ImageView anh;
    Button them, tru, cong;
    EditText hienThi;
    Adapter_kichco adapterKichco;
    FirebaseFirestore db;
    int so = 0;
    String kichCo="";

    public void setKichCo(String kichCo) {
        this.kichCo = kichCo;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanpham_show);
        Intent intent = getIntent();
        String s = intent.getStringExtra("sanpham");

        nghe(s);
        rcv_list = findViewById(R.id.rcv_listco);
        ten = findViewById(R.id.tv_tensp_show);
        gia = findViewById(R.id.tv_giasp_show);
        them = findViewById(R.id.btn_themgio);
        nam = findViewById(R.id.tv_mamsp_show);
        anh = findViewById(R.id.imv_anh_sp_lon);
        tru = findViewById(R.id.bnt_tru_soluong);
        cong = findViewById(R.id.bnt_cong_soluong);
        hienThi = findViewById(R.id.edt_soluong_show);
        hienThi.setText(so + "");
        them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (so==0){
                    Toast.makeText(SeeSanPham.this, "Bạn phải chọn ít nhất 1 sản phẩm ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (kichCo.isEmpty()){
                    Toast.makeText(SeeSanPham.this, "Hãy chọn kích cỡ", Toast.LENGTH_SHORT).show();
                    return;
                }
                themGio();
            }
        });

        tru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinh("-");
            }
        });
        cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinh("+");
            }
        });

    }

    private void themGio() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String maGio = UUID.randomUUID()+"";
        db.collection("gioHang").document(maGio).
                set(new GioHang(maGio, user.getUid(), sanPham.getMaSp(),kichCo, Long.parseLong(so + "")))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Toast.makeText(SeeSanPham.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SeeSanPham.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void tinh(String dau) {
        if ("-".equals(dau)) {
            so -= 1;
            if (so == -1) {
                so = 0;
            }
        } else {
            so += 1;
        }
        hienThi.setText(so + "");
    }


    private void nghe(String s) {
        db = FirebaseFirestore.getInstance();
        db.collection("sanPham").document(s).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    Log.e("TAG", "onComplete: " + task.getResult().toObject(SanPham.class).getGia());
                    sanPham.setAnh(task.getResult().toObject(SanPham.class).getAnh());
                    sanPham.setMaSp(task.getResult().toObject(SanPham.class).getMaSp());
                    sanPham.setGia(task.getResult().toObject(SanPham.class).getGia());
                    sanPham.setTenSP(task.getResult().toObject(SanPham.class).getTenSP());
                    sanPham.setKichCo(task.getResult().toObject(SanPham.class).getKichCo());
                    sanPham.setNamSX(task.getResult().toObject(SanPham.class).getNamSX());
                    Log.e("TAG", "onComplete: " + sanPham.getKichCo().get(0));
                    ten.setText(sanPham.getTenSP());
                    nam.setText("Năm sản xuất: " + sanPham.getNamSX());
                    gia.setText("Giá: " + sanPham.getGia());
                    Glide.with(SeeSanPham.this).load(sanPham.getAnh()).error(R.drawable.baseline_crop_original_24).into(anh);
                    list_co = sanPham.getKichCo();
                    adapterKichco = new Adapter_kichco(list_co, SeeSanPham.this);
                    rcv_list.setAdapter(adapterKichco);
                    LinearLayoutManager manager = new LinearLayoutManager(SeeSanPham.this, LinearLayoutManager.HORIZONTAL, false);
                    rcv_list.setLayoutManager(manager);
                } else {
                    Toast.makeText(SeeSanPham.this, "Sản phẩm đã bị xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}