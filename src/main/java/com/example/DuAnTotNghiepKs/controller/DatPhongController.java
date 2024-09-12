package com.example.DuAnTotNghiepKs.controller;


import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.service.*;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/datphongs")
public class DatPhongController {

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private PhongService phongService;

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    private DoanService doanService;

    @Autowired
    private ThanhToanService thanhToanService;

    @Autowired
    private RoomStatusUpdater roomStatusUpdater;

    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;

    @Autowired
    private NhanVienService nhanVienService;


    // Hiển thị trang đặt phòng
    @GetMapping()
    public String showDatPhongPage(@RequestParam(required = false) Integer loaiPhongId, Model model) {
        List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
        model.addAttribute("loaiPhongs", loaiPhongs);

        if (loaiPhongId != null) {
            List<Phong> phongs = phongService.getPhongsByLoaiPhong1(loaiPhongId);
            model.addAttribute("phongs", phongs);
        }

        return "list/QuanLyDatPhong/datphongs";
    }

    @GetMapping("/available-rooms")
    @ResponseBody
    public List<PhongDTO> getAvailableRoomsByLoaiPhong(@RequestParam Integer loaiPhongId) {
        List<Phong> availablePhongs = datPhongService.findAvailablePhongByLoaiPhong(loaiPhongId);
        List<Phong> activePhongs = availablePhongs.stream()
                .filter(phong -> phong.getTinhTrang()) // Kiểm tra tình trạng hoạt động
                .collect(Collectors.toList());
        return activePhongs.stream()
                .map(phong -> phongService.convertToPhongDTO(phong))
                .collect(Collectors.toList());
    }




