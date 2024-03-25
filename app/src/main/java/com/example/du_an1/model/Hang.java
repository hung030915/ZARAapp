package com.example.du_an1.model;

import java.util.List;

public class Hang {
    private String maHang;
    private String tenHang;
    private Long time;
    private List<SanPham> sanPham;

    public Hang() {
    }

    public Hang(String maHang, String tenHang) {
        this.maHang = maHang;
        this.tenHang = tenHang;
    }

    public Hang(String maHang, String tenHang, Long time) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.time = time;
    }

    public Hang(String tenHang, List<SanPham> sanPham) {
        this.tenHang = tenHang;
        this.sanPham = sanPham;
    }

    public Hang(String maHang, String tenHang, List<SanPham> sanPham) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.sanPham = sanPham;
    }

    public List<SanPham> getSanPham() {
        return sanPham;
    }

    public void setSanPham(List<SanPham> sanPham) {
        this.sanPham = sanPham;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }
}
