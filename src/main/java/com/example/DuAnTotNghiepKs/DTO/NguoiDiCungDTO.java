package com.example.DuAnTotNghiepKs.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NguoiDiCungDTO {
    private Integer id;
    private String tenNguoiDiCung;
    private String soCmnd;
    private Integer datPhongId;
    private Integer khachHangId;
    private Integer idLoaiPhong;


    // Thông tin nhân viên
//    private Integer id;
//    private String maKhachHang;
//    private String hoVaTen;
//    private String email;
//    private String soDienThoai;
//    private Boolean gioiTinh;
}
