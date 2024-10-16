package com.example.DuAnTotNghiepKs.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "NhanVien")
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhan_vien")
    private Integer idNhanVien;


    @Column(name = "ma_nhan_vien",unique = true,nullable = false)
    private String maNhanVien;

    //    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "ho_ten")
    private String hoTen;


    @Column(name = "ngay_sinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @NotNull
//    @Pattern(regexp = "\\d{9,11}$", message = "Số điện thoại phải là số và có từ 10 số")
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "thanh_pho")
    private String thanhPho;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @NotNull
//    @Pattern(regexp = "^[\\w\\.-]+@gmail\\.com$", message = "Email không đúng định dạng")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "li_do")
    private String liDo;

    @Column(name = "img")
    private String img;

    @OneToOne
    @JoinColumn(name = "ten_dang_nhap")
    private TaiKhoan taiKhoan; // Đảm bảo thuộc tính này có mặt

    @OneToMany(mappedBy = "nhanVien",fetch = FetchType.EAGER)
    private Set<ChamSocKhachHang> chamSocKhachHangs;

    @OneToMany(mappedBy = "nhanVien",fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<LichLamViec> lichLamViecs;

//    @OneToMany(mappedBy = "nhanVien",fetch = FetchType.EAGER)
//    private Set<ThanhToan> thanhToans;

}
