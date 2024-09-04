package com.example.DuAnTotNghiepKs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @Column(name = "ten_dang_nhap")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @Column(name = "mat_khau")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;

    @OneToMany(mappedBy = "taiKhoan")
    private Set<NhanVien> nhanViens = new HashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "taiKhoan")
//    List<ChiTietVaiTro> chiTietVaiTros;
}