package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an1.R;
import com.example.du_an1.model.Don;
import com.example.du_an1.model.DonHang;
import com.example.du_an1.model.SanPham;
import com.example.du_an1.model.ThongBao;
import com.example.du_an1.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Adapter_quanlyhoadon extends RecyclerView.Adapter<Adapter_quanlyhoadon.viewHolder> {
    private static final String TAG = "TAG";
    Context context;
    List<SanPham> list_sanPham;
    List<User> list_Users;
    List<DonHang> list_doHang;

    FirebaseFirestore db;
    ProgressDialog progressDialog;
    Long tienHoan = 0l;
    DonHang donHangHoan = null;
    User user = null;


    public Adapter_quanlyhoadon(List<SanPham> list_sanPham, List<User> list_Users, List<DonHang> list_doHang, Context context) {

        this.list_sanPham = list_sanPham;
        this.list_Users = list_Users;
        this.list_doHang = list_doHang;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sẽ mất một lúc vui lòng chờ");
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_ql_don_hang, parent, false);
        return new viewHolder(view);
    }
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (list_Users.size() <= 0 && list_doHang.size() <= 0 && list_sanPham.size() <= 0) {
            return;
        }
        String[] data = getdata(list_Users, list_doHang.get(position));
        if (data.length <= 0) {
            return;
        }
        DonHang donHang = list_doHang.get(position);
        if (donHang == null) {
            return;
        }
        donHangHoan = donHang;
        SanPham sp = getmaSP(data[5]);
        Toast.makeText(context,""+data[5],Toast.LENGTH_SHORT).show();
        String link = laylink(data[5]);
        Glide.with(context).load(link).error(R.drawable.them_anh).into(holder.anh);

        Long gia = Long.parseLong(data[3]);
        tienHoan = gia;
        String maKH = list_doHang.get(position).getMaKhachHang();
        holder.tv_tenKH.setText("Họ tên:" + data[0]);
        holder.tv_diaChi.setText("Địa chỉ: " + data[1]);
        holder.tv_sdt.setText("Sđt: " + data[2]);
        holder.tv_gia.setText("Giá :" + data[3]);
        holder.tv_soluong.setText("Số lượng sản phẩm mua: " + data[4]);
        holder.btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trangThai(3, donHang);
            }
        });
        holder.btn_xacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                db.collection("user").document(maKH).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                Long soDu = task.getResult().getLong("soDu");
                                if (soDu == null) {
                                    Toast.makeText(context, "Loi long", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (soDu < gia) {
                                    Toast.makeText(context, "Số dư khách hàng không đủ", Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                    return;
                                }
                                Long newSoDu = soDu - gia;
                                user = task.getResult().toObject(User.class);
                                db.collection("user").document(maKH).update("soDu", newSoDu).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()) {
                                            Toast.makeText(context, "Đang chạy bước 1", Toast.LENGTH_SHORT).show();
                                            trangThai(1, donHang);
                                        } else {
                                            Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Nguoi dung k ton tai", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Loi truy van", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public SanPham getmaSP(String masp) {
        SanPham sanPham = new SanPham();
        if (masp.equals(sanPham.getMaSp())) {
            return sanPham;
        }
        return sanPham;
    }

    private void trangThai(int i, DonHang donHang) {
        if (donHang == null) {
            return;
        }
        donHang.setTrangThai(i);
        db.collection("donHang").document(donHang.getMaDon()).set(donHang).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(context, "Đang chạy bước 2", Toast.LENGTH_SHORT).show();
                    if (i == 1) {
                        updataDonHang(i, donHang);
                    }

                } else {
                    Toast.makeText(context, "Lỗi cụ rồi bảo dev fix đi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updataDonHang(int i, DonHang donHang) {
        if (donHang == null) {
            return;
        }
        donHang.setTrangThai(i);
        db.collection("donHangDaDuyet").document(donHang.getMaDon()).set(donHang).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(context, "Đang chạy bước 3", Toast.LENGTH_SHORT).show();
                    setTop(donHang);

                } else {
                    Toast.makeText(context, "Lỗi cụ rồi bảo dev fix đi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setTop(DonHang donHang) {
        for (Don d : donHang.getListSP()) {
            getTop(d.getMaSP(), d.getSoLuong());

        }
    }

    private void capnhatSoluong(String maSP, Long soLuong) {
        final SanPham[] sp = {new SanPham()};
        db.collection("sanPham").document(maSP).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isComplete()) {
                    return;
                }
                sp[0] = task.getResult().toObject(SanPham.class);
                if (sp[0].getSoLuong() < soLuong) {
                    Toast.makeText(context, "Số lượng trong kho không đủ", Toast.LENGTH_SHORT).show();
                    huyDon(donHangHoan, user);
                    return;
                }
                sp[0] = task.getResult().toObject(SanPham.class);
                sp[0].setSoLuong(sp[0].getSoLuong() - soLuong);
                db.collection("sanPham").document(maSP).set(sp[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isComplete()) {
                            return;
                        }
                        Toast.makeText(context, "Hoàn thành", Toast.LENGTH_SHORT).show();
                        guiThongBao();
                        Log.e(TAG, "onComplete: " + "trừ hàng thành công");
                    }
                });

            }
        });

    }

    private void guiThongBao() {
        String id = UUID.randomUUID().toString();
        db.collection("thongBao").document(id)
                .set(new ThongBao(id,user.getMaUser(),"Đơn hàng của bạn đang được giao",3,new Date().getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isComplete()){
                            Log.e(TAG, "onComplete: "+"Lỗi 241" );
                            return;
                        }
                        Log.e(TAG, "onComplete: "+"thong bao thanh cong" );
                        progressDialog.cancel();
                    }
                });
    }

    private void huyDon(DonHang donHang, User user) {
        donHang.setTrangThai(3);
        db.collection("donHang").document(donHang.getMaDon()).set(donHang).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isComplete()) {
                    Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                    return;
                }
                hoanTien(user, tienHoan);
            }
        });
    }

    private void hoanTien(User user, Long tienHoan) {
        
        db.collection("user").document(user.getMaUser()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isComplete()) {
                    Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.cancel();
                Toast.makeText(context, "Hoàn tiền thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getTop(String maSP, Long sl) {
        final Long[] i = {0l};
        db.collection("top10").document(maSP).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isComplete()) {
                    return;
                }
                i[0] = task.getResult().getLong("soLuong");
                if (i[0] == null) {
                    i[0] = 0l;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("soLuong", i[0] + sl);
                map.put("maSP", maSP);
                db.collection("top10").document(maSP).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Toast.makeText(context, "Đang chạy bước 4", Toast.LENGTH_SHORT).show();
                            capnhatSoluong(maSP, sl);
                            Log.e(TAG, "onComplete: " + "đẩy dữ liệu thành công");
                        }
                    }
                });
            }
        });
    }

    private String[] getdata(List<User> list_Users, DonHang donHang) {
        if (list_Users.size() <= 0 && list_doHang.size() <= 0 && list_sanPham.size() <= 0) {
            return new String[]{};
        }
        String[] a = new String[]{"", "", "", "", "",""};
        for (User u : list_Users) {
            if (donHang.getMaKhachHang()
                    .equals(u.getMaUser())) {
                a[0] = u.getHoTen();
                a[1] = u.getChonDiaCHi();
                a[2] = u.getSDT();
            }
        }
        a[3] = donHang.getGiaDon() + "";
        Long soluong = 0l;
        String maSP ="";
        for (Don d : donHang.getListSP()) {
            soluong += d.getSoLuong();
            maSP = d.getMaSP();
        }
        a[4] = String.valueOf((soluong));
        a[5] = maSP;
        return a;
    }


    @Override
    public int getItemCount() {
        return list_doHang.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tv_tenKH, tv_gia, tv_diaChi, tv_sdt, tv_soluong;
        ImageButton btn_Huy, btn_xacNhan;
        ImageView anh;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tenKH = itemView.findViewById(R.id.tv_tenKhach);
            tv_diaChi = itemView.findViewById(R.id.tv_diaChi);
            tv_sdt = itemView.findViewById(R.id.tv_sdt);
            tv_soluong = itemView.findViewById(R.id.tv_soLuong_);
            tv_gia = itemView.findViewById(R.id.tv_gia);
            anh = itemView.findViewById(R.id.imgv_anhsp);

            btn_Huy = itemView.findViewById(R.id.ibtn_Huy);
            btn_xacNhan = itemView.findViewById(R.id.ibtn_XacNhan);
        }

    }
}
