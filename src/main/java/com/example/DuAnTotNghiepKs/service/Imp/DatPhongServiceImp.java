package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.DatPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.IdleRoomDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;

import com.example.DuAnTotNghiepKs.repository.*;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class DatPhongServiceImp implements DatPhongService {

    @Autowired
    private DatPhongRepo datPhongRepository;

    @Autowired
    private PhongRepo phongRepository;

    @Autowired
    private DanhGiaRepo danhGiaRepo;

    @Autowired
    private ThamSoRepo thamSoRepo;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private JavaMailSender emailSender;



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


    public List<DatPhong> getDatPhongDaCheckInAndNgay(){
        return datPhongRepository.findByTinhTrang("Đã Checkin");
    }

    public List<DatPhong> getDatPhongAndNganBeetwen() {
        // Lấy ngày hiện tại
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 00:00 hôm nay
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1); // 23:59 hôm nay

        // Gọi repository với tình trạng "Đã Checkin"
        return datPhongRepository.findByTinhTrangAndNgayNhanBetween("Đã Checkin", startOfDay, endOfDay);
    }

    @Override
    public List<DatPhong> getDatPhongChuaXacNhan(){
        return datPhongRepository.findByTinhTrang("Chưa xác nhận");
    }

    @Override
    public List<DatPhong> getDatPhongChuaVaDaCheckIn() {
        boolean trangThai = false;
        return datPhongRepository.findByTinhTrangAndTrangThaiOrTinhTrangAndTrangThai("Chưa Checkin", trangThai, "Đã Checkin", trangThai);
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
            Double revenue = datPhongRepository.findRevenueByMonth(i, year);
            revenues.add(revenue != null ? revenue : 0.0);
            months.add("Tháng " + i);
        }

        result.put("revenues", revenues);
        result.put("months", months);
        return result;
    }

//    @Override
//    public Map<String, Object> getRevenueByYear(int year) {
//        List<Double> revenues = new ArrayList<>();
//        List<String> months = new ArrayList<>();
//
//        List<Object[]> results = datPhongRepository.findRevenueByYear(year);
//        for (Object[] result : results) {
//            int month = (Integer) result[0];
//            Double revenue = (Double) result[1];
//            revenues.add(revenue != null ? revenue : 0.0);
//            months.add("Tháng " + month);
//        }
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("revenues", revenues);
//        data.put("months", months);
//        return data;
//    }

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
    public List<Object[]> getTopPhongDuocDatNhieuNhat1() {
        // Sử dụng PageRequest để giới hạn số kết quả trả về
        return datPhongRepository.findTopPhongDuocDatNhieuNhat1();
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
        return datPhongRepository.findAllByDaCocAndDaCheckin(pageable);
    }

//    @Override
//    public Page<DatPhong> getDatPhongsDaCheckin(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return datPhongRepository.findAllByDaCheckin(pageable);
//    }


    @Override
    public long getThoiGianChoPhepDatPhong() {
        Long idThamSo = 3L; // ID cho tham số thời gian chuyển trạng thái
        return thamSoRepo.findById(idThamSo)
                .map(thamSo -> Long.parseLong(thamSo.getGiaTri()) * 60 * 1000)
                .orElse(0L); // Trả về 0 nếu không tìm thấy tham số
    }



    @Override
    public DatPhong findById(Integer id) {
        Optional<DatPhong> optionalDatPhong = datPhongRepository.findById(id);
        return optionalDatPhong.orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt phòng với ID: " + id));
    }


    @Override
    public long countCancelledBookings() {
        return datPhongRepository.countCancelledBookings();
    }


    @Override
    public boolean xoaCCCD(Integer idDatPhong) {
        Optional<DatPhong> datPhongOptional = datPhongRepository.findById(idDatPhong);
        if (datPhongOptional.isPresent()) {
            DatPhong datPhong = datPhongOptional.get();
            datPhong.setCccd(null); // Xóa CCCD
            datPhongRepository.save(datPhong); // Lưu thay đổi
            return true;
        }
        return false; // Không tìm thấy đặt phòng
    }

