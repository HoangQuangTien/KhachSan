package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DiaChiNhanVienDTO {

    private Integer idDiaChi;

    private Integer idNhanVien;

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

