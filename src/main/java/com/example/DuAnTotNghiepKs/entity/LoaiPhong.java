package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.validation.NoNumbers;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Column(name = "ma_loai_phong", nullable = false, length = 10)
    private String maLoaiPhong;


    @Column(name = "ten_loai_phong")
    private String tenLoaiPhong;

    @Column(name = "mo_ta")
    private String moTa;

    @NotNull(message = "Số lượng giường không được để trống")
    @Min(value = 1, message = "Số lượng giường phải là một số dương")
    @Max(value = 3, message = "So luong giuong tối đa không được vượt quá 3")
    @Column(name = "so_luong_giuong")
    private Integer soLuongGiuong;

    @NotNull(message = "Số người tối đa không được để trống")
    @Min(value = 1, message = "Số người tối đa phải là một số dương")
    @Max(value = 6, message = "Số người tối đa không được vượt quá 6")
    @Column(name = "so_nguoi_toi_da")
    private Integer soNguoiToiDa;
    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải là một số không âm")
    @Column(name = "gia")
    private Float gia;
    @NotNull(message = "Sức chứa không được để trống")
    @Min(value =    1, message = "Sức chứa phải là một số dương")
    @Max(value = 6, message = "Sức chứa tối đa không được vượt quá 6")
    @Column(name = "suc_chua")
    private Integer sucChua;

    @OneToMany(mappedBy = "loaiPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Phong> phongs = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "setLoaiPhong")
    private Set<LoaiPhong> loaiPhongs = new HashSet<>();


    @OneToOne
    @JoinColumn(name = "id_tang", referencedColumnName = "id_tang")
    private Tang tang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dien_tich",referencedColumnName ="id_dien_tich")
    private DienTich dienTich;
}
