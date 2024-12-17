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
import org.apache.commons.lang3.StringUtils;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
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




    @Autowired
    private ThamSoService thamSoService;




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

        // Lấy ra vnp_TxnRef - mã giao dịch duy nhất
        String vnp_TxnRef = fields.get("vnp_TxnRef");
        List<Integer> idListDatPhong = vnpayService.getTransaction(vnp_TxnRef);

        // Kiểm tra chữ ký hợp lệ
        String responseCode = fields.get("vnp_ResponseCode");
        boolean isValidSignature = signValue.equals(vnp_SecureHash);

        if (isValidSignature && "00".equals(responseCode)) {
            System.out.println("isValidSignature:" + isValidSignature);
            System.out.println("vnp_TxnRef: " + vnp_TxnRef);
            // Chuyển vnp_TxnRef thành ID đặt phòng nếu nó là một ID hợp lệ
            for (Integer idDatPhong:idListDatPhong) {
                //                Integer idDatPhong = Integer.parseInt(vnp_TxnRef); // Giả sử vnp_TxnRef là một ID duy nhất
                vnpayService.ThanhToanThanhCong(idDatPhong);

                // Gửi email xác nhận đặt phòng thành công cho khách hàng
                DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
                Integer idKhachHang = datPhong.getKhachHang().getId();
                KhachHangDTO khachHangDTO = khachHangService.findById(idKhachHang);
                KhachHang khachHang = khachHangService.convertToEntity(khachHangDTO);

                // Định dạng ngày và giờ
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String ngayNhanFormatted = datPhong.getNgayNhan().format(dateTimeFormatter);
                String ngayTraFormatted = datPhong.getNgayTra().format(dateTimeFormatter);

                // Định dạng tiền tệ
                Float soTien = Float.valueOf(requestParams.get("vnp_Amount")) / 100;

                // Lấy danh sách tên phòng (nếu có)
                Set<Phong> phongs = datPhong.getPhong().getDatPhongs().stream()
                        .map(DatPhong::getPhong)
                        .collect(Collectors.toSet());

                List<String> roomNames = phongs.stream()
                        .map(Phong::getTenPhong)
                        .collect(Collectors.toList());




                String emailKhachHang = khachHang.getEmail();
                String subject = "Xác nhận đặt phòng thành công tại DRAGONBALL HOTEL";
                String text = "<html>" +
                        "<body style='font-family: Arial, sans-serif; color: #333333; background-color: #f9f9f9;'>" +
                        "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px;'>" +
                        "<h2 style='color: #4CAF50; text-align: center;'>Xin chào " + khachHang.getHoVaTen() + ",</h2>" +
                        "<p style='font-size: 16px;'>Cảm ơn bạn đã lựa chọn <strong>DRAGONBALL HOTEL</strong> cho kỳ nghỉ của mình!</p>" +
                        "<p style='font-size: 16px;'>Dưới đây là thông tin đặt phòng của bạn:</p>" +
                        "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                        "<p style='font-size: 16px;'><strong>Phòng của bạn là:</strong> " + roomNames.toString() + "</p>" +
                        "<p style='font-size: 16px;'><strong>Ngày nhận phòng:</strong> " + ngayNhanFormatted + "</p>" +
                        "<p style='font-size: 16px;'><strong>Ngày trả phòng:</strong> " + ngayTraFormatted + "</p>" +
                        "<p style='font-size: 16px;'><strong>Số tiền cọc:</strong> " + String.format("%,.0f", soTien) + " VND</p>" +
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

            }
        }else {
            System.out.println("isValidSignature:" + isValidSignature);
            System.out.println("vnp_TxnRef: " + vnp_TxnRef);
            for (Integer idDatPhong : idListDatPhong){
                vnpayService.ThanhToanThatBai(idDatPhong);
            }
//            Integer idDatPhong = Integer.parseInt(vnp_TxnRef); // Giả sử vnp_TxnRef là một ID duy nhất
        }


        // Truyền dữ liệu sang model để hiển thị trên view
        model.addAttribute("isValidSignature", isValidSignature);
        model.addAttribute("vnp_TxnRef", requestParams.get("vnp_TxnRef"));
        String amountString = requestParams.get("vnp_Amount");
        float amount = (amountString != null) ? Float.parseFloat(amountString) / 100 : 0f;

