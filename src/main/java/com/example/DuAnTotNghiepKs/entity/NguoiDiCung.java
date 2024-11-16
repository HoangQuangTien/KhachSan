package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "NguoiDiCung")
public class NguoiDiCung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name = "ten_nguoi_di_cung", nullable = false, length = 50)
    private String tenNguoiDiCung;
    @Column(name = "so_cmnd", nullable = false, length = 100)
    private String soCmnd;
    @ManyToOne
    @JoinColumn(name = "dat_phong_id", nullable = false)
    private  DatPhong datPhong;
//    @ManyToOne
//    @JoinColumn(name = "khach_hang_id ", nullable = false)
//    private  KhachHang khachHang;



}