@Override
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true để gửi email dưới dạng HTML
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu cần
        }
    }


    @Override
    public Double getRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Log đầu vào để kiểm tra giá trị startDate và endDate
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        // Chuyển LocalDate thành LocalDateTime để so sánh với ngày và giờ trong cơ sở dữ liệu
        List<DatPhong> bookings = datPhongRepository.findByNgayNhanBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Kiểm tra nếu không có kết quả
        if (bookings.isEmpty()) {
            System.out.println("Không có đơn đặt phòng trong khoảng thời gian này.");
            return 0.0; // Nếu không có đơn đặt phòng, trả về 0 doanh thu
        }

        // Tính tổng doanh thu
        return bookings.stream()
                .mapToDouble(DatPhong::getTongTien)
                .sum();
    }
    @Override
    public Map<String, Object> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX); // Kết thúc ngày
        List<Object[]> rawData = datPhongRepository.findRevenueByDateRange(start, end);

        Map<String, Object> result = new HashMap<>();
        List<String> days = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        for (Object[] record : rawData) {
            days.add(record[0].toString()); // Sử dụng toString() thay vì ép kiểu trực tiếp
            revenues.add((Double) record[1]);
        }

        result.put("days", days);
        result.put("revenues", revenues);
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
    public Map<String, Object> getRevenueByQuarter(int year, int startMonth, int endMonth) {
        List<Object[]> rawData = datPhongRepository.findRevenueByQuarter(year, startMonth, endMonth);
        Map<String, Object> result = new HashMap<>();
        List<String> months = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        for (Object[] record : rawData) {
            months.add("Tháng " + record[0]); // record[0]: month
            revenues.add((Double) record[1]); // record[1]: revenue
        }

        result.put("months", months);
        result.put("revenues", revenues);
        return result;
    }



//    @Scheduled(fixedRate = 60000) // mỗi phút một lần
//    public void updateDatPhongKhachHangStatus() {
//        List<DatPhong> datPhongs = datPhongRepository.findAll();
//        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
//        int gioiHanPhut = 15; // Giới hạn 15 phút
//
//        for (DatPhong datPhong : datPhongs) {
//            LocalDateTime ngayDat = datPhong.getNgayDat();
//            if (ngayDat == null) continue; // Bỏ qua nếu ngày đặt null
//
//            // Tính số phút giữa thời gian hiện tại và ngày đặt
//            long minutesBetween = ChronoUnit.MINUTES.between(ngayDat, now);
//
//            // Kiểm tra điều kiện cập nhật
//            if (minutesBetween > gioiHanPhut) {
//                datPhong.setTinhTrang("Đã Hủy");
//            }
//        }

