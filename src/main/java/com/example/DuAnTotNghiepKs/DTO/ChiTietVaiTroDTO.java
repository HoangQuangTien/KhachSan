package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChiTietVaiTroDTO {
    private Integer idChiTietVaiTro;
    private String maChoTietVaiTro;
    private VaiTroDTO vaiTroDTO;
//    private TaiKhoanDTO taiKhoanDTO;

}
