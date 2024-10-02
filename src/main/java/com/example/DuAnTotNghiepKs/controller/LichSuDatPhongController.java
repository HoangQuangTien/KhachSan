package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.LichSuDatPhong;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.LichSuDatPhongRepo;
import com.example.DuAnTotNghiepKs.service.LichSuDatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lichsu")
public class LichSuDatPhongController {

    @Autowired
    private LichSuDatPhongRepo lichSuDatPhongRepo;

    @Autowired
    private DatPhongRepo datPhongRepo;
    @Autowired
    private LichSuDatPhongService lichSuDatPhongService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("listDatPhong", datPhongRepo.findAll());
    }

    // Hiển thị trang lịch sử đặt phòng khi người dùng truy cập /lichsu
    @GetMapping
    public String showLichSuDatPhong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Page<LichSuDatPhong> lichSuPage = lichSuDatPhongService.getLichSuDatPhong(page, size);
        model.addAttribute("listLichSu", lichSuPage.getContent());
        model.addAttribute("currentPage", lichSuPage.getNumber());
        model.addAttribute("totalPages", lichSuPage.getTotalPages());
        return "list/QuanLyDatPhong/lichsudatphong";

    }
}
