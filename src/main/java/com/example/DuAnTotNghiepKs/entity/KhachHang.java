package com.example.DuAnTotNghiepKs.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_khach_hang")
    private int id;

    @Column(name = "ma_khach_hang", nullable = false, length = 50)
    private String maKhachHang;

//    @Column(name = "mat_khau", nullable = false, length = 50)
//    private String matKhau;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoVaTen;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "gioi_tinh", nullable = false)
    private boolean gioiTinh;

    @Column(name = "sdt", nullable = false, length = 50)
    private String soDienThoai;

//    @Column(name = "trang_thai", nullable = false, length = 50)
//    private String trangThai;

    @CreationTimestamp
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private boolean deletedAt;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DatPhong> datPhongs = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "ten_dang_nhap") // Tên cột khóa ngoại trong bảng KhachHang
    private TaiKhoan taiKhoan;

//    @OneToMany(mappedBy = "datPho ng", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ChiTietDatPhong> chiTietDatPhongs = new HashSet<>();
}
