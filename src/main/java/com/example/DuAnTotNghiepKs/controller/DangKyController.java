package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.ChiTietVaiTro;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.entity.VaiTro;
import com.example.DuAnTotNghiepKs.repository.ChiTietVaiTroRepo;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import com.example.DuAnTotNghiepKs.repository.VaiTroRepo;
import com.example.DuAnTotNghiepKs.service.EmailService;
import com.example.DuAnTotNghiepKs.service.KhachHangService;


import org.eclipse.jetty.util.security.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/register")
public class DangKyController {

    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VaiTroRepo vaiTroRepo;
    @Autowired
    private ChiTietVaiTroRepo chiTietVaiTroRepo;
    @Autowired
    private TaiKhoanRepo taiKhoanRepo;
//    @GetMapping
//    public String login() {
//        return "list/DangKy/register";
//    }

    @PostMapping
    public ResponseEntity<?> register(@RequestParam String hoVaTen, @RequestParam String soDienThoai,
                                      @RequestParam Boolean gioiTinh, @RequestParam String email,
                                      @RequestParam String tenDangNhap,
                                      @RequestParam String matKhau) {

        // Kiểm tra tài khoản có tồn tại không
        if (khachHangService.existsByTenDangNhap(tenDangNhap)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản đã tồn tại"));
        }
        if (khachHangService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email này đã tồn tại"));
        }
        if (khachHangService.existsBySoDienThoai(soDienThoai)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại này đã tồn tại"));
        }

        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(khachHangService.createMaKH());
        khachHang.setEmail(email);
        khachHang.setSoDienThoai(soDienThoai);
        khachHang.setGioiTinh(gioiTinh);
        khachHang.setHoVaTen(hoVaTen);

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(passwordEncoder.encode(matKhau));
        khachHang.setTaiKhoan(taiKhoan);
        taiKhoanRepo.save(taiKhoan);

        //tạo vai trò
        VaiTro vaiTro = vaiTroRepo.findByIdVaiTro(3);
        //tạo chi tiết vai trò
        ChiTietVaiTro chiTietVaiTro = new ChiTietVaiTro();
        chiTietVaiTro.setMaChoTietVaiTro(generateMaChiTietVaiTro());
        chiTietVaiTro.setVaiTro(vaiTro);
        chiTietVaiTro.setTaiKhoan(taiKhoan);
        chiTietVaiTroRepo.save(chiTietVaiTro);


        khachHangService.insert(khachHang);
        try {
            emailService.sendEmailRegister(khachHang.getEmail(), "Đăng ký thành công", khachHang);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));
    }


    //genmaChiTietVaiTro
    private String generateMaChiTietVaiTro() {
        String prefix = "CTVT";
        ChiTietVaiTro lastCTVT = chiTietVaiTroRepo.findTopByOrderByIdChiTietVaiTroDesc();
        int nextId = lastCTVT != null ? (int) (lastCTVT.getIdChiTietVaiTro() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }
}
