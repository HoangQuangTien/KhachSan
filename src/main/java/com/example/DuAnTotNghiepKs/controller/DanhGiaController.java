package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
public class DanhGiaController {
    private final TaiKhoanService taiKhoanService;

    public DanhGiaController(TaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }




    @GetMapping("/danh-gia")
    public String hienThi(Model model) {
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }
        return "list/DanhGia/DanhGia";
    }
}
