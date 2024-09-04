package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaChiKhachHangDTO {

    private Integer id;

    private Integer idKhachHang;

    private String diaChiCuThe;

    private String thanhPho;

    private String quanHuyen;

    private String phuongXa;

    private String ghiChu;

    private String trangThai;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean deletedAt;
}
