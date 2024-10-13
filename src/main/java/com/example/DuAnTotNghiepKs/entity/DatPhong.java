package com.example.DuAnTotNghiepKs.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity

@Table(name = "DatPhong")
public class DatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dat_phong")
    private Integer idDatPhong;
    @Column(name = "ma_dat_phong")
    private String maDatPhong;
    @Column(name = "ngay_nhan_phong")
    private LocalDateTime ngayNhan;
    @Column(name = "ngay_tra_phong")
    private LocalDateTime ngayTra;
    @Column(name = "tinh_trang")
    private String tinhTrang;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "cccd")
    private String cccd;
    @Column(name = "tien_coc")
    private Float tienCoc;

    @Column(name = "tong_tien")
    private Float tongTien;

    @Column(name = "tien_con_lai")
    private Float tienConLai;

    //    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "ngay_check_in")
    private LocalDateTime ngayCheckIn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_phong",referencedColumnName ="id_phong")
    private Phong phong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_loai_phong")
    private LoaiPhong loaiPhong;

    @OneToMany(mappedBy = "datPhong")
    private Set<Doan> doans;


    @OneToMany(mappedBy = "datPhong")
    private List<ChiTietDatPhong> chiTietDatPhongs;

    // Quan hệ với bảng ThanhToan - Một đặt phòng có thể có nhiều thanh toán
    @OneToMany(mappedBy = "datPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ThanhToan> thanhToans = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVienCheckIn;


//    @OneToMany(mappedBy = "datPhong",cascade = CascadeType.ALL,orphanRemoval = true)
//    private Set<PhuPhi> phuPhis = new HashSet<>();

    @OneToMany(mappedBy = "datPhong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NguoiDiCung> nguoiDiCungList;



}

