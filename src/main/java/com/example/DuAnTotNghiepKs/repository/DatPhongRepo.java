package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatPhongRepo extends JpaRepository<DatPhong,Integer> {
    boolean existsByMaDatPhong(String maDatPhong);

    List<DatPhong> findByTinhTrangFalse();

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE MONTH(dp.ngayNhan) = :month AND YEAR(dp.ngayNhan) = :year")
    Double findRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(dp.tienCoc) FROM DatPhong dp WHERE MONTH(dp.ngayNhan) = :month AND YEAR(dp.ngayNhan) = :year")
    Double findRevenueByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT MONTH(dp.ngayNhan) AS month, COALESCE(SUM(dp.tongTien), 0) AS revenue FROM DatPhong dp WHERE YEAR(dp.ngayNhan) = :year GROUP BY MONTH(dp.ngayNhan)")
    List<Object[]> findRevenueByYear(@Param("year") int year);


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



    @Query(value = "SELECT COUNT(DISTINCT dp.id_khach_hang) AS total_customers\n" +
            "FROM DatPhong dp;",nativeQuery = true)
    Long countDistinctCustomers();


    @Query(value = "SELECT id_dat_phong,ma_dat_phong, id_khach_hang,id_phong,id_loai_phong,ngay_nhan_phong,ngay_tra_phong,cccd ,tinh_trang , COALESCE(tien_coc, 0.0) AS tien_coc ,\n" +
            "COALESCE(tien_con_lai, 0.0) AS tien_con_lai,COALESCE(tong_tien, 0.0) AS tong_tien\n" +
            "FROM DatPhong\n" +
            "WHERE ngay_nhan_phong BETWEEN '2024-01-01' AND '2024-12-31';", nativeQuery = true)
    List<DatPhong> findBookingsWithinDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);



        // Truy vấn để tìm các đơn đặt phòng theo id phòng và trùng thời gian
        @Query("SELECT dp FROM DatPhong dp WHERE dp.phong.idPhong = :idPhong AND " +
                "(:ngayNhan BETWEEN dp.ngayNhan AND dp.ngayTra OR " +
                ":ngayTra BETWEEN dp.ngayNhan AND dp.ngayTra OR " +
                "dp.ngayNhan BETWEEN :ngayNhan AND :ngayTra)")
        List<DatPhong> findByPhongAndThoiGian(@Param("idPhong") Integer idPhong,
                                              @Param("ngayNhan") Date ngayNhan,
                                              @Param("ngayTra") Date ngayTra);

    DatPhong findTopByOrderByIdDatPhongDesc();

//    @Query("SELECT SUM(b.tongTien) FROM DatPhong b WHERE b.phong.idPhong = :roomId")
//    float calculateTotalRevenueByRoom(@Param("roomId") Long roomId);

    DatPhong findTopByOrderByIdDatPhongDesc();

    Optional<DatPhong> findByPhongAndTinhTrang(Phong phong, Boolean tinhTrang);


}
