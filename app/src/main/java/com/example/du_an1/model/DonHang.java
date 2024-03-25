package com.example.du_an1.model;

import java.util.List;

public class DonHang {
    private String maDon;
    private String maKhachHang;
    private List<Don> listSP;
    private Long time;
    private int trangThai;
    private Long giaDon;
    private String ngayMua ;

    public DonHang() {
    }




    public DonHang(String maDon, String maKhachHang, List<Don> listSP, Long time, int trangThai, Long giaDon, String ngayMua) {
        this.maDon = maDon;
        this.maKhachHang = maKhachHang;
        this.listSP = listSP;
        this.time = time;
        this.trangThai = trangThai;
        this.giaDon = giaDon;
        this.ngayMua = ngayMua;
    }

    public String getNgayMua() {
        return ngayMua;
    }

    public void setNgayMua(String ngayMua) {
        this.ngayMua = ngayMua;
    }

    public Long getGiaDon() {
        return giaDon;
    }

    public void setGiaDon(Long giaDon) {
        this.giaDon = giaDon;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMaDon() {
        return maDon;
    }

    public void setMaDon(String maDon) {
        this.maDon = maDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public List<Don> getListSP() {
        return listSP;
    }

    public void setListSP(List<Don> listSP) {
        this.listSP = listSP;
    }
}
