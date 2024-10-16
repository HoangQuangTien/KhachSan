package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ThanhToanDTO {

    private Integer idThanhToan;
    private String maThanhToan;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayThanhToan;
    private Boolean phuongThuc;
    private Float soTien;
    private Boolean tinhTrang;

    private Integer idDatPhong;
    private LocalDateTime ngayNhan;
    private LocalDateTime ngayTra;
    @DateTimeFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime ngayCheckIn;
    private Float tienCoc;
    private Float tongTien;
    private DatPhongDTO datPhongDTO;

    private Integer idKhachHang;
    private String hoVaTen;
    private String soDienThoai;

    private Integer idLoaiPhong;

    private Integer idPhong;
    private String tenPhong;

    private boolean inHoaDon;

    private NhanVienDTO nhanVienDTO;

}
