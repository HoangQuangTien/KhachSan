package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.EmailService;
import com.example.DuAnTotNghiepKs.service.JWTService;
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


    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmailService emailService;

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



    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "list/QuenMatKhau/forgotPassword";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(required = true) String token) {
        try {
            jwtService.extractUsername(token);
        } catch (Exception e) {
            return "redirect:/forgot-password?error=false";
        }
        return "list/QuenMatKhau/resetPassword";
    }



    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam(required = true) String token, @RequestParam String password) {
        try {
            String tenDangNhap = jwtService.extractUsername(token);
            TaiKhoanDTO foundTaiKhoan = taiKhoanService.findByTenDangNhap(tenDangNhap);
            if (foundTaiKhoan != null) {
                foundTaiKhoan.setMatKhau(passwordEncoder.encode(password));
                foundTaiKhoan.setKhachHangDTO(null);
                foundTaiKhoan.setNhanVienDTO(null);
                foundTaiKhoan.setChiTietVaiTros(null);
                System.out.println(foundTaiKhoan);
                taiKhoanService.saveTk(foundTaiKhoan);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Đã có lỗi xảy ra"));
        }
        return ResponseEntity.ok(Map.of("message", "Thay đổi mật khẩu thành công"));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @ModelAttribute KhachHangDTO khachHangDTO, Model model,
                                            HttpSession session) {
        String email = khachHangDTO.getEmail();
        TaiKhoanDTO foundTaiKhoan = taiKhoanService.findByEmail(email);
        if (foundTaiKhoan == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không tồn tại"));
        }
        if (foundTaiKhoan.getNhanVienDTO() != null) {
            try {
                emailService.sendEmailForgotPassword(foundTaiKhoan.getNhanVienDTO().getEmail(),
                        "Thiết Lại Mật Khẩu Đăng Nhập", foundTaiKhoan, jwtService.generateTokenNV(foundTaiKhoan));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đã có lỗi xảy ra trong quá trình gửi mail"));
            }
        } else {
            try {
                emailService.sendEmailForgotPassword(foundTaiKhoan.getKhachHangDTO().getEmail(),
                        "Thiết Lại Mật Khẩu Đăng Nhập", foundTaiKhoan, jwtService.generateTokenKH(foundTaiKhoan));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đã có lỗi xảy ra trong quá trình gửi mail"));
            }
        }
        // Tìm tài khoản
        return ResponseEntity.ok(Map.of("message", " Mã xác minh đã được gửi đến địa chỉ email.\nVui lòng xác minh!"));
    }


}