//        // Lưu tất cả thay đổi vào database
//        datPhongRepository.saveAll(datPhongs);
//    }



    @Override
    public List<DatPhongDTO> findByKhachHang_Id(Integer id) {
        // Lấy danh sách entity từ repository với điều kiện trangThai = true
        List<DatPhong> listDatPhongs = datPhongRepository.findByKhachHang_IdAndTinhTrangAndTrangThai(id, "Đã Checkin", true);

        if (listDatPhongs.isEmpty()) {
            return new ArrayList<>();
        }

        List<DatPhongDTO> listDatPhongDTO = new ArrayList<>();
        for (DatPhong datPhong : listDatPhongs) {
            DatPhongDTO dto = new DatPhongDTO();
            dto.setTenPhong(phongRepository.findById(datPhong.getPhong().getIdPhong()).get().getTenPhong());
            dto.setIdPhong(datPhong.getPhong().getIdPhong());
            dto.setNgayNhanPhong(datPhong.getNgayNhan());
            dto.setNgayTraPhong(datPhong.getNgayTra());
            dto.setTongTien(datPhong.getTongTien());
            dto.setTinhTrang(datPhong.getTinhTrang());
            dto.setTrangThai(datPhong.getTrangThai());
            listDatPhongDTO.add(dto);
        }

        return listDatPhongDTO;
    }


    @Override
    public List<DatPhongDTO> findByKhachHang_Id1(Integer id) {
        // Lấy danh sách entity từ repository
        List<DatPhong> listDatPhongs = datPhongRepository.findByKhachHang_IdAndTinhTrang(id,"Đã Checkin");

        if (listDatPhongs.isEmpty()) {
            return new ArrayList<>();
        }

        List<DatPhongDTO> listDatPhongDTO = new ArrayList<>();
        for (DatPhong datPhong : listDatPhongs) {
            DatPhongDTO dto = new DatPhongDTO();
            // Thêm idDatPhong vào DTO
            dto.setIdDatPhong(datPhong.getIdDatPhong());  // Gán idDatPhong vào DTO
            dto.setTenPhong(phongRepository.findById(datPhong.getPhong().getIdPhong()).get().getTenPhong());
            dto.setNgayNhanPhong(datPhong.getNgayNhan());
            dto.setNgayTraPhong(datPhong.getNgayTra());
            dto.setTienConLai(datPhong.getTienConLai());
            dto.setTinhTrang(datPhong.getTinhTrang());
            dto.setTrangThai(datPhong.getTrangThai());
            listDatPhongDTO.add(dto);
        }

        return listDatPhongDTO;
    }

    @Override
    public List<DatPhongDTO> findByKhachHang_Id12(Integer id) {
        // Lấy danh sách entity từ repository
        List<DatPhong> listDatPhongs = datPhongRepository.findByKhachHang_IdAndTinhTrang(id,"Chưa Checkin");

        if (listDatPhongs.isEmpty()) {
            return new ArrayList<>();
        }

        List<DatPhongDTO> listDatPhongDTO = new ArrayList<>();
        for (DatPhong datPhong : listDatPhongs) {
            DatPhongDTO dto = new DatPhongDTO();
            // Thêm idDatPhong vào DTO
            dto.setIdDatPhong(datPhong.getIdDatPhong());  // Gán idDatPhong vào DTO
            dto.setTenPhong(phongRepository.findById(datPhong.getPhong().getIdPhong()).get().getTenPhong());
            dto.setNgayNhanPhong(datPhong.getNgayNhan());
            dto.setNgayTraPhong(datPhong.getNgayTra());
            dto.setTienConLai(datPhong.getTienConLai());
            dto.setTinhTrang(datPhong.getTinhTrang());
            dto.setTrangThai(datPhong.getTrangThai());
            listDatPhongDTO.add(dto);
        }

        return listDatPhongDTO;
    }

    @Override
    public DatPhong findById1(Integer id) {
        // Trả về DatPhong nếu tìm thấy, hoặc null nếu không tìm thấy
        Optional<DatPhong> datPhong = datPhongRepository.findById(id);
        return datPhong.orElse(null);
    }


    @Scheduled(fixedRate = 60000) // mỗi phút một lần
    public void updateDatPhongKhachHangStatus() {
        List<DatPhong> datPhongs = datPhongRepository.findByTinhTrang("Đang chờ thanh toán....");
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
        int gioiHanPhut = 15; // Giới hạn 15 phút

        for (DatPhong datPhong : datPhongs) {
            LocalDateTime ngayDat = datPhong.getNgayDat();
            if (ngayDat == null) continue; // Bỏ qua nếu ngày đặt null

            // Tính số phút giữa thời gian hiện tại và ngày đặt
            long minutesBetween = ChronoUnit.MINUTES.between(ngayDat, now);

            // Kiểm tra điều kiện cập nhật
            if (minutesBetween > gioiHanPhut) {
                datPhong.setTinhTrang("Đã Hủy");
            }
        }

        // Lưu tất cả thay đổi vào database
        datPhongRepository.saveAll(datPhongs);
    }





    @Override
    public boolean extendStay(Integer idDatPhong, LocalDateTime newEndDate) {
        Optional<DatPhong> datPhongOpt = datPhongRepository.findById(idDatPhong);
        if (datPhongOpt.isPresent()) {
            DatPhong datPhong = datPhongOpt.get();

            // Lấy ngày trả phòng hiện tại
            LocalDateTime oldEndDate = datPhong.getNgayTra();

            // Tính số ngày gia hạn
            long daysExtended = ChronoUnit.DAYS.between(oldEndDate, newEndDate);
            if (daysExtended <= 0) {
                return false; // Không thể gia hạn nếu ngày gia hạn không hợp lệ
            }

            // Kiểm tra nếu phòng đã được đặt trong khoảng thời gian gia hạn
            List<DatPhong> existingBookings = datPhongRepository.findByPhongAndThoiGian(
                    datPhong.getPhong().getIdPhong(),  // Truyền ID phòng
                    newEndDate,  // Ngày gia hạn
                    newEndDate.plusDays(daysExtended)  // Ngày trả phòng mới
            );

// Nếu danh sách không trống, có nghĩa là phòng đã được đặt trong khoảng thời gian gia hạn
            if (!existingBookings.isEmpty()) {
                throw new IllegalStateException("Phòng đã được đặt trong khoảng thời gian gia hạn. Không thể gia hạn.");
            }

            // Lấy giá phòng mỗi ngày
            float dailyRate = datPhong.getPhong().getLoaiPhong().getGia();

            // Tính thêm tiền dựa trên số ngày gia hạn
            float additionalCost = daysExtended * dailyRate;

            // Cập nhật ngày trả phòng mới và tổng tiền
            datPhong.setNgayTra(newEndDate);
            datPhong.setTongTien(datPhong.getTongTien() + additionalCost); // Cập nhật tổng tiền
            datPhong.setTienConLai(datPhong.getTongTien() - datPhong.getTienCoc());

            // Lưu lại thay đổi vào cơ sở dữ liệu
            datPhongRepository.save(datPhong);
            return true;
        }
        return false;
}

    @Override
    public Map<String, Object> getRevenueByDay() {
        try {
            // Lấy ngày hiện tại
            LocalDate today = LocalDate.now();

            // Chuyển đổi LocalDate thành LocalDateTime (bắt đầu từ 00:00:00)
            LocalDateTime startOfDay = today.atStartOfDay(); // 00:00:00 hôm nay
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 00:00:00 ngày hôm sau

            // Gọi repository để lấy doanh thu cho ngày hôm nay
            return datPhongRepository.findRevenueByDateRange1(startOfDay, endOfDay);
        } catch (Exception e) {
            // Ghi log chi tiết lỗi
            e.printStackTrace();
            throw new RuntimeException("Đã xảy ra lỗi khi truy vấn doanh thu ngày: " + LocalDate.now());
        }
    }


    @Override
    public void exportGuestReport(List<DatPhong> guests, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Khách lưu trú");

        // Header row
        Row header = sheet.createRow(0);
        String[] columns = {"STT", "Họ tên", "CMND/CCCD", "Quốc tịch", "Số phòng", "Ngày nhận phòng", "Ngày trả phòng"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Data rows
        int rowIdx = 1;
        for (int i = 0; i < guests.size(); i++) {
            DatPhong guest = guests.get(i);
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(guest.getKhachHang().getHoVaTen());
            row.createCell(2).setCellValue(guest.getCccd());
            row.createCell(3).setCellValue("Việt Nam");
            row.createCell(4).setCellValue(guest.getPhong().getTenPhong());
            row.createCell(5).setCellValue(guest.getNgayNhan().toString());
            row.createCell(6).setCellValue(guest.getNgayTra().toString());
        }

        // Write to file
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing Excel file: " + filePath, e);
        }
    }




}
