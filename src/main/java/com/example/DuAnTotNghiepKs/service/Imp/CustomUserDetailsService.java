//package com.example.DuAnTotNghiepKs.service.Imp;
//
//import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
//import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private TaiKhoanRepo taiKhoanRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
//        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
//
//        if (taiKhoan == null) {
//            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + tenDangNhap);
//        }
//
//        // Trả về đối tượng User của Spring Security
//        return new User(
//                taiKhoan.getTenDangNhap(),
//                taiKhoan.getMatKhau(),
//                Collections.singletonList(new SimpleGrantedAuthority(taiKhoan.getVaiTro()))
//        );
//    }
//}
