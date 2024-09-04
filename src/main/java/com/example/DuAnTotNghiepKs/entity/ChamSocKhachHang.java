package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ChamSocKhachHang")
public class ChamSocKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cham_soc_khach_hang")
    private Integer idChamSocKhachHang;

    @Column(name = "ma_cham_soc_khach_hang")
    private String maChamSocKhachHang;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang",referencedColumnName = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id_nhan_vien")
    private NhanVien nhanVien;

    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @Column(name = "mo_ta")
    private String moTa;


}

