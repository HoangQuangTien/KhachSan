package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhuyenMaiDTO {
    private Integer idKhuyenMai;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private String trangThai;
}
