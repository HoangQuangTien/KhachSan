package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ChiTietVaiTro")
public class ChiTietVaiTro {
    @Id
    @Column(name = "id_chi_tiet_vai_tro")
    private Integer idChiTietVaiTro;

    @Column(name = "ma_cho_tiet_vai_tro")
    private String maChoTietVaiTro;

    @ManyToOne
    @JoinColumn(name = "id_vai_tro")
    private VaiTro vaiTro;

    @ManyToOne
    @JoinColumn(name = "ten_dang_nhap")
    private TaiKhoan taiKhoan;


}