// Định dạng tiền Việt Nam
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = currencyFormat.format(amount);
        model.addAttribute("vnp_Amount", formattedAmount);
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
    public String showRoomDetailPhong(@RequestParam(required = false) String roomId,
                                      @RequestParam(required = false) String roomIds,
                                      Model model) {
        // Lấy thông tin tài khoản từ session
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession();
        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null && taiKhoanDTO.getKhachHangDTO().getId() != null) {
            model.addAttribute("idKhachHang", taiKhoanDTO.getKhachHangDTO().getId());
        }

        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null && taiKhoanDTO.getKhachHangDTO().getHoVaTen() != null) {
            model.addAttribute("hoVaTen", taiKhoanDTO.getKhachHangDTO().getHoVaTen());
        }

        // Trường hợp chỉ có một phòng được chọn
        if (roomId != null) {
            try {
                Integer idPhong = Integer.parseInt(roomId);
                Phong phong = phongService.getPhongById1(idPhong);

                if (phong == null) {
                    model.addAttribute("error", "Phòng không tồn tại.");
                } else {
                    // Chuyển đổi Phong sang DTO và thêm vào model
                    PhongDTO phongDTO = phongService.convertToPhongDTO(phong);
                    model.addAttribute("phong", phongDTO);
                    return "list/KhachHang/datPhong"; // Trả về view datPhong.html
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "ID phòng không hợp lệ.");
            }
        }


        if (roomIds != null) {
            try {
                List<Integer> selectedRoomIds = Arrays.stream(roomIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                List<PhongDTO> phongDTOList = phongService.getPhongByIds(selectedRoomIds);

                // Kiểm tra từng phòng trong danh sách
                List<PhongDTO> validPhongDTOList = new ArrayList<>();
                for (PhongDTO phongDTO : phongDTOList) {
                    if (phongDTO != null && phongDTO.getTenPhong() != null) {
                        validPhongDTOList.add(phongDTO);
                    }
                }

                if (validPhongDTOList.isEmpty()) {
                    model.addAttribute("error", "Không có phòng nào hợp lệ trong danh sách.");
                } else {
                    String idPhongStr = phongDTOList.stream()
                            .map(phong -> String.valueOf(phong.getIdPhong()))
                            .collect(Collectors.joining(","));
                    String idLoaiPhongStr = phongDTOList.stream()
                            .map(phong -> String.valueOf((phong.getIdLoaiPhong())))
                            .collect(Collectors.joining(","));
                    model.addAttribute("phongList", validPhongDTOList);
                    model.addAttribute("idPhong",idPhongStr);
                    model.addAttribute("idLoaiPhong",idLoaiPhongStr);
                    return "list/KhachHang/datNhieuPhong";
                }

            } catch (NumberFormatException e) {
                model.addAttribute("error", "Danh sách ID phòng không hợp lệ.");
            }
        }




        return "list/KhachHang/datPhong"; // Trả về view datPhong.html
    }




    //
    @PostMapping("/dat-phong")
    public ResponseEntity<?> createDatNhieuPhongKhachHang1(
            @RequestParam("idLoaiPhong") List<String> idLoaiPhongStrList,
            @RequestParam("idPhong") String idPhongStr,
            @RequestParam("gia") Long amount,
            @RequestParam("bankCode") String bankCode,
            @RequestParam("language") String language,
            @RequestParam("ngayNhan") String ngayNhanStr,
            @RequestParam("ngayTra") String ngayTraStr,
            @RequestParam("cccd") String cccd,
            @RequestParam("idKhachHang") String idKhachHangStr,
            @RequestParam("thanhToan100") boolean thanhToan100,
            HttpServletRequest req) {

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


                float tienCocPhong;
                if (thanhToan100) {
                    // Nếu chọn thanh toán 100%
                    tienCocPhong = tongTienPhongPhong;
                } else {
                    // Nếu chọn thanh toán 80%
                    tienCocPhong = tongTienPhongPhong * 0.8f;
                }

//                // Cập nhật tổng tiền phòng và tiền cọc
                tongTienPhong += tongTienPhongPhong;
                tienCoc += tienCocPhong;

                // Tạo đối tượng DatPhong cho từng phòng
                DatPhong datPhong = new DatPhong();
                datPhong.setMaDatPhong(generateMaDatPhong() + "" + (i + 1));
                datPhong.setPhong(selectedPhong);
                datPhong.setNgayNhan(ngayNhan);
                datPhong.setNgayTra(ngayTra);
                datPhong.setNgayDat(LocalDateTime.now());
                datPhong.setCccd(cccd);
                datPhong.setTongTien(tongTienPhongPhong);
                datPhong.setTienCoc(tongTienPhongPhong * 0.8f);
                float tienConLai = datPhong.getTongTien() - datPhong.getTienCoc(); // Tính tiền còn lại
                datPhong.setTienConLai(tienConLai);
                datPhong.setLoaiPhong(loaiPhong);
                datPhong.setKhachHang(khachHang);
                datPhong.setTinhTrang("Đang chờ thanh toán....");
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


//            DatPhong booking = datPhongList.get(0);
//            Integer id = booking.getIdDatPhong();
            List<Integer> idList = datPhongList.stream()
                    .map(DatPhong::getIdDatPhong)
                    .collect(Collectors.toList());
            // Tạo mã vnp_TxnRef duy nhất
            String vnp_TxnRef = "DP" + String.valueOf(System.currentTimeMillis()).substring(0, 12);


            // Lưu ánh xạ mã giao dịch với danh sách ID phòng
            Map<String, List<Integer>> transactionMap = new HashMap<>();
            transactionMap.put(vnp_TxnRef, idList);
            vnpayService.saveTransaction(vnp_TxnRef,idList);
            System.out.println("transactionMap:"+transactionMap);
            System.out.println("snp:"+vnp_TxnRef);

// Thông tin cấu hình
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_IpAddr = Config.getIpAddress(req);
            String vnp_TmnCode = Config.vnp_TmnCode;
            String vnp_Amount = String.valueOf((int) (tienCoc * 100)); // Số tiền (chuyển sang đồng)
            String vnp_CurrCode = "VND";
            String vnp_OrderInfo = "billpayment";
            String vnp_OrderType = "booking";
            String locate = (language != null && !language.isEmpty()) ? language : "vn";
            String vnp_ReturnUrl = Config.vnp_ReturnUrl;

// Tạo ngày giờ
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

// Các tham số VNPAY
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", vnp_OrderType);
            vnp_Params.put("vnp_Locale", locate);
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

// Tạo chuỗi tham số
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

// Tạo chữ ký bảo mật
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());

