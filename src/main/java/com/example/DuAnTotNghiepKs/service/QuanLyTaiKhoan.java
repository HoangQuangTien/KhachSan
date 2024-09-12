//package com.example.DuAnTotNghiepKs.service;
//
//import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
//import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class QuanLyTaiKhoan {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private TaiKhoanRepo taiKhoanRepository;
//
//    public void taoTaiKhoan(String tenDangNhap, String matKhau, String vaiTro) {
//        String matKhauMaHoa = passwordEncoder.encode(matKhau); // Mã hóa mật khẩu
//        TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, matKhauMaHoa, vaiTro);
//        taiKhoanRepository.save(taiKhoan);
//    }
//}
