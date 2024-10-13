package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private TaiKhoanService taiKhoanService;


    @GetMapping
    public String login() {
        return "list/DangNhap/login";
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestParam String tenDangNhap, @RequestParam String matKhau) {
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.findByTenDangNhap(tenDangNhap);

        // Kiểm tra tài khoản có tồn tại không
        if (taiKhoanDTO == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản không tồn tại"));
        }

        // Kiểm tra mật khẩu
        if (!taiKhoanDTO.getMatKhau().equals(matKhau)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu không chính xác"));
        }

        // Lấy thông tin nhân viên từ TaiKhoanDTO
        NhanVienDTO nhanVienDTO = taiKhoanDTO.getNhanVienDTO();

        // Kiểm tra thông tin nhân viên
        if (nhanVienDTO == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thông tin nhân viên không tồn tại"));
        }
        // Chuyển hướng tới trang chủ với thông tin nhân viên
        return ResponseEntity.ok(Map.of("nhanVienDto", nhanVienDTO)); // Chỉ trả về thông tin nhân viên nếu cần
    }

}
