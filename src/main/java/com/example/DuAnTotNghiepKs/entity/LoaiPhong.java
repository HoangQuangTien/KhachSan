package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.validation.NoNumbers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LoaiPhong")
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loai_phong")
    private Integer idLoaiPhong;
    @NotBlank(message = "Mã loại phòng không được để trống")
    @Column(name = "ma_loai_phong", nullable = false, length = 10)
    private String maLoaiPhong;

    @NoNumbers
    @NotBlank(message = "Ten loại phòng không được để trống")
    @Column(name = "ten_loai_phong")
    private String tenLoaiPhong;

    @Column(name = "mo_ta")
    private String moTa;

    @Positive(message = "Số lượng giường phải là số dương")
    @Column(name = "so_luong_giuong")
    private Integer soLuongGiuong;

    @Positive(message = "Số lượng người phải là số dương")
    @Column(name = "so_nguoi_toi_da")
    private Integer soNguoiToiDa;

    @Column(name = "gia")
    private Float gia;

    @OneToMany(mappedBy = "loaiPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Phong> phongs = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "setLoaiPhong")
    private Set<LoaiPhong> loaiPhongs = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tang",referencedColumnName ="id_tang")
    private Tang tang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dien_tich",referencedColumnName ="id_dien_tich")
    private DienTich dienTich;


}

