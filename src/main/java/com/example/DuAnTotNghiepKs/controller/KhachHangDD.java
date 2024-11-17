package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.SecurityConfig.Config;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping
@Controller
public class KhachHangDD {


    private final DatPhongService datPhongService;
    private final PhongService phongService;
    private final KhachHangService khachHangService;

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;
    @Autowired
    private TaiKhoanService taiKhoanService;

    public KhachHangDD(DatPhongService datPhongService, PhongService phongService, KhachHangService khachHangService) {
        this.datPhongService = datPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
    }

    @GetMapping("/vnpay_result")
    public String vnpayResult(
            @RequestParam Map<String, String> requestParams,
            Model model) {


        // Xử lý thông tin nhận được từ VNPAY
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            fields.put(fieldName, fieldValue);
        }

        // Lấy mã băm bảo mật và mã băm tạo từ dữ liệu
        String vnp_SecureHash = requestParams.get("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        String signValue = Config.hashAllFields(fields);

        // Kiểm tra chữ ký hợp lệ
        String vnp_TxnRef = requestParams.get("vnp_TxnRef");
        String responseCode = requestParams.get("vnp_ResponseCode");
        Integer idDatPhong = Integer.valueOf(vnp_TxnRef);
        boolean isValidSignature = signValue.equals(vnp_SecureHash);

        if (isValidSignature && "00".equals(responseCode)) {

            vnpayService.ThanhToanThanhCong(idDatPhong);
        } else {
            vnpayService.ThanhToanThatBai(idDatPhong);
        }


        // Truyền dữ liệu sang model để hiển thị trên view
        model.addAttribute("isValidSignature", isValidSignature);
        model.addAttribute("vnp_TxnRef", requestParams.get("vnp_TxnRef"));
        model.addAttribute("vnp_Amount", requestParams.get("vnp_Amount"));
        model.addAttribute("vnp_OrderInfo", requestParams.get("vnp_OrderInfo"));
        model.addAttribute("vnp_ResponseCode", requestParams.get("vnp_ResponseCode"));
        model.addAttribute("vnp_TransactionNo", requestParams.get("vnp_TransactionNo"));
        model.addAttribute("vnp_BankCode", requestParams.get("vnp_BankCode"));
        model.addAttribute("vnp_PayDate", requestParams.get("vnp_PayDate"));
        model.addAttribute("vnp_TransactionStatus", requestParams.get("vnp_TransactionStatus"));

        return "list/KhachHang/Vnpay_result"; // Đường dẫn tới template HTML
    }

