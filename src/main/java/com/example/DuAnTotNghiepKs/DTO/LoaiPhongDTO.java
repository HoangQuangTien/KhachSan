package com.example.DuAnTotNghiepKs.DTO;

import com.example.DuAnTotNghiepKs.entity.DienTich;
import com.example.DuAnTotNghiepKs.entity.Tang;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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
    private Float tenDienTich;
    private String tenTang;


    public LoaiPhongDTO(Integer idLoaiPhong, String maLoaiPhong, String tenLoaiPhong, String moTa, Integer soLuongGiuong, Integer soNguoiToiDa, Float gia, DienTich dienTich, Tang tang) {
    }

    private List<PhongDTO> availableRooms;

}
