package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;

import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DatPhongServiceImp implements DatPhongService {

    @Autowired
    private DatPhongRepo datPhongRepository;

    @Autowired
    private PhongRepo phongRepository;

    @Override
    public DatPhong saveDatPhong(DatPhong datPhong) {
        // Cập nhật trạng thái phòng là "đã đặt"
        Phong phong = datPhong.getPhong();
        if (existsByMaDatPhong(datPhong.getMaDatPhong())) {
            throw new RuntimeException("Mã đặt phòng đã tồn tại.");
        }
        if (phong != null && phong.getTrangThai()) {
            phong.setTrangThai(false); // Đặt phòng, chuyển trạng thái sang đã đặt
            phongRepository.save(phong);
            return datPhongRepository.save(datPhong);
        } else {
            throw new RuntimeException("Phòng không còn trống để đặt.");
        }
    }

    @Override
    public List<DatPhong> getAllDatPhong() {
        return datPhongRepository.findAll();
    }

    @Override
    public DatPhong getDatPhongById(int id) {
        return datPhongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt phòng với ID: " + id));
    }

    @Override
    public void deleteDatPhong(int id) {
        DatPhong datPhong = getDatPhongById(id);
        if (datPhong != null) {
            Phong phong = datPhong.getPhong();
            if (phong != null) {
                phong.setTrangThai(true); // Khi xóa đặt phòng, trạng thái phòng trở lại là "còn trống"
                phongRepository.save(phong);
            }
            datPhongRepository.delete(datPhong);
        } else {
            throw new RuntimeException("Không tìm thấy thông tin đặt phòng để xóa.");
        }
    }

    @Override
    public List<Phong> findAvailablePhongByLoaiPhong(Integer loaiPhongId) {
        // Lấy danh sách phòng còn trống theo loại phòng
        return phongRepository.findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(loaiPhongId);
    }


    @Override
    public Page<DatPhong> getDatPhongPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayNhan").descending());
        return datPhongRepository.findAll(pageable);
    }



    @Override
    public boolean existsByMaDatPhong(String maDatPhong) {
        return datPhongRepository.existsByMaDatPhong(maDatPhong);
    }

    @Override
    public List<DatPhong> getDatPhongChuaCheckIn() {
        return datPhongRepository.findByTinhTrangFalse();
    }

    @Override
    public DatPhong saveDatPhong1(DatPhong datPhong) {
        if (datPhong.getIdDatPhong() != null) { // Đang cập nhật
            // Kiểm tra nếu có mã đặt phòng trùng nhưng không phải cùng ID
            DatPhong existingDatPhong = datPhongRepository.findById(datPhong.getIdDatPhong()).orElse(null);
            if (existingDatPhong != null && !existingDatPhong.getMaDatPhong().equals(datPhong.getMaDatPhong())) {
                throw new IllegalArgumentException("Mã đặt phòng đã tồn tại.");
            }
        }

        return datPhongRepository.save(datPhong);
    }


    @Override
    public double getRevenueByMonth(int month, int year) {
        return datPhongRepository.findRevenueByMonth(month, year);
    }

    @Override
    public double getRevenueByFourMonths(int startMonth, int year) {
        return datPhongRepository.calculateRevenueForFourMonths(startMonth, year);
    }

    @Override
    public double getRevenueByYear(int year) {
        return datPhongRepository.findRevenueByYear(year);
    }

    @Override
    public Long getBookingCount() {
        // Triển khai logic để tính tổng số phòng đã đặt từ cơ sở dữ liệu
        return datPhongRepository.countTotalBookings();
    }



    @Override
    // Thêm phương thức tính tổng số phòng Ngung hoạt động
    public long countActivePhongsFalse() {
        return phongRepository.countByTinhTrang(false);
    }


    @Override
    public List<IdleRoomDTO> getIdleRoomTimes() {
        List<Object[]> results = datPhongRepository.findIdleRoomDays();  // Giả sử phương thức này trả về List<Object[]>
        List<IdleRoomDTO> idleRoomList = new ArrayList<>();

        for (Object[] result : results) {
            Long phongId = ((Number) result[0]).longValue();  // Chuyển đổi từ Number sang Long
            String phongName = (String) result[1];
            Date lastCheckOut = (Date) result[2];  // Chuyển đổi từ Object sang Date
            Date nextCheckIn = (Date) result[3];  // Chuyển đổi từ Object sang Date
            Integer idleDays = ((Number) result[4]).intValue();  // Chuyển đổi từ Number sang Integer

            idleRoomList.add(new IdleRoomDTO(phongId, phongName,lastCheckOut, nextCheckIn, idleDays));
        }
        return idleRoomList;
    }


    @Override
    public List<Object[]> getTopPhongDuocDatNhieuNhat() {
        // Sử dụng PageRequest để giới hạn số kết quả trả về
        return datPhongRepository.findTopPhongDuocDatNhieuNhat();
    }


    @Override
    public Long countDistinctCustomers() {
        return datPhongRepository.countDistinctCustomers();
    }






}
