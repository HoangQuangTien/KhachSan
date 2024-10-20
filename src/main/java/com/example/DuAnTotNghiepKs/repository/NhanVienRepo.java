package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO; // Cập nhật đường dẫn đúng

import java.util.List;
import java.util.Optional;


@Repository
public interface NhanVienRepo extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findBysoDienThoai(String soDienThoai);
    Optional<NhanVien> findBymaNhanVien(String maNhanVien);
    Optional<NhanVien> findByemail(String email);

    // Trong repository
    @Query("SELECT n FROM NhanVien n WHERE n.soDienThoai LIKE %:keyword% OR n.hoTen LIKE %:keyword% OR n.email LIKE %:keyword% ")
    Page<NhanVien> searchEmployees(@Param("keyword") String keyword,Pageable pageable);

    @Query("SELECT n FROM NhanVien n WHERE n.trangThai = :trangThai")
    Page<NhanVien> filterTrangThai(@Param("trangThai") Boolean trangThai, Pageable pageable);


    @Query("SELECT n FROM NhanVien n WHERE "
            + "(n.soDienThoai LIKE %:keyword% OR n.hoTen LIKE %:keyword%) AND n.trangThai = :trangThai")
    Page<NhanVien> findByKeywordAndTrangThai(@Param("keyword") String keyword, @Param("trangThai") boolean trangThai, Pageable pageable);


    NhanVien getOne(Integer id);

    NhanVien findTopByOrderByIdNhanVienDesc();


    //validate add
    Boolean existsByEmail(String email);


    Boolean existsBySoDienThoai(String soDienThoai);

    //validate update
    @Query("SELECT n FROM NhanVien n WHERE n.email = ?1")
    List<Tuple> findByEmail(String email);

    @Query("SELECT n FROM NhanVien n WHERE n.soDienThoai = ?1")
    List<Tuple> findBySoDienThoai(String soDienThoai);


    List<NhanVien> findByHoTenContaining(String hoTen);

}
