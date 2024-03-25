package com.example.du_an1.model;

import java.util.List;

public class User {
    private String maUser;
    private String Email;
    private String hoTen;
    private String SDT;
    private int trangThai;
    private Long soDu;
    private int chucVu;
    private List<String> diachi;
    private String chonDiaCHi;

    public User() {
    }

    public User(String maUser, String email, int trangThai, Long soDu, int chucVu) {
        Email = email;
        this.trangThai = trangThai;
        this.soDu = soDu;
        this.chucVu = chucVu;
        this.maUser = maUser;
    }


    public User(String maUser, String email, String hoTen, String SDT, int trangThai, Long soDu, int chucVu) {
        this.maUser = maUser;
        Email = email;
        this.hoTen = hoTen;
        this.SDT = SDT;
        this.trangThai = trangThai;
        this.soDu = soDu;
        this.chucVu = chucVu;
    }

    public String getChonDiaCHi() {
        return chonDiaCHi;
    }

    public void setChonDiaCHi(String chonDiaCHi) {
        this.chonDiaCHi = chonDiaCHi;
    }

    public List<String> getDiachi() {
        return diachi;
    }

    public void setDiachi(List<String> diachi) {
        this.diachi = diachi;
    }

    public String getMaUser() {
        return maUser;
    }

    public void setMaUser(String maUser) {
        this.maUser = maUser;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public Long getSoDu() {
        return soDu;
    }

    public void setSoDu(Long soDu) {
        this.soDu = soDu;
    }

    public int getChucVu() {
        return chucVu;
    }

    public void setChucVu(int chucVu) {
        this.chucVu = chucVu;
    }

}
