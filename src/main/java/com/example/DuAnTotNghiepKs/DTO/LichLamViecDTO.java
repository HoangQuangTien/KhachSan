package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LichLamViecDTO {
    private Integer idLichLamViec;
    private String maLichLamViec;
    private Integer idNhanVien;
    private Date ngay;
    private String maCaLamViec;


}
