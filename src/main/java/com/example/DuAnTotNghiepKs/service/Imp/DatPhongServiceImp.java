package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;

import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.repository.ThamSoRepo;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DatPhongServiceImp implements DatPhongService {

    @Autowired
    private DatPhongRepo datPhongRepository;

    @Autowired
    private PhongRepo phongRepository;

    @Autowired
    private ThamSoRepo thamSoRepo;




    @Override
    public DatPhong saveDatPhong(DatPhong datPhong) {
        // Cập nhật trạng thái phòng là "đã đặt"
        Phong phong = datPhong.getPhong();
        if (existsByMaDatPhong(datPhong.getMaDatPhong())) {
            throw new RuntimeException("Mã đặt phòng đã tồn tại.");
        }
        if (phong != null) {
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

//    @Override
//    public DatPhong findTopByOrderByIdDatPhongDesc(){
//        return datPhongRepository.findTopByOrderByIdDatPhongDesc();
//    }

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
        return datPhongRepository.findByTinhTrang("Chưa Checkin");
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
    public Map<String, Object> getRevenueForFourMonths(int startMonth, int year) {
        Map<String, Object> result = new HashMap<>();
        List<Double> revenues = new ArrayList<>();
        List<String> months = new ArrayList<>();

        for (int i = startMonth; i < startMonth + 4; i++) {
            Double revenue = datPhongRepository.findRevenueByMonthAndYear(i, year);
            revenues.add(revenue != null ? revenue : 0.0);
            months.add("Tháng " + i);
        }

        result.put("revenues", revenues);
        result.put("months", months);
        return result;
    }

    @Override
    public Map<String, Object> getRevenueByYear(int year) {
        List<Double> revenues = new ArrayList<>();
        List<String> months = new ArrayList<>();

        List<Object[]> results = datPhongRepository.findRevenueByYear(year);
        for (Object[] result : results) {
            int month = (Integer) result[0];
            Double revenue = (Double) result[1];
            revenues.add(revenue != null ? revenue : 0.0);
            months.add("Tháng " + month);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("revenues", revenues);
        data.put("months", months);
        return data;
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


    @Override
    public List<DatPhong> findByPhongAndThoiGian(Integer idPhong, LocalDateTime ngayNhan, LocalDateTime ngayTra) {
        return datPhongRepository.findByPhongAndThoiGian(idPhong, ngayNhan,ngayTra);
    }



    @Override
    public DatPhong findTopByOrderByIdDatPhongDesc(){
        return datPhongRepository.findTopByOrderByIdDatPhongDesc();
    }


    @Override
    public Page<DatPhong> getDatPhongsDaCoc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return datPhongRepository.findAllByDaCoc(pageable);
    }


    @Override
    public long getThoiGianChoPhepDatPhong() {
        Long idThamSo = 3L; // ID cho tham số thời gian chuyển trạng thái
        return thamSoRepo.findById(idThamSo)
                .map(thamSo -> Long.parseLong(thamSo.getGiaTri()) * 60 * 1000)
                .orElse(0L); // Trả về 0 nếu không tìm thấy tham số
    }



}