    public List<Map<String, Object>> getDatPhongEvents() {
        List<DatPhong> datPhongs = datPhongService.getAllDatPhong(); // Lấy tất cả thông tin đặt phòng

        // Duyệt qua danh sách đặt phòng và tạo danh sách sự kiện
        return datPhongs.stream().map(datPhong -> {
            Map<String, Object> event = new HashMap<>();
            event.put("title", datPhong.getPhong().getTenPhong());
            event.put("start", datPhong.getNgayNhan());
            event.put("end", datPhong.getNgayTra());
            event.put("color", "#f00"); // Tùy chỉnh màu cho sự kiện (ví dụ: màu đỏ)
            return event;
        }).collect(Collectors.toList());
    }


    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(
            @RequestParam("idDatPhong") Integer idDatPhong,
            @RequestParam("paymentAmount") Double paymentAmount) {

        try {
            boolean isPaymentSuccess = thanhToanService.xacNhanThanhToan(idDatPhong, paymentAmount);

            if (isPaymentSuccess) {
                return ResponseEntity.ok(Map.of("success", "Thanh toán thành công!"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Thanh toán không thành công."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    @PostMapping("/xacnhan")
    public ResponseEntity<?> xacNhanThongTinKhachHang(@ModelAttribute KhachHangDTO khachHangDTO ) {
        try {
            // Kiểm tra trùng lặp email và số điện thoại
            boolean emailExists = khachHangService.existsByEmail(khachHangDTO.getEmail());
            boolean phoneExists = khachHangService.existsBySoDienThoai(khachHangDTO.getSoDienThoai());

            if (emailExists) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại."));
            }

            if (phoneExists) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại đã tồn tại."));
            }

            // Lưu khách hàng và trả về ID
            KhachHangDTO khachHang = khachHangService.save(khachHangDTO);

            // Trả về ID khách hàng vừa lưu
            return ResponseEntity.ok(Map.of("success", "Xác nhận thông tin thành công!", "khachHangId", khachHang.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDatPhong(
            @RequestParam("idLoaiPhong") String idLoaiPhongStr,
            @RequestParam("idPhong") String idPhongStr,
            @RequestParam("ngayNhan") String ngayNhanStr,
            @RequestParam("ngayTra") String ngayTraStr,
            @RequestParam("cccd") String cccd,
            @RequestParam("maDatPhong") String maDatPhong,
            @RequestParam("idKhachHang") String idKhachHangStr) { // Thêm ID khuyến mãi

        try {
            // Kiểm tra mã đặt phòng trước
            if (datPhongService.existsByMaDatPhong(maDatPhong)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã đặt phòng đã tồn tại."));
            }

            Integer idKhachHang = Integer.valueOf(idKhachHangStr);
            KhachHangDTO khachHangDTO = khachHangService.findById(idKhachHang);
            // Kiểm tra ID khách hàng
            if (khachHangDTO == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Khách hàng không tồn tại."));
            }

            // Chuyển đổi từ KhachHangDTO sang KhachHang entity
            KhachHang khachHang = khachHangService.convertToEntity(khachHangDTO);

            // Xử lý loại phòng, phòng và ngày nhận/trả
            Integer idLoaiPhong = Integer.parseInt(idLoaiPhongStr);
            LoaiPhong loaiPhong = loaiPhongService.findById(idLoaiPhong);

            Integer idPhong = Integer.parseInt(idPhongStr);
            Phong selectedPhong = phongService.findById(idPhong);

            // Kiểm tra tình trạng phòng
            if (!selectedPhong.getTinhTrang()) { // Kiểm tra tình trạng hoạt động
                return ResponseEntity.badRequest().body(Map.of("error", "Phòng đã ngừng hoạt động."));
            }

            LocalDate ngayNhan = LocalDate.parse(ngayNhanStr);
            LocalDate ngayTra = LocalDate.parse(ngayTraStr);

            if (ngayTra.isBefore(ngayNhan)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày trả phòng phải sau ngày nhận phòng."));
            }

            long soNgayO = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
            float giaPhong = selectedPhong.getGia();
            float tongTienPhong = giaPhong * soNgayO;
            float tienCoc = tongTienPhong * 0.8f;
            float tienConLai = tongTienPhong - tienCoc;

            // Tạo đối tượng DatPhong và gán các thông tin cần thiết
            DatPhong datPhong = new DatPhong();
            datPhong.setMaDatPhong(maDatPhong);
            datPhong.setPhong(selectedPhong);
            datPhong.setNgayNhan(convertToDate(ngayNhan));
            datPhong.setNgayTra(convertToDate(ngayTra));
            datPhong.setCccd(cccd);
            datPhong.setTongTien(tongTienPhong);
            datPhong.setTienCoc(tienCoc);
            datPhong.setTienConLai(tienConLai);
            datPhong.setLoaiPhong(loaiPhong);
            datPhong.setKhachHang(khachHang);  // Gán khách hàng vào đối tượng đặt phòng
            datPhong.setTinhTrang(false);
            // Lưu thông tin đặt phòng
            datPhongService.saveDatPhong(datPhong);

            // Tạo đối tượng ChiTietDatPhong và gán các thông tin cần thiết
            ChiTietDatPhong chiTietDatPhong = new ChiTietDatPhong();
            chiTietDatPhong.setMaChiTietDatPhong("CTDP" + datPhong.getIdDatPhong()); // Ví dụ tạo mã chi tiết đặt phòng
            chiTietDatPhong.setDatPhong(datPhong);
            chiTietDatPhong.setKhachHang(khachHang);
//            chiTietDatPhong.setNhanVien(nhanVienService.findById(Integer.valueOf(idNhanVienStr))); // Thay bằng phương thức lấy nhân viên
//            chiTietDatPhong.setKhuyenMai(khuyenMaiService.findById(Integer.valueOf(idKhuyenMaiStr))); // Thay bằng phương thức lấy khuyến mãi
            chiTietDatPhong.setNgayLap(new Date());
            chiTietDatPhong.setTongTien(BigDecimal.valueOf(tongTienPhong));

            // Lưu thông tin chi tiết đặt phòng
            chiTietDatPhongService.saveChiTietDatPhong(chiTietDatPhong);

            // Cập nhật trạng thái phòng
            selectedPhong.setTrangThai(false);
            phongService.savePhong(selectedPhong);

            return ResponseEntity.ok(Map.of("success", "Đặt phòng thành công!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng ID không hợp lệ."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    @GetMapping("/month")
    public ResponseEntity<Double> getRevenueByMonth(@RequestParam int month, @RequestParam int year) {
        double revenue = datPhongService.getRevenueByMonth(month, year);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/four-months")
    public ResponseEntity<Double> getRevenueByFourMonths(
            @RequestParam int startMonth,
            @RequestParam int year) {

        // Kiểm tra các tham số
        if (startMonth < 1 || startMonth > 12) {
            return ResponseEntity.badRequest().body(null); // Hoặc gửi thông báo lỗi dưới dạng Double nếu cần
        }

        if (year <= 0) {
            return ResponseEntity.badRequest().body(null); // Hoặc gửi thông báo lỗi dưới dạng Double nếu cần
        }

        // Lấy doanh thu từ service
        double revenue = datPhongService.getRevenueByFourMonths(startMonth, year);

        // Trả về doanh thu
        return ResponseEntity.ok(revenue);
    }


    @GetMapping("/year")
    public ResponseEntity<Double> getRevenueByYear(@RequestParam int year) {
        double revenue = datPhongService.getRevenueByYear(year);
        return ResponseEntity.ok(revenue);
    }



    // Thống kê số lượng phòng đã đặt
    @GetMapping("/bookings/count")
    public ResponseEntity<Long> getBookingCount() {
        Long bookingCount = datPhongService.getBookingCount();
        return ResponseEntity.ok(bookingCount);
    }

    @GetMapping("/inactive/count")
    public ResponseEntity<Long> getInactiveRoomCount() {
        Long count = datPhongService.countActivePhongsFalse();
        return ResponseEntity.ok(count);
    }
}
