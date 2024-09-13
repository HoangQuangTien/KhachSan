package com.example.DuAnTotNghiepKs.controller;


import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class ThongKeController {

    @Autowired
    private DatPhongService datPhongService;


    @GetMapping("/thongke")
//    public String getThongKePage() {
//        return "list/QuanLyThongKe/thongke"; // Nếu sử dụng Thymeleaf hoặc trả về view template
//    }
//
//
//    @GetMapping
    public String getIdleRoomStatistics(Model model) {
        List<IdleRoomDTO> idleRooms = datPhongService.getIdleRoomTimes();
        model.addAttribute("idleRooms", idleRooms);
        return "list/QuanLyThongKe/thongke"; // Thay thế bằng tên file template của bạn
    }
}