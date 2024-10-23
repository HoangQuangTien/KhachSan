package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.EventDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;

@Controller
@RequestMapping("/dangLich")
public class DangLichController {

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping
    public String showDangLichList(Model model) {
        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }
        return "list/QuanLyDatPhong/dangLich"; // Đường dẫn đến trang HTML Thymeleaf
    }

    @GetMapping("/events")
    @ResponseBody
    public List<EventDTO> getEvents() {
        List<DatPhong> datPhongs = datPhongService.getAllDatPhong();
        return datPhongs.stream()
                .map(datPhong -> new EventDTO(
                        datPhong.getIdDatPhong(),
                        datPhong.getMaDatPhong(),
                        datPhong.getKhachHang().getHoVaTen(),
                        datPhong.getPhong().getTenPhong(),
                        datPhong.getNgayNhan(),
                        datPhong.getNgayTra(),
                        datPhong.getTinhTrang(), // Lấy trạng thái từ DatPhong
                        getColorBasedOnStatus(datPhong.getTinhTrang()), // Lấy màu tương ứng
                        datPhong.getTrangThai()
                ))
                .toList();
    }

    // Phương thức bổ sung để lấy màu sắc dựa trên tình trạng đặt phòng
    private String getColorBasedOnStatus(String tinhTrang) {
        if (tinhTrang.equals("Đã Checkin")) {
            return "#fd7e14"; // Màu cam cho "Đã check-in"
        } else if (tinhTrang.equals("Đã Hủy")) {
            return "#dc3545"; // Màu đỏ cho "Đã hủy"
        } else if (tinhTrang.equals("Chưa Checkin")) {
            return "#007bff"; // Màu xanh cho "Chưa check-in"
        } else {
            return "#6c757d"; // Màu xám cho các trạng thái không xác định
        }
    }


}
