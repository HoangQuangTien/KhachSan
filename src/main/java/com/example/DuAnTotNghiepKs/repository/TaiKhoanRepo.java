package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiKhoanRepo extends JpaRepository<TaiKhoan, Integer> {
    TaiKhoan findByTenDangNhap(String tenDangNhap);


}