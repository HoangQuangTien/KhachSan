package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepo extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findBysoDienThoai(String soDienThoai);
    Optional<NhanVien> findBymaNhanVien(String maNhanVien);
    Optional<NhanVien> findByemail(String email);
    @Query("SELECT n FROM NhanVien n WHERE "
            + "(n.soDienThoai LIKE %:keyword% OR n.hoTen LIKE %:keyword%)")
    Page<NhanVien> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM NhanVien n WHERE "
            + "(n.soDienThoai LIKE %:keyword% OR n.hoTen LIKE %:keyword%) AND n.trangThai = :trangThai")
    Page<NhanVien> findByKeywordAndTrangThai(@Param("keyword") String keyword, @Param("trangThai") boolean trangThai, Pageable pageable);


    NhanVien findTopByOrderByIdNhanVienDesc();
}
