package com.example.DuAnTotNghiepKs.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DatPhongRequest {

    private String maDatPhong;
    private Date ngayNhan;
    private Date ngayTra;
    private String cccd;
    private float tienCoc;
    private int idPhong;
    private int idLoaiPhong;
}
