package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
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
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private TaiKhoanService taiKhoanService;


    @GetMapping
    public String login() {
        return "list/DangNhap/login";
    }

    @PostMapping()
    public String login(@Valid @ModelAttribute TaiKhoanDTO taiKhoanDTO, Model model) {
        String tenDangNhap = taiKhoanDTO.getTenDangNhap();
        String matKhau = taiKhoanDTO.getMatKhau();

        TaiKhoanDTO foundTaiKhoan = taiKhoanService.findByTenDangNhap(tenDangNhap);

        // Kiểm tra nếu tài khoản không tồn tại
        if (foundTaiKhoan == null) {
            model.addAttribute("error", "Tên đăng nhập không tồn tại.");
            return "list/DangNhap/login";
        }

        // Kiểm tra mật khẩu có khớp không
        if (!foundTaiKhoan.getMatKhau().equals(matKhau)) {
            model.addAttribute("error", "Mật khẩu không chính xác.");
            return "list/DangNhap/login";
        }

        Set<ChiTietVaiTroDTO> roles = foundTaiKhoan.getChiTietVaiTros();


        // Lấy vai trò đầu tiên
        String role = roles.stream()
                .map(chiTietVaiTroDto -> chiTietVaiTroDto.getVaiTroDTO().getTenVaiTro())
                .findFirst()
                .orElse(null);

        // Log giá trị của role
        System.out.println("Vai trò: " + role);

        // Logic để chuyển hướng dựa trên vai trò
        String redirectUrl = switch (role) {
            case "ADMIN" -> "/thongke";
            case "EMPLOYEE" -> "/datphongs";
            case "CUSTOMER" -> "/khach-hang";
            default -> "/khach-hang"; // Mặc định nếu vai trò không xác định
        };

        // Nếu hợp lệ, thực hiện đăng nhập và chuyển hướng đến URL dựa trên vai trò
        return "redirect:" + redirectUrl;
    }


}
