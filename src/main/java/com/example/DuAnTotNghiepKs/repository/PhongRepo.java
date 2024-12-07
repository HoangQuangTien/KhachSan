package com.example.DuAnTotNghiepKs.repository;


import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.Phong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PhongRepo extends JpaRepository<Phong, Integer> {

    Page<Phong> findByLoaiPhong_IdLoaiPhongAndTinhTrang(Integer idLoaiPhong, Boolean tinhTrang, Pageable pageable);
    Page<Phong> findByLoaiPhong_IdLoaiPhong(Integer idLoaiPhong, Pageable pageable);
    Page<Phong> findByTinhTrang(Boolean tinhTrang, Pageable pageable);
    Page<Phong> findAll(Pageable pageable);
    Optional<Phong> findById(Integer idPhong);
    // Thêm phương thức đếm số phòng đang hoạt động
    long countByTinhTrang(boolean tinhTrang);


    long countByTrangThai(Boolean trangThai);


    List<Phong> findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(Integer loaiPhongId);

    List<Phong> findByLoaiPhongIdLoaiPhong(Integer loaiPhongId);



    boolean existsByMaPhong(String maPhong);

    long countByTrangThaiAndTinhTrang(Boolean trangThai, Boolean tinhTrang);


    @Query("SELECT p FROM Phong p WHERE p.maPhong LIKE %:query% OR p.tenPhong LIKE %:query%")
    List<Phong> searchByMaOrTenPhong(@Param("query") String query);

    Optional<Phong> findByTenPhong(String tenPhong);


    @Query("SELECT COUNT(p) FROM Phong p WHERE p.loaiPhong.idLoaiPhong = :idLoaiPhong")
    int countByLoaiPhongId(@Param("idLoaiPhong") Integer idLoaiPhong);


    @Query("SELECT p FROM Phong p WHERE p.idPhong != :currentRoomId AND p.idPhong NOT IN " +
            "(SELECT dp.phong.idPhong FROM DatPhong dp WHERE " +
            "(dp.ngayNhan < :endDate AND dp.ngayTra > :startDate))")
    List<Phong> findAvailableRoomsForTimeRange(@Param("currentRoomId") Integer currentRoomId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Phong p WHERE p.trangThai = true AND p.idPhong NOT IN (" +
            "SELECT dp.phong.idPhong FROM DatPhong dp WHERE " +
            "((dp.ngayCheckIn <= :ngayTra AND dp.ngayTra >= :ngayNhan))" +
            ") AND p.loaiPhong.soNguoiToiDa = :soLuongNguoi")
    List<Phong> findAvailablePhongs(@Param("ngayNhan") LocalDateTime ngayNhan,
                                    @Param("ngayTra") LocalDateTime ngayTra,
                                    @Param("soLuongNguoi") Integer soLuongNguoi);


    @Query("SELECT p FROM Phong p WHERE p.trangThai = true")
    List<Phong> findByTrangThai(Boolean trangThai);


    boolean existsByTenPhong(String tenPhong);

    @Query("SELECT MAX(p.maPhong) FROM Phong p")
    String findMaxMaPhong();


//    @Query(value = "SELECT p.* " +
//            "FROM Phong p " +
//            "JOIN LoaiPhong lp ON p.id_loai_phong = lp.id_loai_phong " +
//            "WHERE lp.so_nguoi_toi_da >= :soNguoi " +
//            "AND p.id_phong NOT IN ( " +
//            "  SELECT dp.id_phong " +
//            "  FROM DatPhong dp " +
//            "  WHERE (dp.ngay_nhan_phong BETWEEN :startDate AND :endDate) " +
//            "     OR (dp.ngay_tra_phong BETWEEN :startDate AND :endDate) " +
//            "     OR (:startDate BETWEEN dp.ngay_nhan_phong AND dp.ngay_tra_phong) " +
//            "     OR (:endDate BETWEEN dp.ngay_nhan_phong AND dp.ngay_tra_phong) " +
//            ") " +
//            "AND p.trang_thai = 1"+
//            "ORDER BY p.id_phong " +
//            "OFFSET 0 ROWS FETCH NEXT :soLuongPhongCanTim ROWS ONLY",
//            nativeQuery = true)
//    List<Phong> findAvailableRoomsWithMaxGuests(@Param("startDate") LocalDateTime startDate,
//                                                @Param("endDate") LocalDateTime endDate,
//                                                @Param("soNguoi") Integer soNguoi,
//                                                @Param("soLuongPhongCanTim") Integer soLuongPhongCanTim);


    @Query(value = "SELECT p.* " +
            "FROM phong p " +
            "WHERE p.trang_thai = 1 " +
            "  AND p.id_phong NOT IN (" +
            "      SELECT dp.id_phong " +
            "      FROM DatPhong dp " +
            "      WHERE dp.ngay_nhan_phong < :endDate " +
            "        AND dp.ngay_tra_phong > :startDate " +
            "        AND dp.tinh_trang != 'Đã Hủy' " +
            "        AND dp.trang_thai != 1" +
            "  )", nativeQuery = true)
    List<Phong> findAvailableRooms(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



}

