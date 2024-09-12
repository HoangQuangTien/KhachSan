package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KhuyenMai")
@Data
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khuyen_mai")
    private Integer idKhuyenMai;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(min = 5, max = 10, message = "Mã khuyến mãi phải có độ dài từ 5 đến 10 ký tự")
    @Column(name = "ma_khuyen_mai", nullable = false, unique = true)
    private String maKhuyenMai;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 3, max = 50, message = "Tên khuyến mãi phải có độ dài từ 3 đến 50 ký tự")
    @Column(name = "ten_khuyen_mai", nullable = false)
    private String tenKhuyenMai;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 10, max = 200, message = "Mô tả phải có độ dài từ 10 đến 200 ký tự")
    @Column(name = "mo_ta")
    private String moTa;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Column(name = "ngay_bat_dau", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayBatDau;

    @NotNull (message = "Ngày kết thúc không được để trống")
    @Column(name = "ngay_ket_thuc", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayKetThuc;

    @NotNull(message  = "Giảm giá không được để trống")
    @Min(value = 0, message = "Giảm giá phải lớn hơn hoặc bằng 0")
    @Column(name = "giam_gia", nullable = false)
    private Float giamGia;

    @NotNull(message = "Loại giảm không được để trống")
    @Column(name = "loai_giam", nullable = false)
    private Boolean loaiGiam;

    @NotNull
    @Column(name = "trang_thai")
    private Boolean trangThai;

}
