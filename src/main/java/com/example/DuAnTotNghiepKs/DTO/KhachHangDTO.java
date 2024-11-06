package com.example.DuAnTotNghiepKs.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class KhachHangDTO {

    private Integer id;


    private String maKhachHang;

    private String hoVaTen;

    private String email;

    private boolean gioiTinh;

    private String soDienThoai;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean deletedAt;

    private String idDiaChiKhachHang;

    private String trangThai;
    private List<DiaChiKhachHangDTO> diaChi; // Danh sách địa chỉ

    private String tenDangNhap;
    private String matKhau;

    public KhachHangDTO(Integer id, String maKhachHang, String hoVaTen, String email, boolean gioiTinh, String soDienThoai, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean deletedAt, String tenDangNhap, String matKhau) {
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoVaTen = hoVaTen;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }
}
