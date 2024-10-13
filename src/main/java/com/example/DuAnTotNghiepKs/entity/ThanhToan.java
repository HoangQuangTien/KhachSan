package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Column(name = "id_thanh_toan")
    private Integer idThanhToan;
    @Column(name = "ma_thanh_toan")
    private String maThanhToan;
//
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;
    @Column(name = "so_tien")
    private Float soTien;
    @Column(name = "phuong_thuc")
    private boolean phuongThuc;
    @Column(name = "tinh_trang")
    private Boolean tinhTrang;

    // Quan hệ với bảng DatPhong - Một thanh toán phải thuộc về một đơn đặt phòng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dat_phong", referencedColumnName = "id_dat_phong")
    private DatPhong datPhong;


    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

}
