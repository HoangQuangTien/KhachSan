package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CaLamViecDTO {
    private String maCaLamViec;
    private String tenCaLamViec;
    private LocalDateTime gioBatDau;
    private LocalDateTime gioKetThuc;


}