package com.example.DuAnTotNghiepKs.DTO;

import lombok.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@ToString
@Getter
@Setter

@NoArgsConstructor
public class EventDTO {
    private Integer id;
    private String maDatPhong;
    private String tenKhachHang;
    private String tenPhong;
    private String startDate; // Định dạng chuỗi
    private String endDate;   // Định dạng chuỗi
    private String backgroundColor;
    private String status;
    private boolean th;

    public EventDTO(Integer id, String maDatPhong, String tenKhachHang, String tenPhong, LocalDateTime startDate, LocalDateTime endDate, String status, String backgroundColor, boolean th) {
        this.id = id;
        this.maDatPhong = maDatPhong;
        this.tenKhachHang = tenKhachHang;
        this.tenPhong = tenPhong;
        this.startDate = String.valueOf(startDate);
        this.endDate = String.valueOf(endDate);
        this.backgroundColor = backgroundColor;
        this.status = status;
        this.th = th;

    }

    // Chuyển đổi Date thành chuỗi
    private String formatDate(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ISO
        return sdf.format(date);
    }

        // Getters và setters...

}
