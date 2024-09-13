package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.DTO.DatPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface DatPhongRepo extends JpaRepository<DatPhong,Integer> {
    boolean existsByMaDatPhong(String maDatPhong);

    List<DatPhong> findByTinhTrangFalse();

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE MONTH(dp.ngayNhan) = :month AND YEAR(dp.ngayNhan) = :year")
    Double findRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE MONTH(dp.ngayNhan) BETWEEN :startMonth AND (:startMonth + 3) AND YEAR(dp.ngayNhan) = :year")
    Double calculateRevenueForFourMonths(@Param("startMonth") int startMonth, @Param("year") int year);

    @Query("SELECT SUM(dp.tongTien) FROM DatPhong dp WHERE YEAR(dp.ngayNhan) = :year")
    Double findRevenueByYear(@Param("year") int year);

    @Query("SELECT COUNT(dp) FROM DatPhong dp")
    Long countTotalBookings();

    List<DatPhong> findByNgayNhanBetween(Date startDate, Date endDate);


    @Query(value = "SELECT dp1.id_phong AS phongId, \n" +
            "       p.ten_phong AS tenPhong,  -- Thêm tên phòng\n" +
            "       dp1.ngay_tra_phong AS lastCheckOut, \n" +
            "       dp2.ngay_nhan_phong AS nextCheckIn, \n" +
            "       DATEDIFF(day, dp1.ngay_tra_phong, dp2.ngay_nhan_phong) AS idleDays \n" +
            "FROM DatPhong dp1 \n" +
            "LEFT JOIN DatPhong dp2 \n" +
            "ON dp1.id_phong = dp2.id_phong \n" +
            "AND dp2.ngay_nhan_phong > dp1.ngay_tra_phong \n" +
            "LEFT JOIN Phong p \n" +
            "ON dp1.id_phong = p.id_phong  -- JOIN với bảng Phong\n" +
            "WHERE dp1.ngay_tra_phong IS NOT NULL \n" +
            "AND dp2.ngay_nhan_phong IS NOT NULL \n" +
            "ORDER BY dp1.id_phong, dp1.ngay_tra_phong;", nativeQuery = true)
    List<Object[]> findIdleRoomDays();



    @Query(value = "WITH PhongDatNhieuNhat AS (\n" +
            "    SELECT \n" +
            "        p.id_phong,\n" +
            "        p.ten_phong,\n" +
            "        COUNT(dp.id_dat_phong) AS so_lan_dat,\n" +
            "        ROW_NUMBER() OVER (ORDER BY COUNT(dp.id_dat_phong) DESC) AS row_num\n" +
            "    FROM DatPhong dp\n" +
            "    JOIN phong p ON dp.id_phong = p.id_phong\n" +
            "    GROUP BY p.id_phong, p.ten_phong\n" +
            ")\n" +
            "SELECT id_phong, ten_phong, so_lan_dat\n" +
            "FROM PhongDatNhieuNhat\n" +
            "WHERE row_num <= 3;",nativeQuery = true)
    List<Object[]> findTopPhongDuocDatNhieuNhat();

}
