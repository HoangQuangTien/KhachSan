package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class NhanVienDTO {
    private Integer idNhanVien;
    private String email;
    private String soDienThoai;
    private String maNhanVien;
    private String img;
    private String hoTen;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;
    private Boolean gioiTinh;
    //    private Float luong;
    private String liDo;
    private Boolean trangThai;
    private String thanhPho;
    private String quanHuyen;
    private String phuongXa;
    private TaiKhoanDTO taiKhoanDTO;

    public NhanVienDTO(Integer idNhanVien, String maNhanVien, String hoTen) {
        this.idNhanVien = idNhanVien;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
    }

    public NhanVienDTO(Integer idNhanVien, String email, String soDienThoai, String maNhanVien, String img, String hoTen, Date ngaySinh, Boolean gioiTinh, String liDo, Boolean trangThai, String thanhPho, String quanHuyen, String phuongXa) {
        this.idNhanVien = idNhanVien;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.maNhanVien = maNhanVien;
        this.img = img;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.liDo = liDo;
        this.trangThai = trangThai;
        this.thanhPho = thanhPho;
        this.quanHuyen = quanHuyen;
        this.phuongXa = phuongXa;
    }
}
