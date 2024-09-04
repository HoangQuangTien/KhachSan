package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DatPhongRepo extends JpaRepository<DatPhong,Integer> {
    boolean existsByMaDatPhong(String maDatPhong);

    List<DatPhong> findByTinhTrangFalse();

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE MONTH(dp.ngayNhan) = :month AND YEAR(dp.ngayNhan) = :year")
    Double findRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE YEAR(dp.ngayNhan) = :year")
    Double findRevenueByYear(@Param("year") int year);
}
