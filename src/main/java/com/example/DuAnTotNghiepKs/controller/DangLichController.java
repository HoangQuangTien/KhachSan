package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.EventDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@Controller
@RequestMapping("/dangLich")
public class DangLichController {

    @Autowired
    private DatPhongService datPhongService;

    @GetMapping
    public String showDangLichList(Model model) {
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
                        datPhong.getTinhTrang() ? "#fd7e14" : "#007bff", // Màu sắc dựa trên trạng thái
                        datPhong.getTinhTrang() ? "Đã check-in" : "Chưa check-in" // Trạng thái
                ))
                .toList();
    }



}
