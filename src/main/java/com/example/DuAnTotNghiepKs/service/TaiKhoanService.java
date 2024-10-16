package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.DTO.VaiTroDTO;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @Autowired
    private ModelMapper modelMapper;

    public TaiKhoanDTO saveTk(TaiKhoanDTO taiKhoanDTO){
        TaiKhoan taiKhoan = modelMapper.map(taiKhoanDTO,TaiKhoan.class);
        taiKhoan = taiKhoanRepo.save(taiKhoan);
        return modelMapper.map(taiKhoan,TaiKhoanDTO.class);
    }

    //dùng cho các dto
    public TaiKhoanDTO findByTenDangNhap(String tenDangNhap) {
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(tenDangNhap);
        System.out.println("Tìm kiếm tài khoản với tên đăng nhập: " + tenDangNhap); // Debug

        if (taiKhoan != null) {
            System.out.println("Tài khoản tồn tại: " + taiKhoan.getTenDangNhap());

            NhanVien nhanVien = taiKhoan.getNhanVien();
            NhanVienDTO nhanVienDTO = null;

            if (nhanVien != null) {
                nhanVienDTO = convertToDTO(nhanVien); // Chuyển đổi sang NhanVienDTO
                System.out.println("Ảnh: " + nhanVienDTO.getImg());
                System.out.println("Họ tên: " + nhanVienDTO.getHoTen());
            } else {
                System.out.println("Không tìm thấy nhân viên cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Trả về đối tượng TaiKhoanDTO với tất cả các trường đúng kiểu
            return new TaiKhoanDTO(
                    taiKhoan.getTenDangNhap(),
                    taiKhoan.getMatKhau(),
                    nhanVienDTO, // Đảm bảo rằng bạn thêm nhanVienDTO vào đây
                    taiKhoan.getChiTietVaiTros().stream()
                            .map(chiTietVaiTro -> new ChiTietVaiTroDTO(
                                    chiTietVaiTro.getIdChiTietVaiTro(),
                                    chiTietVaiTro.getMaChoTietVaiTro(),
                                    new VaiTroDTO(chiTietVaiTro.getVaiTro().getIdVaiTro(),
                                            chiTietVaiTro.getVaiTro().getMaVaiTro(),
                                            chiTietVaiTro.getVaiTro().getTenVaiTro())
                            ))
                            .collect(Collectors.toSet()) // Đảm bảo rằng bạn đang trả về một Set<ChiTietVaiTroDTO>
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
                nhanVien.getSoDienThoai() ,
                nhanVien.getMaNhanVien(),
                nhanVien.getImg() ,
                nhanVien.getHoTen(),
                nhanVien.getNgaySinh(),
                nhanVien.getGioiTinh() ,
                nhanVien.getLiDo() ,
                nhanVien.getTrangThai(),
                nhanVien.getThanhPho() ,
                nhanVien.getQuanHuyen() ,
                nhanVien.getPhuongXa()  // Đảm bảo rằng bạn đang lấy đúng thuộc tính này
        );
    }

    //dùng cho các trường DTO
    public TaiKhoanDTO getTaiKhoanTuSession() {
        // Lấy thông tin tài khoản từ SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findByTenDangNhap(username); // Lấy TaiKhoanDTO từ tên đăng nhập
        } else {
            return null; // Không có thông tin người dùng
        }
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
