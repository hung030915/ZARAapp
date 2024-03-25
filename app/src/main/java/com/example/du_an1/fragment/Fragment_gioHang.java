package com.example.du_an1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an1.R;
import com.example.du_an1.adapter.Adapter_giohang;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Fragment_gioHang extends Fragment {
    RecyclerView rcv_list;
    TextView tongGia;
    LinearLayout mua;
    Adapter_giohang adapterGiohang;
    List<GioHang> list_gio;
    List<SanPham> list_sanPham;
    List<Hang> list_hang;
    FirebaseFirestore db;
    List<Don> listMaSP;
    FirebaseUser user;
    String TAG = "TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gio_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
    }

    private void anhXa(View view) {
        list_gio = new ArrayList<>();
        list_hang = new ArrayList<>();
        list_sanPham = new ArrayList<>();
        nghe();
        rcv_list = view.findViewById(R.id.rcv_listgio);
        tongGia = view.findViewById(R.id.tv_gio_gia);
        mua = view.findViewById(R.id.ll_themgio);
        user = FirebaseAuth.getInstance().getCurrentUser();
        adapterGiohang = new Adapter_giohang(list_gio, list_sanPham, list_hang, getContext(), this);
        rcv_list.setAdapter(adapterGiohang);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_list.setLayoutManager(manager);
        tinhTong();

        mua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mua();
            }
        });

    }

    private void mua() {
        List<String> listMaGio = getListMa();
        if (listMaGio.size()<=0) {
            Toast.makeText(getContext(), "Vui lòng thêm sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
            return;
        }
        String maDon = UUID.randomUUID().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Calendar lich = Calendar.getInstance();
        int ngay = lich.get(Calendar.DAY_OF_MONTH);
        int thang = lich.get(Calendar.MONTH)+1;
        int nam = lich.get(Calendar.YEAR);
        String ngayMua = nam+"/"+thang+"/"+ngay;
        db.collection("donHang").document(maDon).set(new DonHang(maDon,user.getUid(),listMaSP,new Date().getTime(),0,tinhTong(),ngayMua))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(getContext(), "Đơn hàng đang chờ nhân viên xác nhận", Toast.LENGTH_SHORT).show();
                    guiThongBao();
                }else {
                    Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        for (String s : listMaGio){
            db.collection("gioHang").document(s).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        adapterGiohang.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Toast.makeText(getContext(), "Thành công", Toast.LENGTH_SHORT).show();
        
    }

    private void guiThongBao() {
        String id = UUID.randomUUID().toString();
        db.collection("thongBao").document(id).set(new ThongBao(id,user.getUid(),"Có đơn hàng mới của "+user.getUid(),2,new Date().getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Log.e(TAG, "onComplete: "+"Thành công" );
                }
            }
        });
    }


    private List<String> getListMa() {
        List<String> listGio = new ArrayList<>();
        listMaSP = new ArrayList<>();
        for (GioHang gh : list_gio){
            listGio.add(gh.getMaGio());
            listMaSP.add(new Don(gh.getMaSanPham(),gh.getSoLuong()));
        }
        return listGio;
    }


    private void nghe() {
        db = FirebaseFirestore.getInstance();
        ngheGio();
        ngheSP();
        ngheHang();

    }

    private void ngheHang() {
        db.collection("hang").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(Hang.class);
                                list_hang.add(dc.getDocument().toObject(Hang.class));
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Hang dtoq = dc.getDocument().toObject(Hang.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_hang.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_hang.remove(dc.getOldIndex());
                                    list_hang.add(dtoq);
                                }
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(Hang.class);
                                list_hang.remove(dc.getOldIndex());
                                adapterGiohang.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    private void ngheSP() {
        db.collection("sanPham").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(SanPham.class);
                                list_sanPham.add(dc.getDocument().toObject(SanPham.class));
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                SanPham dtoq = dc.getDocument().toObject(SanPham.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_sanPham.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_sanPham.remove(dc.getOldIndex());
                                    list_sanPham.add(dtoq);
                                }
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(SanPham.class);
                                list_sanPham.remove(dc.getOldIndex());
                                adapterGiohang.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    private void ngheGio() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("gioHang").whereEqualTo("maKhachHang", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Lỗi không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                list_gio.add(dc.getDocument().toObject(GioHang.class));
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                GioHang dtoq = dc.getDocument().toObject(GioHang.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_gio.set(dc.getOldIndex(), dtoq);
                                } else {
                                    list_gio.remove(dc.getOldIndex());
                                    list_gio.add(dtoq);
                                }
                                adapterGiohang.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(GioHang.class);
                                list_gio.remove(dc.getOldIndex());
                                adapterGiohang.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    public Long tinhTong() {
        Long tong = 0l;
        for (GioHang s : list_gio) {
            for (SanPham a : list_sanPham) {
                if (s.getMaSanPham().equals(a.getMaSp())) {
                    tong = Long.parseLong(s.getSoLuong() + "") * Long.parseLong(a.getGia() + "") + tong;
                }
            }
        }
        tongGia.setText(tong + " đ");
        return tong;
    }
}