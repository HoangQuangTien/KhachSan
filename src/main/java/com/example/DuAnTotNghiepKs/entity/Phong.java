package com.example.DuAnTotNghiepKs.entity;





import jakarta.persistence.*;

import lombok.AllArgsConstructor;

import lombok.Getter;

import lombok.NoArgsConstructor;

import lombok.Setter;



import java.util.HashSet;

import java.util.List;
import java.util.Set;



@Getter

@Setter

@AllArgsConstructor

@NoArgsConstructor

@Entity

@Table(name = "Phong")

public class Phong {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_phong")

    private Integer idPhong;



    @Column(name = "ma_phong")

    private String maPhong;



    @Column(name = "ten_phong")

    private String tenPhong;



//    @Column(name = "so_nguoi_toi_da")

//    private Boolean soNguoiToiDa;



    @Column(name = "tinh_trang")

    private Boolean tinhTrang;



    @Column(name = "mo_ta")

    private String moTa;



    @Column(name = "gia")

    private Float gia;



    @Column(name = "trang_thai")

    private Boolean trangThai;



    @Column(name = "img")

    private String img;



//    @ManyToOne(fetch = FetchType.EAGER)

//    @JoinColumn(name = "id_tang",referencedColumnName ="id_tang")

//    private Tang tang;

//

//    @ManyToOne(fetch = FetchType.EAGER)

//    @JoinColumn(name = "id_dien_tich",referencedColumnName ="id_dien_tich")

//    private DienTich dienTich;



    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "id_loai_phong", referencedColumnName = "id_loai_phong")

    private LoaiPhong loaiPhong;



//    @OneToMany

//    @JoinColumn(name = "setCTPhong")

//    private Set<ChiTietPhong> chiTietPhongs = new HashSet<>();

//

    @OneToMany(mappedBy = "phong") // Đảm bảo rằng lớp DatPhong có thuộc tính 'phong'

    private Set<DatPhong> datPhongs = new HashSet<>();


//    @OneToMany(mappedBy = "phong")
//    private Set<DanhGia> danhGias; // Danh sách các đánh giá của phòng
//



}