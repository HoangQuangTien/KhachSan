package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.DatPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DatPhongService {
    DatPhong saveDatPhong(DatPhong datPhong); // Lưu thông tin đặt phòng
    List<DatPhong> getAllDatPhong(); // Lấy tất cả thông tin đặt phòng
    DatPhong getDatPhongById(int id); // Lấy thông tin đặt phòng theo ID
    void deleteDatPhong(int id); // Xóa thông tin đặt phòng
    List<Phong> findAvailablePhongByLoaiPhong(Integer loaiPhongId); // Tìm các phòng còn trống theo loại phòng

    Page<DatPhong> getDatPhongPage(int page, int size);

    boolean existsByMaDatPhong(String maDatPhong);

    List<DatPhong> getDatPhongChuaCheckIn();


    List<DatPhong> getDatPhongChuaXacNhan();

    List<DatPhong> getDatPhongChuaVaDaCheckIn();


//    List<DatPhong> getDatPhongChuaVaDaCheckIn1(Integer idKhachHang);

    DatPhong saveDatPhong1(DatPhong datPhong);

    double getRevenueByMonth(int month, int year);

//    double getRevenueByFourMonths(int startMonth, int year);

    Map<String, Object> getRevenueForFourMonths(int startMonth, int year);

//    double getRevenueByYear(int year);

    Double getRevenueBetweenDates(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRevenueByYear(int year);

    public DatPhong findTopByOrderByIdDatPhongDesc();

    Long getBookingCount();

    // Thêm phương thức tính tổng số phòng Ngung hoạt động
    long countActivePhongsFalse();

    List<IdleRoomDTO> getIdleRoomTimes();

    List<Object[]> getTopPhongDuocDatNhieuNhat();

    List<Object[]> getTopPhongDuocDatNhieuNhat1();

    Long countDistinctCustomers();

    List<DatPhong> findByPhongAndThoiGian(Integer idPhong, LocalDateTime ngayNhan, LocalDateTime ngayTra );

//    List<DatPhong> getDatPhongsDaCoc();

    Page<DatPhong> getDatPhongsDaCoc(int page, int size);



    long getThoiGianChoPhepDatPhong();

    DatPhong findById(Integer id);

    long countCancelledBookings();



    boolean xoaCCCD(Integer idDatPhong);

    void sendEmail(String to, String subject, String text);

    Map<String, Object> getRevenueByQuarter(int year, int startMonth, int endMonth);

    List<DatPhongDTO> findByKhachHang_Id(Integer id);

    List<DatPhongDTO> findByKhachHang_Id1(Integer id);


    List<DatPhongDTO> findByKhachHang_Id12(Integer id);

    DatPhong findById1(Integer id);



    boolean extendStay(Integer idDatPhong, LocalDateTime newEndDate);

    Map<String, Object> getRevenueByDay();

    void exportGuestReport(List<DatPhong> guests, String filePath) throws IOException;


//    // Thêm phương thức tính tổng số phòng Ngung hoạt động
//    long countActivePhongsFalse();


}
