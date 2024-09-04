package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import com.example.DuAnTotNghiepKs.service.DiaChiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/quan-ly-khach-hang")
public class DiaChiKhachHangController {

    @Autowired
    private DiaChiKhachHangService diaChiKhachHangService;

    @PostMapping("/them-dia-chi")
    public String addAddress(@RequestParam("idKhachHang") Integer idKhachHang, @ModelAttribute DiaChiKhachHangDTO diaChiKhachHangDTO, Model model) {
        diaChiKhachHangDTO.setIdKhachHang(idKhachHang);
        diaChiKhachHangService.save(diaChiKhachHangDTO);
        return "redirect:/quan-ly-khach-hang-detail?id=" + idKhachHang + "&page=0";
    }
    @PostMapping("/cap-nhat-dia-chi")
    public String updateAddress(@RequestParam("id") Integer id, @ModelAttribute DiaChiKhachHangDTO diaChiKhachHangDTO) {
        // Lấy địa chỉ từ id
        DiaChiKhachHangDTO diaChi = diaChiKhachHangService.getOne(id);

        // Lấy idKhachHang từ địa chỉ
        Integer idKhachHang = diaChi.getIdKhachHang();

        // Cập nhật địa chỉ
        diaChiKhachHangService.update(id, diaChiKhachHangDTO);

        // Chuyển hướng về chi tiết của khách hàng
        return "redirect:/quan-ly-khach-hang-detail?id=" + idKhachHang;
    }


    @GetMapping("/dia-chi/{id}")
    @ResponseBody
    public DiaChiKhachHangDTO getAddress(@PathVariable Integer id) {
        return diaChiKhachHangService.getOne(id);
    }
}
