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
@Table(name = "LichSuDatPhong")
public class LichSuDatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLSDP")
    private Integer idLichSuDatPhong;

    @Column(name = "thoi_gian_thay_doi")
    private Date thoiGianThayDoi;  // Thời gian hủy

    @Column(name = "chi_tiet_thay_doi")
    private String chiTietThayDoi; // Lý do hủy

    @ManyToOne
    @JoinColumn(name = "id_dat_phong", referencedColumnName = "id_dat_phong")
    private DatPhong datPhong;
    //    @Column(name = "thoi_gian_sua")
    //    private Date thoiGianSua;
}
