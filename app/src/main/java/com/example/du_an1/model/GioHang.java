package com.example.du_an1.model;

import java.util.List;

public class GioHang {
    private String maGio;
    private String maKhachHang;
    private String maSanPham;
    private String kichCo;
    private Long soLuong;



    public GioHang() {
    }

    public GioHang(String maGio, String maKhachHang, String maSanPham, String kichCo, Long soLuong) {
        this.maGio = maGio;
        this.maKhachHang = maKhachHang;
        this.maSanPham = maSanPham;
        this.kichCo = kichCo;
        this.soLuong = soLuong;
    }



    public String getKichCo() {
        return kichCo;
    }

    public void setKichCo(String kichCo) {
        this.kichCo = kichCo;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }

    public String getMaGio() {
        return maGio;
    }

    public void setMaGio(String maGio) {
        this.maGio = maGio;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public GioHang getgioHang(List<GioHang> list_gio) {
        for (GioHang hang : list_gio) {
            if (maGio.equals(hang.getMaGio())) {
                return hang;
            }
        }
        return null;
    }
}
