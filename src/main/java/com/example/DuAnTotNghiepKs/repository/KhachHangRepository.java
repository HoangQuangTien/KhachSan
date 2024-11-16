package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {
    @Query("SELECT kh FROM KhachHang kh WHERE kh.hoVaTen LIKE %:query% OR kh.soDienThoai LIKE %:query%")
    List<KhachHang> searchByNameOrPhone(@Param("query") String query);

    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);

    KhachHang findByEmail(String email);
    KhachHang findBySoDienThoai(String soDienThoai);


    @Query(value = "SELECT * FROM KhachHang WHERE ten_dang_nhap = :tenDangNhap",nativeQuery = true)
    KhachHang findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

//    @Query("SELECT kh FROM KhachHang kh WHERE kh.trangThai = :status")
//    List<KhachHang> filterByStatus(@Param("status") String status);

    boolean existsByMaKhachHang(String maKhachHang);



}
