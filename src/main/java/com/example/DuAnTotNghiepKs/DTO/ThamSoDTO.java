package com.example.DuAnTotNghiepKs.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThamSoDTO {
    private Long id;
    private String tenThamSo;
    private String giaTri;
    private String moTa;
}