// Tạo URL thanh toán
            String paymentUrl = Config.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

// Trả về URL thanh toán
            Map<String, Object> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("data", paymentUrl);

            System.out.println("dataPayment:"+paymentUrl);

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

        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        System.out.println("TaiKhoanDTO: " + taiKhoanDTO); // In giá trị TaiKhoanDTO để kiểm tra

        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null && taiKhoanDTO.getKhachHangDTO().getHoVaTen() != null) {
            model.addAttribute("hoVaTen", taiKhoanDTO.getKhachHangDTO().getHoVaTen());
        }
        // Chuyển đổi chuỗi sang LocalDateTime
        LocalDateTime startDate = LocalDateTime.parse(ngayNhan);
        LocalDateTime endDate = LocalDateTime.parse(ngayTra);

        // Lấy danh sách phòng có sẵn theo số người tối đa của loại phòng
        List<Phong> availableRooms = phongService.getAvailableRoomsWithMaxGuests(startDate, endDate, soNguoi,soPhong);

        // Chuyển danh sách phòng thành chuỗi
        // Tạo chuỗi các ID phòng
        String rooms = availableRooms.stream()
                .map(phong -> String.valueOf(phong.getIdPhong())) // Đảm bảo getId trả về Integer
                .collect(Collectors.joining(","));

        // Thêm dữ liệu vào model để trả về view
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("soPhong", soPhong);
        model.addAttribute("soNguoi", soNguoi);
        model.addAttribute("rooms", rooms);

        return "list/KhachHang/tim"; // Tên view trả về (có thể là searchResults.html)
    }
}
