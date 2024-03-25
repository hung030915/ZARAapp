package com.example.du_an1.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.ManHinhAdmin;
import com.example.du_an1.R;
import com.example.du_an1.adapter.Adapter_hang;
import com.example.du_an1.adapter.Adapter_sanpham;
import com.example.du_an1.model.Hang;
import com.example.du_an1.model.SanPham;
import com.example.du_an1.model.ThuongHieu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class QuanLyGiay extends Fragment {

    private ImageButton ibtn_them;
    private Adapter_sanpham adapterSanpham;
    private List<SanPham> list_giay;
    private FirebaseFirestore db;
    private ImageView anh;
    private String linkImage = "";
    private ProgressDialog progressDialog;
    private int quyen = 0;

    public QuanLyGiay(int i) {
        quyen = i;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhxa(view);
        ibtn_them.setOnClickListener(v -> {
            id = UUID.randomUUID().toString();
            sanPham = new SanPham();
            them("Thêm sản phẩm", id, sanPham, "Thêm", "Thêm thành công");
        });

    }

    private SanPham sanPham;

    EditText a;


    public void setA(String ad) {
        a.setText(ad);
    }

    @SuppressLint({"UseRequireInsteadOfGet", "SetTextI18n"})
    public void them(String name, String id, SanPham sanPham, String tennut, String thongBao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        @SuppressLint("UseRequireInsteadOfGet") LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_themsp, null, false);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
        EditText thuongHieu, ten, gia, list_kichco, namSX, soLuong;
        TextView tittle = view.findViewById(R.id.tv_themsanpham);
        tittle.setText(name);
        anh = view.findViewById(R.id.imv_addAnh);
        ten = view.findViewById(R.id.edt_tensp);
        thuongHieu = view.findViewById(R.id.edt_tenthuongHieu);
        a = thuongHieu;
        gia = view.findViewById(R.id.edt_giaSP);
        list_kichco = view.findViewById(R.id.rcv_kichco);
        namSX = view.findViewById(R.id.edt_namSX);
        soLuong = view.findViewById(R.id.edt_soLuong);
        Button them = view.findViewById(R.id.btn_themSP);
        list_hang = new ArrayList<>();
        adapter = new Adapter_hang(list_hang, getContext());
        ngheHang(list_hang);
        if (sanPham == null) {
            return;
        }
        if (sanPham.getTenSP() != null && sanPham.getAnh() != null && sanPham.getKichCo() != null && sanPham.getGia() != null
                && sanPham.getNamSX() != null && sanPham.getSoLuong() != null && list_hang != null
        ) {
            linkImage = sanPham.getAnh();
            Glide.with(getContext()).load(sanPham.getAnh()).error(R.drawable.baseline_crop_original_24).into(anh);
            ten.setText(sanPham.getTenSP());
            thuongHieu.setText(sanPham.getTenHang());
            gia.setText(sanPham.getGia() + "");
            list_kichco.setText(hienCo(sanPham.getKichCo()));
            soLuong.setText(sanPham.getSoLuong() + "");
            namSX.setText(sanPham.getNamSX());
        }

        them.setText(tennut);
        anh.setOnClickListener(v -> {
            ManHinhAdmin admin = (ManHinhAdmin) getActivity();
            admin.yeucauquyen(getContext());
        });
        thuongHieu.setOnClickListener(v -> addHang(thuongHieu, sanPham));

        them.setOnClickListener(v -> {
            progressDialog.show();
            if (ten.getText().toString().isEmpty() || thuongHieu.getText().toString().isEmpty()
                    || gia.getText().toString().isEmpty() || list_kichco.getText().toString().isEmpty()
                    || namSX.getText().toString().isEmpty() || soLuong.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
            if (linkImage.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng thêm ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
            if (!list_kichco.getText().toString().trim().matches("[0-9,]+")) {
                Toast.makeText(getContext(), "Bạn không được viết gì khác ngoài số và dấu phẩy", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
            upAnh(Uri.parse(linkImage), ten, gia, namSX, soLuong, list_kichco, dialog, id, sanPham, thongBao);

        });
    }

    private String hienCo(List<String> list) {
        String ma = "";
        for (String s : list) {
            ma += s + ",";
        }
        StringBuilder builder = new StringBuilder(ma);
        builder.deleteCharAt(ma.length() - 1);
        return ma;
    }

    private Adapter_hang adapter;
    private EditText edt_hang;
    private int change = 0;
    List<Hang> list_hang;

    private void addHang(EditText thuongHieu, SanPham sanPham) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_them_hang, null, false);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
        ListView listView = view.findViewById(R.id.list_hang);
        edt_hang = view.findViewById(R.id.edt_themhang_);
        ImageButton themHang = view.findViewById(R.id.ibtn_addhang);
        listView.setAdapter(adapter);
        edt_hang.setVisibility(View.GONE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
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
                        db.collection("hang").document(list_hang.get(position).getMaHang()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder1.create().show();

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                thuongHieu.setText(list_hang.get(position).getTenHang());
                sanPham.setMaHang(list_hang.get(position).getMaHang());
                dialog.dismiss();
            }
        });
        themHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                if (change == 0) {
                    edt_hang.setVisibility(View.VISIBLE);
                    change = 1;
                } else {
                    edt_hang.setVisibility(View.GONE);
                    change = 0;
                    db.collection("hang").document(id).set(new Hang(id, edt_hang.getText().toString(), new Date().getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(getContext(), "Thêm hãng thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                thuongHieu.setText(edt_hang.getText().toString());
                                sanPham.setMaHang(id);
                            } else {
                                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    private void ngheHang(List<Hang> list) {
        db.collection("hang").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                list.add(dc.getDocument().toObject(Hang.class));
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Hang dtoq = dc.getDocument().toObject(Hang.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list.set(dc.getOldIndex(), dtoq);
                                    adapterSanpham.notifyDataSetChanged();
                                } else {
                                    list.remove(dc.getOldIndex());
                                    list.add(dtoq);
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(Hang.class);
                                list.remove(dc.getOldIndex());
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    private String id;

    private void anhxa(View view) {
        RecyclerView rcv_list = view.findViewById(R.id.rcv_qlsp);
        ibtn_them = view.findViewById(R.id.ibtn_them_sp);
        list_giay = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        getSP();
        List<ThuongHieu> list_thuongHieu = new ArrayList<>();
        adapterSanpham = new Adapter_sanpham(list_giay, getContext(), this, quyen);
        rcv_list.setAdapter(adapterSanpham);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcv_list.setLayoutManager(manager);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sẽ mất một lúc vui lòng chờ");
        if (quyen == 1) {
            ibtn_them.setVisibility(View.GONE);
        }

    }

    private void getSP() {
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
                                list_giay.add(dc.getDocument().toObject(SanPham.class));
                                adapterSanpham.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                SanPham dtoq = dc.getDocument().toObject(SanPham.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list_giay.set(dc.getOldIndex(), dtoq);
                                    adapterSanpham.notifyDataSetChanged();
                                } else {
                                    list_giay.remove(dc.getOldIndex());
                                    list_giay.add(dtoq);
                                    adapterSanpham.notifyDataSetChanged();
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(SanPham.class);
                                list_giay.remove(dc.getOldIndex());
                                adapterSanpham.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quan_ly_giay, container, false);
    }

    public void hienthiAnh(Uri uri) {
        Glide.with(getContext()).load(uri).error(R.drawable.logo3).into(anh);
        linkImage = uri.toString();
    }


    private String linkMoi = "";

    public void upAnh(Uri imageUri, EditText ten, EditText gia, EditText namSX, EditText soLuong, EditText list_kichco, Dialog dialog, String id, SanPham sanPham, String thongbao) {
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference("img").child(id);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    linkMoi = uri.toString();

                                    sanPham.setAnh(linkMoi);
                                    sanPham.setMaSp(id);
                                    sanPham.setTenHang(a.getText().toString());
                                    sanPham.setTenSP(ten.getText().toString().trim());
                                    sanPham.setGia(Long.parseLong(gia.getText().toString()));
                                    sanPham.setNamSX(namSX.getText().toString());
                                    sanPham.setTime(new Date().getTime());
                                    sanPham.setSoLuong(Long.parseLong(soLuong.getText().toString()));
                                    List<String> kichCo = Arrays.asList(list_kichco.getText().toString().trim().split(","));
                                    sanPham.setKichCo(kichCo);
                                    db.collection("sanPham").document(id).set(sanPham).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), thongbao, Toast.LENGTH_SHORT).show();
                                                progressDialog.cancel();
                                                dialog.dismiss();
                                                adapterSanpham.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi lấy đường dẫn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Adapter_sanpham getAdapter() {
        return adapterSanpham;
    }


}