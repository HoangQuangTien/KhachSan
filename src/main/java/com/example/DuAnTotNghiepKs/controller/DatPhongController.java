package com.example.DuAnTotNghiepKs.controller;


import com.example.DuAnTotNghiepKs.DTO.*;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.service.*;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private ThamSoService thamSoService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private NguoiDiCungService nguoiDiCungService;


    // Hiển thị trang đặt phòng
    @GetMapping()
    public String showDatPhongPage(@RequestParam(required = false) Integer loaiPhongId, Model model) {
        List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
        model.addAttribute("loaiPhongs", loaiPhongs);

        if (loaiPhongId != null) {
            List<Phong> phongs = phongService.getPhongsByLoaiPhong1(loaiPhongId);
            model.addAttribute("phongs", phongs);
        }

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
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




//    public List<Map<String, Object>> getDatPhongEvents() {
//        List<DatPhong> datPhongs = datPhongService.getAllDatPhong(); // Lấy tất cả thông tin đặt phòng
//
//        // Duyệt qua danh sách đặt phòng và tạo danh sách sự kiện
//        return datPhongs.stream().map(datPhong -> {
//            Map<String, Object> event = new HashMap<>();
//            event.put("title", datPhong.getPhong().getTenPhong());
//            event.put("start", datPhong.getNgayNhan());
//            event.put("end", datPhong.getNgayTra());
//            event.put("color", "#f00"); // Tùy chỉnh màu cho sự kiện (ví dụ: màu đỏ)
//            return event;
//        }).collect(Collectors.toList());
//    }


//    @PostMapping("/confirm-payment")
//    public ResponseEntity<?> confirmPayment(
//            @RequestParam("idDatPhong") Integer idDatPhong,
//            @RequestParam("paymentAmount") Double paymentAmount) {
//
//        try {
//            boolean isPaymentSuccess = thanhToanService.xacNhanThanhToan(idDatPhong, paymentAmount);
//
//            if (isPaymentSuccess) {
//                return ResponseEntity.ok(Map.of("success", "Thanh toán thành công!"));
//            } else {
//                return ResponseEntity.badRequest().body(Map.of("error", "Thanh toán không thành công."));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
//        }
//    }

    @PostMapping("/xacnhan")
    public ResponseEntity<?> xacNhanThongTinKhachHang(@ModelAttribute KhachHangDTO khachHangDTO) {
        try {
            // Kiểm tra nếu DTO trống hoặc không hợp lệ
            if (khachHangDTO == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Dữ liệu không hợp lệ!"));
            }

            if (khachHangDTO.getId() == null) {
                khachHangDTO.setMaKhachHang(khachHangService.generateMaKhachHang()); // Tạo mã khách hàng mới
            } else {
                // Kiểm tra khách hàng đã tồn tại dựa trên email
                KhachHangDTO existingByEmail = khachHangService.findByEmail(khachHangDTO.getEmail());
                if (existingByEmail != null && !existingByEmail.getId().equals(khachHangDTO.getId())) {
                    // Trả về thông tin khách hàng tồn tại nếu không phải là khách hàng hiện tại
                    return ResponseEntity.ok(Map.of(
                            "message", "Khách hàng với email đã tồn tại.",
                            "khachHang", existingByEmail
                    ));
                }

                // Kiểm tra khách hàng đã tồn tại dựa trên số điện thoại
                KhachHangDTO existingByPhone = khachHangService.findBySoDienThoai(khachHangDTO.getSoDienThoai());
                if (existingByPhone != null && !existingByPhone.getId().equals(khachHangDTO.getId())) {
                    // Trả về thông tin khách hàng tồn tại nếu không phải là khách hàng hiện tại
                    return ResponseEntity.ok(Map.of(
                            "message", "Khách hàng với số điện thoại đã tồn tại.",
                            "khachHang", existingByPhone
                    ));
                }
            }

            // Lưu thông tin khách hàng mới hoặc cập nhật thông tin
            KhachHangDTO savedKhachHang = khachHangService.save(khachHangDTO);

            // Tạo DTO để trả về thông tin đã lưu
            KhachHangDTO savedKhachHangDTO = new KhachHangDTO();
            savedKhachHangDTO.setId(savedKhachHang.getId());
            savedKhachHangDTO.setMaKhachHang(savedKhachHang.getMaKhachHang());
            savedKhachHangDTO.setHoVaTen(savedKhachHang.getHoVaTen());
            savedKhachHangDTO.setEmail(savedKhachHang.getEmail());
            savedKhachHangDTO.setGioiTinh(savedKhachHang.isGioiTinh());
            savedKhachHangDTO.setSoDienThoai(savedKhachHang.getSoDienThoai());
            // Copy địa chỉ nếu cần
            savedKhachHangDTO.setDiaChi(savedKhachHang.getDiaChi());

            // Trả về thông báo thành công và thông tin khách hàng mới
            return ResponseEntity.ok(Map.of(
                    "success", "Xác nhận thông tin thành công!",
                    "khachHang", savedKhachHangDTO
            ));
        } catch (Exception e) {
            // Ghi log lỗi chi tiết
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }



    @PostMapping("/create")
    public ResponseEntity<?> createDatPhong(
            @RequestParam("idLoaiPhong") List<String> idLoaiPhongStrList,
            @RequestParam("idPhong") String idPhongStr, // Chuyển sang nhận chuỗi
            @RequestParam("ngayNhan") String ngayNhanStr,
            @RequestParam("ngayTra") String ngayTraStr,
            @RequestParam("cccd") String cccd,
            @RequestParam("maDatPhong") String maDatPhong,
            @RequestParam("idKhachHang") String idKhachHangStr) {

        // Chuyển đổi chuỗi ID phòng thành danh sách
        List<String> idPhongStrList = Arrays.asList(idPhongStr.split(","));

        // Kiểm tra các tham số đầu vào để đảm bảo chúng không rỗng
        if (idLoaiPhongStrList == null || idLoaiPhongStrList.isEmpty() ||
                idPhongStrList == null || idPhongStrList.isEmpty() ||
                idKhachHangStr == null || idKhachHangStr.isEmpty() ||
                ngayNhanStr == null || ngayNhanStr.isEmpty() ||
                ngayTraStr == null || ngayTraStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Các tham số không được để trống."));
        }

        try {
            // Kiểm tra mã đặt phòng trước
            if (datPhongService.existsByMaDatPhong(maDatPhong)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã đặt phòng đã tồn tại."));
            }

            // Kiểm tra và chuyển đổi ID khách hàng
            Integer idKhachHang = parseInteger(idKhachHangStr, "ID khách hàng không hợp lệ.");
            KhachHangDTO khachHangDTO = khachHangService.findById(idKhachHang);
            if (khachHangDTO == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Khách hàng không tồn tại."));
            }
            KhachHang khachHang = khachHangService.convertToEntity(khachHangDTO);

            // Chuyển đổi ngày nhận và ngày trả
            LocalDateTime ngayNhan = parseDateTime(ngayNhanStr, "Ngày nhận phòng không hợp lệ.");
            LocalDateTime ngayTra = parseDateTime(ngayTraStr, "Ngày trả phòng không hợp lệ.");


            if (ngayTra.isBefore(ngayNhan)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày trả phòng phải sau ngày nhận phòng."));
            }

            // Kiểm tra khoảng cách tối thiểu 24 giờ
            Duration duration = Duration.between(ngayNhan, ngayTra);
            if (duration.toHours() < 24) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày trả phòng và ngày nhận phòng phải cách nhau tối thiểu 24 giờ."));
            }

            // Tính toán tổng tiền phòng và tiền cọc
            float tongTienPhong = 0;
            float tienCoc = 0;


            // Lấy giá trị thời gian cho phép từ bảng ThamSo dựa trên ID
            Long idThamSo = 3L;
            String thoiGianChoPhepStr = thamSoService.getValueById(idThamSo);

            if (thoiGianChoPhepStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tham số thời gian cho phép không tồn tại."));
            }

            // Chuyển đổi giá trị 'giaTri' từ chuỗi sang kiểu long
            long thoiGianChoPhepMillis = TimeUnit.MINUTES.toMillis(Long.parseLong(thoiGianChoPhepStr));

            // Lưu danh sách các đối tượng DatPhong
            List<DatPhong> datPhongList = new ArrayList<>();

            for (int i = 0; i < idPhongStrList.size(); i++) {

                // Kiểm tra và chuyển đổi ID loại phòng
                // Sử dụng loại phòng tương ứng hoặc giữ nguyên loại phòng cuối cùng nếu danh sách loại phòng ngắn hơn
                Integer idLoaiPhong = parseInteger(idLoaiPhongStrList.get(Math.min(i, idLoaiPhongStrList.size() - 1)), "ID loại phòng không hợp lệ.");
                LoaiPhong loaiPhong = loaiPhongService.findById(idLoaiPhong);


                // Kiểm tra và chuyển đổi ID phòng
                Integer idPhong = parseInteger(idPhongStrList.get(i), "ID phòng không hợp lệ.");
                Phong selectedPhong = phongService.findById(idPhong);

                // Kiểm tra tình trạng phòng
                if (selectedPhong == null || !selectedPhong.getTinhTrang()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Phòng " + idPhong + " không hợp lệ hoặc đã ngừng hoạt động."));
                }



                // Lấy thông tin đặt phòng đã tồn tại
                List<DatPhong> existingBookings = datPhongService.findByPhongAndThoiGian(idPhong, ngayNhan, ngayTra);

                // Kiểm tra khoảng thời gian giữa các lần đặt phòng
                for (DatPhong existingBooking : existingBookings) {
                    // Kiểm tra nếu trạng thái của đặt phòng không phải "Đã Hủy"
                    if (!existingBooking.getTinhTrang().equals("Đã Hủy") && !existingBooking.getTrangThai()) {
                        LocalDateTime existingNgayTra = existingBooking.getNgayTra();
                        long existingNgayTraMillis = existingNgayTra.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        long ngayNhanMillis = ngayNhan.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                        // Kiểm tra khoảng thời gian giữa các lần đặt phòng
                        if (ngayNhanMillis - existingNgayTraMillis < thoiGianChoPhepMillis) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Thời gian đặt của bạn không hợp lệ, trùng với người đặt trước."));
                        }
                    }
                }

                // Tính toán các chi phí cho từng phòng
                long soNgayO = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
                float giaPhong = selectedPhong.getLoaiPhong().getGia();
                float tongTienPhongPhong = giaPhong * soNgayO;
//                float tienCocPhong = tongTienPhongPhong * 0.8f;

//                // Cập nhật tổng tiền phòng và tiền cọc
//                tongTienPhong += tongTienPhongPhong;
//                tienCoc += tienCocPhong;

                // Tạo đối tượng DatPhong cho từng phòng
                DatPhong datPhong = new DatPhong();
                datPhong.setMaDatPhong(generateMaDatPhong()+""+(i+1));
                datPhong.setPhong(selectedPhong);
                datPhong.setNgayNhan(ngayNhan);
                datPhong.setNgayTra(ngayTra);
                datPhong.setCccd(cccd);
                datPhong.setTongTien(tongTienPhongPhong);
                datPhong.setTienCoc(tongTienPhongPhong * 0.8f);
                float tienConLai = datPhong.getTongTien() - datPhong.getTienCoc(); // Tính tiền còn lại
                datPhong.setTienConLai(tienConLai);
                datPhong.setLoaiPhong(loaiPhong);
                datPhong.setKhachHang(khachHang);
                datPhong.setTinhTrang("Chưa Checkin");
                datPhong.setTrangThai(false);

                // Thêm vào danh sách để lưu sau
                datPhongList.add(datPhong);
                System.out.println("idLoaiPhongStrList: " + idLoaiPhongStrList);
                System.out.println("idPhongStrList: " + idPhongStrList);
            }

            // Lưu tất cả các đối tượng DatPhong vào cơ sở dữ liệu
            for (DatPhong datPhong : datPhongList) {
                datPhongService.saveDatPhong(datPhong);


                // Lưu thông tin chi tiết đặt phòng
                ChiTietDatPhong chiTietDatPhong = new ChiTietDatPhong();
                chiTietDatPhong.setMaChiTietDatPhong("CTDP" + datPhong.getIdDatPhong());
                chiTietDatPhong.setDatPhong(datPhong);
                chiTietDatPhong.setKhachHang(khachHang);
                chiTietDatPhong.setNgayLap(new Date());
                chiTietDatPhong.setTongTien(BigDecimal.valueOf(tongTienPhong));

                chiTietDatPhongService.saveChiTietDatPhong(chiTietDatPhong);
            }

            StringBuilder danhSachPhong = new StringBuilder();
            for (DatPhong datPhong : datPhongList) {
                String tenPhong = datPhong.getPhong().getTenPhong(); // Giả sử phương thức `getTenPhong()` trả về tên phòng
                danhSachPhong.append("- ").append(tenPhong).append("<br>");
            }

//            DatPhong datPhong1 = datPhongList.get(0);
//            Integer idPhong = datPhong1.getPhong().getIdPhong();
//            phongService.findById(idPhong);


            // Tính tổng tiền cọc của tất cả các phòng đã đặt
            float tongTienCocTatCaPhong = datPhongList.stream()
                    .map(DatPhong::getTienCoc) // Lấy tiền cọc của từng đặt phòng
                    .reduce(0f, Float::sum);   // Tính tổng tiền cọc



            // Gửi email xác nhận đặt phòng thành công cho khách hàng
            String emailKhachHang = khachHangDTO.getEmail(); // Lấy email từ DTO của khách hàng
            String subject = "Xác nhận đặt phòng thành công tại DRAGONBALL HOTEL";
            String text = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333333; background-color: #f9f9f9;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px;'>" +
                    "<h2 style='color: #4CAF50; text-align: center;'>Xin chào " + khachHang.getHoVaTen() + ",</h2>" +
                    "<p style='font-size: 16px;'>Cảm ơn bạn đã lựa chọn <strong>DRAGONBALL HOTEL</strong> cho kỳ nghỉ của mình!</p>" +
                    "<p style='font-size: 16px;'>Dưới đây là thông tin đặt phòng của bạn:</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'><strong>Phòng của bạn là:</strong> " +danhSachPhong.toString()   + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày nhận phòng:</strong> " + ngayNhanStr + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày trả phòng:</strong> " + ngayTraStr + "</p>" +
                    "<p style='font-size: 16px;'><strong>Số tiền cọc:</strong> " + String.format("%,.0f", tongTienCocTatCaPhong) + " VND</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'>Chúng tôi mong được đón tiếp bạn và sẽ làm mọi điều có thể để kỳ nghỉ của bạn trở nên hoàn hảo nhất.</p>" +
                    "<p style='font-size: 16px;'>Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi qua số điện thoại hoặc email hỗ trợ.</p>" +
                    "<p style='font-size: 16px;'>Trân trọng,<br>Đội ngũ quản lý khách sạn <strong>DRAGONBALL HOTEL</strong></p>" +
                    "<footer style='margin-top: 20px; font-size: 12px; text-align: center; color: #666666;'>" +
                    "<p>Địa chỉ: Tòa nhà FPT Polytechnic, Phố Trịnh Văn Bô, Nam Từ Liêm, Hà Nội.</p>" +
                    "<p>Điện thoại: 0397156204 | Email: support@dragonballhotel.com</p>" +
                    "<p>&copy; 2024 DRAGONBALL HOTEL. Tất cả các quyền được bảo lưu.</p>" +
                    "</footer>" +
                    "</div>" +
                    "</body>" +
                    "</html>";


// Gửi email xác nhận
            datPhongService.sendEmail(emailKhachHang, subject, text);

            return ResponseEntity.ok(Map.of("success", "Đặt phòng thành công!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng ID không hợp lệ."));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng ngày không hợp lệ."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }



    // Phương thức chuyển đổi ngày giờ
    private LocalDateTime parseDateTime(String dateTimeStr, String errorMessage) {
        try {
            // Định dạng datetime-local
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }



    private String generateMaDatPhong() {
        String prefix = "DP";
        DatPhong lastDatPhong = datPhongService.findTopByOrderByIdDatPhongDesc();
        int nextId = lastDatPhong != null ? (int) (lastDatPhong.getIdDatPhong() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }

    // Helper methods for parsing integer and date
    private Integer parseInteger(String value, String errorMessage) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

//
//    @GetMapping("/month")
//    public ResponseEntity<Double> getRevenueByMonth(@RequestParam int month, @RequestParam int year) {
//        double revenue = datPhongService.getRevenueByMonth(month, year);
//        return ResponseEntity.ok(revenue);
//    }

    @GetMapping("/four-months")
    public ResponseEntity<Map<String, Object>> getRevenueForFourMonths(@RequestParam int startMonth, @RequestParam int year) {
        Map<String, Object> revenueData = datPhongService.getRevenueForFourMonths(startMonth, year);
        return ResponseEntity.ok(revenueData);
    }

//    @GetMapping("/year")
//    public ResponseEntity<Map<String, Object>> getRevenueByYear(@RequestParam int year) {
//        Map<String, Object> revenueData = datPhongService.getRevenueByYear(year);
//        return ResponseEntity.ok(revenueData);
//    }







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



//    @GetMapping("/api/idle-rooms")
//    public List<IdleRoomDTO> getIdleRooms() {
//        return datPhongService.getIdleRoomTimes();
//    }
@GetMapping("/top-3-phong")
public ResponseEntity<?> getTop3PhongDuocDatNhieuNhat() {
    List<Object[]> topPhong = datPhongService.getTopPhongDuocDatNhieuNhat();

    // Chuyển đổi kết quả truy vấn sang danh sách DTO
    List<TopPhongDTO> topPhongDTOs = topPhong.stream().map(result -> {
        TopPhongDTO dto = new TopPhongDTO();
        dto.setIdPhong((Integer) result[0]); // Giả sử id_phong là Integer
        dto.setTenPhong((String) result[1]); // Giả sử ten_phong là String

        // Kiểm tra và ép kiểu `so_lan_dat` dựa trên kiểu dữ liệu
        Object soLanDat = result[2];
        if (soLanDat instanceof Long) {
            dto.setSoLuongDat((Long) soLanDat);
        } else if (soLanDat instanceof Integer) {
            dto.setSoLuongDat(((Integer) soLanDat).longValue());
        } else {
            // Xử lý lỗi nếu kiểu dữ liệu không hợp lệ
            throw new IllegalArgumentException("Kiểu dữ liệu không hợp lệ cho `so_lan_dat`.");
        }

        return dto;
    }).collect(Collectors.toList());

    return ResponseEntity.ok(Map.of("topPhong", topPhongDTOs));
}

    @GetMapping("/total-distinct-customers")
    public ResponseEntity<Long> getDistinctCustomers() {
        Long totalDistinctCustomers = datPhongService.countDistinctCustomers();
        return ResponseEntity.ok(totalDistinctCustomers);
    }


    @GetMapping("/thong-ke-da-huy")
    public ResponseEntity<Long> getCancelledBookingStats() {

            Long cancelledCount = datPhongService.countCancelledBookings();

            return ResponseEntity.ok(cancelledCount);

    }


    @DeleteMapping("/xoaCCCD/{idDatPhong}")
    public ResponseEntity<?> xoaCCCD(@PathVariable Integer idDatPhong) {
        try {
            boolean result = datPhongService.xoaCCCD(idDatPhong);
            if (result) {
                return ResponseEntity.ok().body("CCCD đã được xóa thành công.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đặt phòng với ID này.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xóa CCCD.");
        }
    }


    @GetMapping("/month")
    public ResponseEntity<Double> getRevenueByMonth(@RequestParam int month, @RequestParam int year) {
        try {
            double revenue = datPhongService.getRevenueByMonth(month, year);
            return ResponseEntity.ok(revenue);  // Trả về doanh thu là một số thực
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0.0);
        }
    }


    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getRevenueByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            Map<String, Object> revenueData = datPhongService.getRevenueByDateRange(start, end);
            return ResponseEntity.ok(revenueData);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error", "message", e.getMessage()));
        }
    }


    @GetMapping("/year")
    public ResponseEntity<Map<String, Object>> getRevenueByYear(@RequestParam int year) {
        Map<String, Object> revenueData = datPhongService.getRevenueByYear(year);
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/quarter")
    public ResponseEntity<Map<String, Object>> getRevenueByQuarter(
            @RequestParam String quarterRange, @RequestParam int year) {
        int startMonth, endMonth;

        // Xác định khoảng tháng dựa trên quarterRange
        switch (quarterRange) {
            case "1-3":
                startMonth = 1;
                endMonth = 3;
                break;
            case "4-6":
                startMonth = 4;
                endMonth = 6;
                break;
            case "7-9":
                startMonth = 7;
                endMonth = 9;
                break;
            case "10-12":
                startMonth = 10;
                endMonth = 12;
                break;
            default:
                throw new IllegalArgumentException("Quý không hợp lệ: " + quarterRange);
        }

        Map<String, Object> revenueData = datPhongService.getRevenueByQuarter(year, startMonth, endMonth);
        return ResponseEntity.ok(revenueData);
    }



}
