package com.example.du_an1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.du_an1.adapter.Adapter_quanlyhoadon;
import com.example.du_an1.model.DonHang;
import com.example.du_an1.model.GioHang;
import com.example.du_an1.model.SanPham;
import com.example.du_an1.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class loatDonHang extends AppCompatActivity {

    FirebaseFirestore db;
    List<DonHang> list_dh = new ArrayList<>();
    List<User> list_User = new ArrayList<>();
    List<GioHang> list_GioHang = new ArrayList<>();
    List<SanPham> list_sp = new ArrayList<>();

    Adapter_quanlyhoadon adapterQuanlyhoadon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loat_don_hang);
        adapterQuanlyhoadon = new Adapter_quanlyhoadon( list_sp, list_User, list_dh, loatDonHang.this);
        ngheGio();
        ngheKH();
        ngheHoaDon();
        ngheSP();

    }

    public void ngheHoaDon() {
        db = FirebaseFirestore.getInstance();
        db.collection("donHang").whereEqualTo("trangThai", 0).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(loatDonHang.this, "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(DonHang.class);
                                list_dh.add(dc.getDocument().toObject(DonHang.class));
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                DonHang dtoq = dc.getDocument().toObject(DonHang.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_dh.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_dh.remove(dc.getOldIndex());
                                    list_dh.add(dtoq);
                                }
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(DonHang.class);
                                list_dh.remove(dc.getOldIndex());
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    public void ngheKH() {
        db = FirebaseFirestore.getInstance();
        db.collection("user").whereEqualTo("chucVu", 3).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Log.e("TAG", "onEvent: " + dc.getType());
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(User.class);
                                list_User.add(dc.getDocument().toObject(User.class));
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                User user1 = dc.getDocument().toObject(User.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_User.set(dc.getOldIndex(), user1);
                                    adapterQuanlyhoadon.notifyDataSetChanged();
                                } else {
                                    list_User.remove(dc.getOldIndex());
                                    list_User.add(user1);
                                    adapterQuanlyhoadon.notifyDataSetChanged();
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(User.class);
                                list_User.remove(dc.getOldIndex());
                                adapterQuanlyhoadon.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void ngheSP() {
        db = FirebaseFirestore.getInstance();
        db.collection("sanPham").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(loatDonHang.this, "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(SanPham.class);
                                list_sp.add(dc.getDocument().toObject(SanPham.class));
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                SanPham dtoq = dc.getDocument().toObject(SanPham.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_sp.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_sp.remove(dc.getOldIndex());
                                    list_sp.add(dtoq);
                                }
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(SanPham.class);
                                list_sp.remove(dc.getOldIndex());
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    public void ngheGio() {
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("gioHang").whereEqualTo("maKhachHang", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(loatDonHang.this, "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                list_GioHang.add(dc.getDocument().toObject(GioHang.class));
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                GioHang dtoq = dc.getDocument().toObject(GioHang.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_GioHang.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_GioHang.remove(dc.getOldIndex());
                                    list_GioHang.add(dtoq);
                                }
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(GioHang.class);
                                list_GioHang.remove(dc.getOldIndex());
                                adapterQuanlyhoadon.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

}