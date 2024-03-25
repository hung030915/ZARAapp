package com.example.du_an1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an1.ManHinhKhachHang;
import com.example.du_an1.R;
import com.example.du_an1.adapter.Adapter_cuahang;
import com.example.du_an1.model.Hang;
import com.example.du_an1.model.SanPham;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Frag_cuahang extends Fragment {
    private RecyclerView rcv_list;
    private Adapter_cuahang adapterCuahang;
    private FirebaseFirestore db;
    List<Hang> list_hang;
    List<SanPham> list_sp;
    String TAG = "TAG";
    ManHinhKhachHang manHinhKhachHang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_cuahang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
    }

    private void anhXa(View view) {
        rcv_list = view.findViewById(R.id.rcv_cuaHang);
        list_hang = new ArrayList<>();
        list_sp = new ArrayList<>();
        manHinhKhachHang = (ManHinhKhachHang) getActivity();
        nghe();
        adapterCuahang = new Adapter_cuahang(list_hang, getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_list.setAdapter(adapterCuahang);
        rcv_list.setLayoutManager(manager);



    }

    private void nghe() {
        db = FirebaseFirestore.getInstance();
        db.collection("hang").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value == null) {
                    return;
                }
                list_sp.clear();
                list_hang.clear();
                for (DocumentSnapshot dc : value.getDocuments()) {
                    AddListSP(dc.getString("maHang"), dc.getString("tenHang"));
                }

            }
        });
    }

    private void AddListSP(String maHang, String name) {
        db.collection("sanPham").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value == null) {
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (maHang.equals(dc.getDocument().get("maHang").toString())) {
                        //lấy được sản phẩm
                        list_sp.add(dc.getDocument().toObject(SanPham.class));
                    }
                    switch (dc.getType()) {
                        case MODIFIED:
//                            manHinhKhachHang.getSupportFragmentManager().beginTransaction().replace(R.id.fcv_KhachHang, new Frag_cuahang()).commit();
                            return;
                    }
                }
                Log.e(TAG, "onEvent:2 " + list_sp.size());
                list_hang.add(new Hang(maHang, name, list_sp));
                list_hang.sort(new Comparator<Hang>() {
                    @Override
                    public int compare(Hang o1, Hang o2) {
                        return (o2.getSanPham().size() - o1.getSanPham().size());
                    }
                });
                adapterCuahang.notifyDataSetChanged();
                list_sp = new ArrayList<>();
            }
        });
        for (Hang h : list_hang) {
            if (h.getSanPham().size() <= 0) {
                list_hang.remove(h);
                adapterCuahang.notifyDataSetChanged();
            }
        }
    }

}
