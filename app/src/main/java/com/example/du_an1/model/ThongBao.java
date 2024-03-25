package com.example.du_an1.model;

public class ThongBao {
    private String maThongBao;
    private String maKhachHang;
    private String noiDung;
    private int chucVu;
    private Long time;


    public ThongBao() {
    }

    public ThongBao(String maThongBao, String maKhachHang, String noiDung, int chucVu) {
        this.maThongBao = maThongBao;
        this.maKhachHang = maKhachHang;
        this.noiDung = noiDung;
        this.chucVu = chucVu;
    }

    public ThongBao(String maThongBao, String maKhachHang, String noiDung, int chucVu, Long time) {
        this.maThongBao = maThongBao;
        this.maKhachHang = maKhachHang;
        this.noiDung = noiDung;
        this.chucVu = chucVu;
        this.time = time;
    }

    public String getMaThongBao() {
        return maThongBao;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setMaThongBao(String maThongBao) {
        this.maThongBao = maThongBao;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public int getChucVu() {
        return chucVu;
    }

    public void setChucVu(int chucVu) {
        this.chucVu = chucVu;
    }
}
