package com.example.du_an1.model;

import androidx.annotation.NonNull;

import com.example.du_an1.fragment.QuanLyGiay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanPham implements Serializable {
    private String maSp;
    private String anh;
    private String tenSP;
    private Long gia;
    private Long soLuong;
    private String maHang;
    private List<String> kichCo;
    private String namSX;
    private Long time;

    public SanPham() {
    }

    public SanPham(String maSp, String anh, String tenSP, Long gia, Long soLuong, String maHang, List<String> kichCo, String namSX, Long time) {
        this.maSp = maSp;
        this.anh = anh;
        this.tenSP = tenSP;
        this.gia = gia;
        this.soLuong = soLuong;
        this.maHang = maHang;
        this.kichCo = kichCo;
        this.namSX = namSX;
        this.time = time;
    }


    public String getMaSp() {
        return maSp;
    }

    public void setMaSp(String maSp) {
        this.maSp = maSp;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public Long getGia() {
        return gia;
    }

    public void setGia(Long gia) {
        this.gia = gia;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public List<String> getKichCo() {
        return kichCo;
    }

    public void setKichCo(List<String> kichCo) {
        this.kichCo = kichCo;
    }

    public String getNamSX() {
        return namSX;
    }

    public void setNamSX(String namSX) {
        this.namSX = namSX;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    HashMap<String,Object> map() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("maSp",maSp);
        map.put("anh",anh);
        map.put("tenSP",tenSP);
        map.put("gia",gia);
        map.put("soLuong",soLuong);
        map.put("maHang",maHang);
        map.put("kichCo",kichCo);
        map.put("namSX",namSX);
        map.put("time",time);
        return map;
    }
FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String tenHang;

    public String getTenHang() {

        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public void tenHang(QuanLyGiay giay) {
        DocumentReference reference;
       reference = db.collection("hang").document(getMaHang());
       reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (!task.isComplete()){
                   return;
               }
               DocumentSnapshot snapshot = task.getResult();
               if (!snapshot.exists()){
                   return;
               }
               Map<String,Object> map = snapshot.getData();
               tenHang = (String) map.get("tenHang");
               giay.setA(tenHang);

           }
       });

    }
    public String getTenSP(List<SanPham> list_sanPham) {
        for (SanPham u : list_sanPham) {
            if (maSp.equals(u.getMaSp())) {
                return u.getTenSP();
            }
        }
        return null;
    }
    public String getgia(List<SanPham> list_sanPham) {
        for (SanPham u : list_sanPham) {
            if (maSp.equals(u.getMaSp())) {
                return u.getGia()+"";
            }
        }
        return null;
    }
}