    @GetMapping("/khach-hang")
    public String hienThi(Model model) {
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        System.out.println("TaiKhoanDTO: " + taiKhoanDTO); // In giá trị TaiKhoanDTO để kiểm tra

        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null && taiKhoanDTO.getKhachHangDTO().getHoVaTen() != null) {
            model.addAttribute("hoVaTen", taiKhoanDTO.getKhachHangDTO().getHoVaTen());
        }
        model.addAttribute("phongTrong", phongService.getAllByTrangThai());
        return "list/KhachHang/khachHang";
    }


    @GetMapping("/load-phong")
    public ResponseEntity<List<Object[]>> loadAllPhongKhachHang() {
        List<Object[]> objects = datPhongService.getTopPhongDuocDatNhieuNhat1();
        return ResponseEntity.ok(objects);
    }


    @GetMapping("/lien-he")
    public String contactPage() {
        return "list/KhachHang/lienHe"; // Không cần .html
    }

    @GetMapping("/searchh")
    public String searchPhong(@RequestParam("ngayNhan") String ngayNhanStr,
                              @RequestParam("ngayTra") String ngayTraStr,
                              @RequestParam("soLuongNguoi") Integer soLuongNguoi,
                              Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime ngayNhan = LocalDateTime.parse(ngayNhanStr, formatter);
        LocalDateTime ngayTra = LocalDateTime.parse(ngayTraStr, formatter);

        // Validate ngày trả không được trước ngày nhận
        if (ngayTra.isBefore(ngayNhan)) {
            model.addAttribute("error", "Ngày trả không được trước ngày nhận.");
            return "list/KhachHang/khachHang"; // View hiển thị lỗi
        }
        List<Phong> phongs = phongService.searchPhongs(ngayNhan, ngayTra, soLuongNguoi);
        model.addAttribute("phongs", phongs);
        return "list/KhachHang/khachHang"; // Tên của view hiển thị danh sách phòng
    }

    @GetMapping("/view-dat-phong")
    public String showRoomDetailPhong(@RequestParam("roomId") Integer roomId, Model model, HttpSession session) {
        // Lấy thông tin tài khoản từ session
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession();
        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null && taiKhoanDTO.getKhachHangDTO().getId() != null) {
            model.addAttribute("idKhachHang", taiKhoanDTO.getKhachHangDTO().getId());
        }

        // Lấy thông tin phòng
        Phong phong = phongService.getPhongById1(roomId);
        if (phong == null) {
            model.addAttribute("error", "Phòng không tồn tại.");
            return "list/KhachHang/datPhong"; // Trả về view với thông báo lỗi
        }

        // Chuyển đổi Phong sang PhongDTO
        PhongDTO phongDTO = phongService.convertToPhongDTO(phong);
        model.addAttribute("phong", phongDTO);

        return "list/KhachHang/datPhong"; // Tên của view datPhong.html
    }




    //
    @PostMapping("/dat-phong1")
    public ResponseEntity<?> createDatPhongKhachHang1(
            @RequestParam(name = "amount", required = false) Long amount,
            @RequestParam("bankCode") String bankCode,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam("idPhong") String idPhongStr,
            @RequestParam("idLoaiPhong") String idLoaiPhongStr,
            @RequestParam("ngayNhan") String ngayNhanStr,
            @RequestParam("ngayTra") String ngayTraStr,
            @RequestParam("cccd") String cccd,
            @RequestParam("idKhachHang") String idKhachHangStr,
            HttpServletRequest req) {

        // Kiểm tra các tham số đầu vào
        if (idLoaiPhongStr.isEmpty() || idPhongStr.isEmpty() ||
                idKhachHangStr.isEmpty() || ngayNhanStr.isEmpty() ||
                ngayTraStr.isEmpty() || cccd.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Các tham số không được để trống."));
        }

        try {
            // Chuyển đổi ID khách hàng
            Integer idKhachHang = parseInteger(idKhachHangStr, "ID không đúng định dạng");
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

            // Tính toán tổng tiền phòng và tiền cọc
            float tongTienPhong = 0;
            float tienCoc = 0;

            long thoiGianChoPhepMillis = datPhongService.getThoiGianChoPhepDatPhong();
            List<DatPhong> datPhongList = new ArrayList<>();

            // Chuyển đổi ID loại phòng
            Integer idLoaiPhong = Integer.valueOf(idLoaiPhongStr);
            LoaiPhong loaiPhong = loaiPhongService.findById(idLoaiPhong);

            // Xử lý danh sách các ID phòng
            List<String> idPhongStrList = Arrays.asList(idPhongStr.split(","));
            for (String idPhongStrItem : idPhongStrList) {
                Integer idPhong = Integer.valueOf(idPhongStrItem.trim());
                Phong selectedPhong = phongService.findById(idPhong);

                // Kiểm tra tình trạng phòng
                if (selectedPhong == null || !selectedPhong.getTinhTrang()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Phòng " + idPhong + " không hợp lệ hoặc đã ngừng hoạt động."));
                }

                // Lấy thông tin đặt phòng đã tồn tại
                List<DatPhong> existingBookings = datPhongService.findByPhongAndThoiGian(idPhong, ngayNhan, ngayTra);
                if (!existingBookings.isEmpty()) {
                    for (DatPhong existingBooking : existingBookings) {
                        if (ngayNhan.isBefore(existingBooking.getNgayTra().plusSeconds(thoiGianChoPhepMillis / 1000))) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Thời gian đặt của bạn không hợp lệ, trùng với người đặt trước."));
                        }
                    }
                }

                // Tính toán các chi phí cho từng phòng
                long soNgayO = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
                float giaPhong = selectedPhong.getLoaiPhong().getGia();
                float tongTienPhongPhong = giaPhong * soNgayO;
                float tienCocPhong = tongTienPhongPhong * 0.8f;

                // Cập nhật tổng tiền phòng và tiền cọc
                tongTienPhong += tongTienPhongPhong;
                tienCoc += tienCocPhong;

                // Tạo đối tượng DatPhong
                DatPhong datPhong = new DatPhong();
                datPhong.setMaDatPhong(generateMaDatPhong() + "-" + idPhong);
                datPhong.setPhong(selectedPhong);
                datPhong.setNgayNhan(ngayNhan);
                datPhong.setNgayTra(ngayTra);
                datPhong.setNgayDat(LocalDateTime.now());
                datPhong.setCccd(cccd);
                datPhong.setTongTien(tongTienPhong);
                datPhong.setTienCoc(tienCoc);
                datPhong.setTienConLai(tongTienPhong - tienCoc);
                datPhong.setLoaiPhong(loaiPhong);
                datPhong.setKhachHang(khachHang);
                datPhong.setTinhTrang("Đang chờ thanh toán...");
                datPhong.setTrangThai(false);

                // Thêm vào danh sách để lưu sau
                datPhongList.add(datPhong);
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


            DatPhong booking = datPhongList.get(0);
            Integer id = booking.getIdDatPhong();

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = String.valueOf(id);
            String vnp_IpAddr = Config.getIpAddress(req);
            String vnp_TmnCode = Config.vnp_TmnCode;

            System.out.println("vnp_TxnRef:"+vnp_TxnRef);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf((int) (tienCoc * 100))); // Chuyển đổi sang đồng
            vnp_Params.put("vnp_CurrCode", "VND");

            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "billpayment");
            vnp_Params.put("vnp_OrderType", "booking");

            String locate = (language != null && !language.isEmpty()) ? language : "vn";
            vnp_Params.put("vnp_Locale", locate);
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                            .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            String paymentUrl = Config.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

            Map<String, Object> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("data", paymentUrl);

            return ResponseEntity.ok(response);

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


    @GetMapping("/about")
    public String gioiThieu(){
        return "list/KhachHang/gioiThieu";
    }

    @GetMapping("/listPhong")
    public String listphong(){
        return "list/KhachHang/ListPhong";
    }

    @GetMapping("/phong-theo-loai")
    public ResponseEntity<List<PhongDTO>> getRoomsByRoomType(@RequestParam(value = "loaiPhongId", required = false) Integer loaiPhongId) {
        if (loaiPhongId == null || loaiPhongId <= 0) {
            return ResponseEntity.ok(Collections.emptyList()); // Trả về [] nếu loaiPhongId không hợp lệ
        }

        List<Phong> phongs = phongService.getPhongsByLoaiPhong1(loaiPhongId);

        if (phongs.isEmpty()) {
            System.out.println("Không tìm thấy phòng nào cho loại phòng ID: " + loaiPhongId); // Thêm log này
            return ResponseEntity.ok(Collections.emptyList()); // Trả về [] nếu không tìm thấy phòng nào
        }

        List<PhongDTO> phongDTOs = phongs.stream()
                .map(phongService::convertToPhongDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(phongDTOs);
    }
    @GetMapping("/hangphongdetail")
    public String showRoomDetailPage() {
        return "List/KhachHang/hangphongdetail";
    }



    @GetMapping("/api/available-rooms")
    @ResponseBody
    public List<PhongDTO> getAvailableRooms(
            @RequestParam LocalDateTime ngayNhan,
            @RequestParam LocalDateTime ngayTra,
            @RequestParam int soLuongNguoi) {
        return phongService.findAvailableRooms(ngayNhan, ngayTra, soLuongNguoi);
    }


    @GetMapping("/tim")
    public String tim() {
        return "list/KhachHang/tim"; // Không cần .html
    }


    @GetMapping("/search1")
    public String searchRooms(@RequestParam("ngayNhan") String ngayNhan,
                              @RequestParam("ngayTra") String ngayTra,
                              @RequestParam("soPhong") Integer soPhong,
                              @RequestParam("soNguoi") Integer soNguoi,
                              Model model) {
        // Chuyển đổi chuỗi sang LocalDateTime
        LocalDateTime startDate = LocalDateTime.parse(ngayNhan);
        LocalDateTime endDate = LocalDateTime.parse(ngayTra);

        // Lấy danh sách phòng có sẵn theo số người tối đa của loại phòng
        List<Phong> availableRooms = phongService.getAvailableRoomsWithMaxGuests(startDate, endDate, soNguoi);

        // Thêm dữ liệu vào model để trả về view
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("soPhong", soPhong);
        model.addAttribute("soNguoi", soNguoi);

        return "list/KhachHang/tim"; // Tên view trả về (có thể là searchResults.html)
    }
}
