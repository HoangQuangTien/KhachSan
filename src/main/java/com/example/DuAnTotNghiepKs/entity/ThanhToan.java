package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ThanhToan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idThanhToan;
    @Column(name = "ma_thanh_toan")
    private String maThanhToan;
    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;
    @Column(name = "phuong_thuc")
    private boolean phuongThuc;
    @Column(name = "tinh_trang")
    private boolean tinhTrang;

    // Quan hệ với bảng DatPhong - Một thanh toán phải thuộc về một đơn đặt phòng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dat_phong", referencedColumnName = "id_dat_phong")
    private DatPhong datPhong;

}