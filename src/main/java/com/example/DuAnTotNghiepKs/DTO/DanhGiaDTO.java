package com.example.DuAnTotNghiepKs.DTO;

import com.example.DuAnTotNghiepKs.entity.DanhGia;
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
    private String tenPhong;


    // Constructor chuyển từ Entity DanhGia sang DTO
    public DanhGiaDTO(DanhGia danhGia) {
        this.idDanhGia = danhGia.getIdDanhGia();
        this.idPhong = danhGia.getPhong().getIdPhong(); // Lấy id phòng
        this.tenKhachHang = danhGia.getTenKhachHang();
        this.soSao = danhGia.getSoSao();
        this.noiDung = danhGia.getNoiDung();
        this.ngayDanhGia = danhGia.getNgayDanhGia();
        this.tenPhong = danhGia.getPhong() != null ? danhGia.getPhong().getTenPhong() : null; // Lấy tên phòng nếu có
    }
}
