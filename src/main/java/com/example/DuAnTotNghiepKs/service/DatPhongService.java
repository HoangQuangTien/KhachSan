package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import org.springframework.data.domain.Page;


import java.util.List;

public interface DatPhongService {
    DatPhong saveDatPhong(DatPhong datPhong); // Lưu thông tin đặt phòng
    List<DatPhong> getAllDatPhong(); // Lấy tất cả thông tin đặt phòng
    DatPhong getDatPhongById(int id); // Lấy thông tin đặt phòng theo ID
    void deleteDatPhong(int id); // Xóa thông tin đặt phòng
    List<Phong> findAvailablePhongByLoaiPhong(Integer loaiPhongId); // Tìm các phòng còn trống theo loại phòng

    Page<DatPhong> getDatPhongPage(int page, int size);

    boolean existsByMaDatPhong(String maDatPhong);

    List<DatPhong> getDatPhongChuaCheckIn();


    DatPhong saveDatPhong1(DatPhong datPhong);

    double getRevenueByMonth(int month, int year);

    double getRevenueByFourMonths(int startMonth, int year);

    double getRevenueByYear(int year);

    Long getBookingCount();

    // Thêm phương thức tính tổng số phòng Ngung hoạt động
    long countActivePhongsFalse();

    List<IdleRoomDTO> getIdleRoomTimes();

    List<Object[]> getTopPhongDuocDatNhieuNhat();

//    // Thêm phương thức tính tổng số phòng Ngung hoạt động
//    long countActivePhongsFalse();
}
