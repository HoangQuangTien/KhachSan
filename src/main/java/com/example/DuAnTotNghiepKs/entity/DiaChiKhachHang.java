package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DiaChiKhachHang")
public class DiaChiKhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang idKhachHang;

    @Column(name = "dia_chi_cu_the", length = 255, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String diaChiCuThe;

    @Column(name = "thanh_pho", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String thanhPho;

    @Column(name = "quan_huyen", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String quanHuyen;

    @Column(name = "phuong_xa", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String phuongXa;

    @Column(name = "ghi_chu", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String ghiChu;

    @Column(name = "trang_thai", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String trangThai;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private Boolean deletedAt;
}
