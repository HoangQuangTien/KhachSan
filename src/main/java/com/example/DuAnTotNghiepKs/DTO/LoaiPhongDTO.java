package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.math.BigDecimal;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoaiPhongDTO {
    private Integer idLoaiPhong;
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private String moTa;
    private Integer soLuongGiuong;
    private Integer soNguoiToiDa;
    private Float gia;


}
