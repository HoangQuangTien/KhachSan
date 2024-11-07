package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.*;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public TaiKhoanDTO saveTk(TaiKhoanDTO taiKhoanDTO) {
        TaiKhoan taiKhoan = modelMapper.map(taiKhoanDTO, TaiKhoan.class);
        taiKhoan = taiKhoanRepo.save(taiKhoan);
        return modelMapper.map(taiKhoan, TaiKhoanDTO.class);
    }

    //dùng cho các dto
    public TaiKhoanDTO findByTenDangNhap(String tenDangNhap) {
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(tenDangNhap);
        System.out.println("Tìm kiếm tài khoản với tên đăng nhập: " + tenDangNhap); // Debug

        if (taiKhoan != null) {
            System.out.println("Tài khoản tồn tại: " + taiKhoan.getTenDangNhap());

            // Chuyển đổi thông tin nhân viên sang DTO nếu có
            NhanVien nhanVien = taiKhoan.getNhanVien();
            NhanVienDTO nhanVienDTO = nhanVien != null ? convertToDTO(nhanVien) : null;
            if (nhanVienDTO != null) {
                System.out.println("Ảnh nhân viên: " + nhanVienDTO.getImg());
                System.out.println("Họ tên nhân viên: " + nhanVienDTO.getHoTen());
            } else {
                System.out.println("Không tìm thấy nhân viên cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Chuyển đổi thông tin khách hàng sang DTO nếu có
            KhachHang khachHang = taiKhoan.getKhachHang();
            KhachHangDTO khachHangDTO = khachHang != null ? convertToKhachHangDTO(khachHang) : null;
            if (khachHangDTO != null) {
                System.out.println("Họ tên khách hàng: " + khachHangDTO.getHoVaTen());
                System.out.println("Số điện thoại khách hàng: " + khachHangDTO.getSoDienThoai());
            } else {
                System.out.println("Không tìm thấy khách hàng cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Kiểm tra trạng thái
            if (nhanVienDTO == null && khachHangDTO == null) {
                throw new DisabledException("Tài khoản không hoạt động");
            }

            if (nhanVienDTO != null && !nhanVienDTO.getTrangThai()) {
                throw new DisabledException("Nhân viên không hoạt động");
            }

            if (khachHangDTO != null) {
                System.out.println("Trạng thái khách hàng: " + khachHangDTO.getDeletedAt());
                if (khachHangDTO.getDeletedAt()) {
                    throw new DisabledException("Khách hàng không hoạt động");
                }
            }


            // Tạo TaiKhoanDTO với thông tin nhân viên và khách hàng (nếu có)
            return new TaiKhoanDTO(
                    taiKhoan.getTenDangNhap(),
                    taiKhoan.getMatKhau(),
                    nhanVienDTO, // Bao gồm thông tin nhân viên
                    khachHangDTO, // Bao gồm thông tin khách hàng
                    taiKhoan.getChiTietVaiTros().stream()
                            .map(chiTietVaiTro -> new ChiTietVaiTroDTO(
                                    chiTietVaiTro.getIdChiTietVaiTro(),
                                    chiTietVaiTro.getMaChoTietVaiTro(),
                                    new VaiTroDTO(chiTietVaiTro.getVaiTro().getIdVaiTro(),
                                            chiTietVaiTro.getVaiTro().getMaVaiTro(),
                                            chiTietVaiTro.getVaiTro().getTenVaiTro())
                            ))
                            .collect(Collectors.toSet())
            );
        } else {
            System.out.println("Không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap);
            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + tenDangNhap);
        }
    }




    //convert to dto
    public NhanVienDTO convertToDTO(NhanVien nhanVien) {
        if (nhanVien == null) {
            return null;
        }

        return new NhanVienDTO(
                nhanVien.getIdNhanVien(),
                nhanVien.getEmail(),
                nhanVien.getSoDienThoai(),
                nhanVien.getMaNhanVien(),
                nhanVien.getImg(),
                nhanVien.getHoTen(),
                nhanVien.getNgaySinh(),
                nhanVien.getGioiTinh(),
                nhanVien.getLiDo(),
                nhanVien.getTrangThai(),
                nhanVien.getThanhPho(),
                nhanVien.getQuanHuyen(),
                nhanVien.getPhuongXa()  // Đảm bảo rằng bạn đang lấy đúng thuộc tính này
        );
    }

    //convert to dto khachHang
    public KhachHangDTO convertToKhachHangDTO(KhachHang khachHang) {
        if (khachHang == null) {
            return null;
        }
        return new KhachHangDTO(
                khachHang.getId(),
                khachHang.getMaKhachHang(),
                khachHang.getHoVaTen(),
                khachHang.getEmail(),
                khachHang.isGioiTinh(),
                khachHang.getSoDienThoai(),
                khachHang.getCreatedAt(),
                khachHang.getUpdatedAt(),
                khachHang.isDeletedAt(),
                khachHang.getTaiKhoan().getTenDangNhap(),
                khachHang.getTaiKhoan().getMatKhau()
        );
    }

    //dùng cho các trường DTO
    public TaiKhoanDTO getTaiKhoanTuSession() {
        // Lấy thông tin tài khoản từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                TaiKhoanDTO taiKhoanDTO = findByTenDangNhap(username); // Lấy TaiKhoanDTO từ tên đăng nhập

                if (taiKhoanDTO != null) {
                    return taiKhoanDTO; // Trả về thông tin tài khoản
                } else {
                    // Xử lý trường hợp không tìm thấy tài khoản
                    System.out.println("Không tìm thấy tài khoản cho tên đăng nhập: " + username);
                }
            }
        } else {
            System.out.println("Người dùng không được xác thực hoặc không có thông tin người dùng.");
        }

        return null; // Không có thông tin người dùng
    }


    //dùng cho các entity
    public TaiKhoan findByTenDanhNhap(String tenDangNhap) {
        // Tìm tài khoản theo tên đăng nhập
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(tenDangNhap);

        if (taiKhoan != null) {
            System.out.println("Tài khoản tồn tại: " + taiKhoan.getTenDangNhap());

            NhanVien nhanVien = taiKhoan.getNhanVien();

            if (nhanVien != null) {
                System.out.println("Ảnh: " + nhanVien.getImg());
                System.out.println("Họ tên: " + nhanVien.getHoTen());
            } else {
                System.out.println("Không tìm thấy nhân viên cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            KhachHang khachHang = taiKhoan.getKhachHang();
            if (khachHang != null){
                System.out.println("idKhachHang: "+khachHang.getId());
            }else{
                System.out.println("Không tìm thấy khách hàng cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Trả về đối tượng TaiKhoan
            return taiKhoan;
        } else {
            System.out.println("Không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap);
            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + tenDangNhap);
        }
    }

    public TaiKhoan getTaiKhoanTuSession1() {
        // Lấy thông tin tài khoản từ SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findByTenDanhNhap(username); // Lấy TaiKhoan từ tên đăng nhập
        } else {
            return null; // Không có thông tin người dùng
        }
    }


}
