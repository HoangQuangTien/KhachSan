package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.NguoiDiCung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NguoiDiCungRepo extends JpaRepository<NguoiDiCung, Integer> {
    Optional<NguoiDiCung> findByTenNguoiDiCungAndSoCmnd(String tenNguoiDiCung, String soCmnd);
    @Query("SELECT COUNT(n) FROM NguoiDiCung n WHERE n.datPhong.idDatPhong = :datPhongId")
    int countByDatPhongId(@Param("datPhongId") Integer datPhongId);
    List<NguoiDiCung> findByDatPhong_IdDatPhong(Integer idDatPhong);
    boolean existsBySoCmnd(String cccd);


    @Modifying
    @Transactional
    @Query("DELETE FROM NguoiDiCung ndc WHERE ndc.datPhong.idDatPhong = :idDatPhong")
    void xoaTatCaCCCDTheoIdDatPhong(@Param("idDatPhong") Long idDatPhong);
}
