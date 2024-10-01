package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.LichSuDatPhongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lichsu")
public class LichSuDatPhongController {

    @Autowired
    private LichSuDatPhongRepo lichSuDatPhongRepo;

    @Autowired
    private DatPhongRepo datPhongRepo;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("listDatPhong", datPhongRepo.findAll());
        model.addAttribute("listLichSu", lichSuDatPhongRepo.findAll());
    }

    // Hiển thị trang lịch sử đặt phòng khi người dùng truy cập /lichsu
    @GetMapping
    public String showLichSuDatPhong() {
        return "list/QuanLyDatPhong/lichsudatphong"; // Tên trang Thymeleaf cần render
    }
}
