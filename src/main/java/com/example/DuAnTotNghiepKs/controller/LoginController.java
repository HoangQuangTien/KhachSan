package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller

public class LoginController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
//        // Ví dụ: Lưu roomId vào session trước khi điều hướng đến trang đăng nhập
//        session.setAttribute("roomId", roomId);
        return "list/DangNhap/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute TaiKhoanDTO taiKhoanDTO, Model model, HttpSession session) {
        String tenDangNhap = taiKhoanDTO.getTenDangNhap();
        String matKhau = taiKhoanDTO.getMatKhau();

        // Tìm tài khoản
        TaiKhoanDTO foundTaiKhoan = taiKhoanService.findByTenDangNhap(tenDangNhap);

        if (foundTaiKhoan == null) {
            model.addAttribute("error", "Tên đăng nhập không tồn tại.");
            return "list/DangNhap/login";
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(matKhau, foundTaiKhoan.getMatKhau())) {
            model.addAttribute("error", "Mật khẩu không chính xác.");
            return "list/DangNhap/login";
        }

        // Lấy thông tin khách hàng
        KhachHangDTO khachHangDTO = foundTaiKhoan.getKhachHangDTO();
        if (khachHangDTO == null) {
            model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
            return "list/DangNhap/login";
        }

        // Lưu thông tin người dùng vào session
//        session.setAttribute("loggedInUser", khachHangDTO);

        // Lấy roomId từ session
//        Integer roomId = (Integer) session.getAttribute("roomId");

        // Chuyển hướng đến trang chi tiết phòng nếu roomId có
//        if (roomId != null) {
//            return "redirect:/showRoomDetailPhong?roomId=" + roomId;
//        }

        return "redirect:/"; // Trở về trang chính
    }





}
