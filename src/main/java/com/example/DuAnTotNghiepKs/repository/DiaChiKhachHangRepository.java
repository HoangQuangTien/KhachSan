package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.DiaChiKhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang,Integer> {
    @Query("SELECT dckh FROM DiaChiKhachHang dckh JOIN FETCH dckh.idKhachHang")
    List<DiaChiKhachHang> getAllJoinFetch();

    List<DiaChiKhachHang> findByIdKhachHang_Id(Integer id);
    Page<DiaChiKhachHang> findByIdKhachHang_Id(Integer idKhachHang, Pageable pageable);
}
