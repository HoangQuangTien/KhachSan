package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.util.Date;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DatPhongDTO {
    private Integer idDatPhong;
    private String maDatPhong;
    private Date ngayNhanPhong;
    private Date ngayTraPhong;
    private String tinhTrang;
    private String cccd;
    private Float tienCoc;

    private Integer idPhong;
    private String tenPhong;
    private Integer idLoaiPhong;
    private String tenLoaiPhong;

    private Integer idDoan;
    private String isGroup;
    private Integer soLuong;



}
