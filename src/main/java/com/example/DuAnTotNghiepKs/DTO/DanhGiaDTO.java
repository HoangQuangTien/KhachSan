package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Data
public class DanhGiaDTO {
    private Integer idDanhGia;
    private Integer idPhong;
    private String tenKhachHang;
    private Integer soSao;
    private String noiDung;
    private LocalDateTime ngayDanhGia;
}
