package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/danhsach")
public class DanhSachDatPhong {

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private LichSuDatPhongService lichSuDatPhongService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private ThamSoService thamSoService;

    @Autowired
    private EmailService emailService;

    @GetMapping()
    public String showDatPhongList(Model model) {
        // Lấy tất cả thông tin đặt phòng chưa check-in
        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaCheckIn();

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }
        // Truyền danh sách đặt phòng vào model
        model.addAttribute("datPhongs", datPhongs);

        return "list/QuanLyDatPhong/danhsach"; // Đường dẫn tới trang HTML hiển thị danh sách đặt phòng
    }


    @PostMapping("/check-in")
    public ResponseEntity<?> checkInDatPhong(@RequestParam("idDatPhong") Integer idDatPhong) {
        try {
            // Tìm đặt phòng theo ID
            DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
            if (datPhong == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Đặt phòng không tồn tại."));
            }

            // Lấy thông tin tài khoản từ session
            TaiKhoan taiKhoan = taiKhoanService.getTaiKhoanTuSession1();
            if (taiKhoan == null || taiKhoan.getNhanVien() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin nhân viên."));
            }

            // Kiểm tra nếu phòng có yêu cầu người đi cùng
            if (datPhong.getLoaiPhong().getSoNguoiToiDa() > 1 &&
                    (datPhong.getNguoiDiCungList() == null || datPhong.getNguoiDiCungList().isEmpty())) {
                return ResponseEntity.ok(Map.of("warning", "Phòng chưa có người đi cùng. Bạn có muốn tiếp tục check-in?"));
            }



            // Lấy thời gian dự kiến nhận phòng từ đặt phòng
            LocalDateTime ngayNhanPhong = datPhong.getNgayNhan();

            // Lấy giá trị cho phép từ bảng tham số bằng id
            Long thamSoId = 1L; // id của tham số check-in được phép trong bảng ThamSo
            String checkinLeewayStr = thamSoService.getValueById(thamSoId);
            if (checkinLeewayStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tham số kiểm tra check-in chưa được cấu hình."));
            }

            // Chuyển đổi giá trị từ String sang Integer
            Integer checkinLeewayMinutes = Integer.parseInt(checkinLeewayStr);

            // Lấy thời gian hiện tại
            LocalDateTime now = LocalDateTime.now();

            // Kiểm tra nếu khách hàng check-in trước quá 5 phút (hoặc giá trị từ tham số)
            if (now.isBefore(ngayNhanPhong.minusMinutes(checkinLeewayMinutes))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chỉ được phép check-in trước tối đa " + checkinLeewayMinutes + " phút."));
            }
            // Lấy thời gian dự kiến nhận phòng từ đặt phòng


            // Cập nhật thông tin check-in
            NhanVien nhanVienCheckIn = taiKhoan.getNhanVien();
            datPhong.setNhanVienCheckIn(nhanVienCheckIn); // Lưu đối tượng NhanVien

            Phong phong = datPhong.getPhong();
            // Cập nhật trạng thái đặt phòng thành "đang ở"
            datPhong.setTinhTrang("Đã Checkin");
            datPhongService.saveDatPhong1(datPhong);


            phong.setTrangThai(false); // false biểu thị phòng đã hết
            phongService.savePhong(phong); // Giả sử có một dịch vụ lưu phòng

            datPhong.setTrangThai(false);
            datPhong.setNgayCheckIn(LocalDateTime.now()); // lấy ngày hiện tại

            datPhongService.saveDatPhong1(datPhong);


            // Gửi email xác nhận check-in thành công
            String emailKhachHang = datPhong.getKhachHang().getEmail(); // Lấy email khách hàng từ đối tượng đặt phòng
            String subject = "Xác nhận check-in thành công tại DRAGONBALL HOTEL";
            String text = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333333; background-color: #f9f9f9;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px;'>" +
                    "<h2 style='color: #4CAF50; text-align: center;'>Xin chào " + datPhong.getKhachHang().getHoVaTen() + ",</h2>" +
                    "<p style='font-size: 16px;'>Chúng tôi xin thông báo rằng bạn đã check-in thành công tại <strong>DRAGONBALL HOTEL</strong>!</p>" +
                    "<p style='font-size: 16px;'>Dưới đây là thông tin check-in của bạn:</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'><strong>Nhân viên checkin:</strong> " + datPhong.getNhanVienCheckIn().getHoTen() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Phòng:</strong> " + datPhong.getPhong().getTenPhong() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày nhận phòng:</strong> " + datPhong.getNgayNhan() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày check-in:</strong> " + datPhong.getNgayTra() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Số người tối đa:</strong> " + datPhong.getLoaiPhong().getSoNguoiToiDa() + "</p>" +
                    "<p style='font-size: 16px;'>Chúng tôi rất mong bạn sẽ có một kỳ nghỉ thoải mái tại khách sạn của chúng tôi!</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'>Nếu bạn có bất kỳ câu hỏi hoặc yêu cầu nào, vui lòng liên hệ với chúng tôi.</p>" +
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
            return ResponseEntity.ok(Map.of("success", "Check-in thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-check-in")
    public ResponseEntity<?> confirmCheckIn(@RequestParam("idDatPhong") Integer idDatPhong) {
        try {
            // Tìm đặt phòng theo ID
            DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
            if (datPhong == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Đặt phòng không tồn tại."));
            }

            // Lấy thông tin tài khoản từ session
            TaiKhoan taiKhoan = taiKhoanService.getTaiKhoanTuSession1();
            if (taiKhoan == null || taiKhoan.getNhanVien() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin nhân viên."));
            }


            // Lấy thời gian dự kiến nhận phòng từ đặt phòng
            LocalDateTime ngayNhanPhong = datPhong.getNgayNhan();

            // Lấy giá trị cho phép từ bảng tham số bằng id
            Long thamSoId = 1L; // id của tham số check-in được phép trong bảng ThamSo
            String checkinLeewayStr = thamSoService.getValueById(thamSoId);
            if (checkinLeewayStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tham số kiểm tra check-in chưa được cấu hình."));
            }

            // Chuyển đổi giá trị từ String sang Integer
            Integer checkinLeewayMinutes = Integer.parseInt(checkinLeewayStr);

            // Lấy thời gian hiện tại
            LocalDateTime now = LocalDateTime.now();

            // Kiểm tra nếu khách hàng check-in trước quá 5 phút (hoặc giá trị từ tham số)
            if (now.isBefore(ngayNhanPhong.minusMinutes(checkinLeewayMinutes))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chỉ được phép check-in trước tối đa " + checkinLeewayMinutes + " phút."));
            }
            // Lấy thời gian dự kiến nhận phòng từ đặt phòng


            // Lấy thời gian dự kiến nhận phòng từ đặt phòng
            LocalDateTime ngayTraPhong = datPhong.getNgayTra();
            // Kiểm tra nếu đặt phòng đã hết hạn (quá ngày nhận phòng) hoặc đã bị hủy
            if ((ngayTraPhong != null && now.isAfter(ngayTraPhong)) || "Đã Hủy".equals(datPhong.getTinhTrang())) {
                // Cập nhật trạng thái đặt phòng thành "Đã Hủy"
                datPhong.setTinhTrang("Đã Hủy");
                datPhongService.saveDatPhong1(datPhong);


                return ResponseEntity.badRequest().body(Map.of("error", "Không thể check-in vì đặt phòng đã quá hạn hoặc bị hủy. Trạng thái đã được cập nhật."));
            }

            // Cập nhật thông tin check-in
            NhanVien nhanVienCheckIn = taiKhoan.getNhanVien();
            datPhong.setNhanVienCheckIn(nhanVienCheckIn); // Lưu đối tượng NhanVien

            Phong phong = datPhong.getPhong();
            // Cập nhật trạng thái đặt phòng thành "đang ở"
            datPhong.setTinhTrang("Đã Checkin");
            datPhongService.saveDatPhong1(datPhong);


            phong.setTrangThai(false); // false biểu thị phòng đã hết
            phongService.savePhong(phong); // Giả sử có một dịch vụ lưu phòng

            datPhong.setTrangThai(false);
            datPhong.setNgayCheckIn(LocalDateTime.now()); // lấy ngày hiện tại

            datPhongService.saveDatPhong1(datPhong);


            // Gửi email xác nhận check-in thành công
            String emailKhachHang = datPhong.getKhachHang().getEmail(); // Lấy email khách hàng từ đối tượng đặt phòng
            String subject = "Xác nhận check-in thành công tại DRAGONBALL HOTEL";
            String text = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333333; background-color: #f9f9f9;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px;'>" +
                    "<h2 style='color: #4CAF50; text-align: center;'>Xin chào " + datPhong.getKhachHang().getHoVaTen() + ",</h2>" +
                    "<p style='font-size: 16px;'>Chúng tôi xin thông báo rằng bạn đã check-in thành công tại <strong>DRAGONBALL HOTEL</strong>!</p>" +
                    "<p style='font-size: 16px;'>Dưới đây là thông tin check-in của bạn:</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'><strong>Nhân viên checkin:</strong> " + datPhong.getNhanVienCheckIn().getHoTen() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Phòng:</strong> " + datPhong.getPhong().getTenPhong() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày nhận phòng:</strong> " + datPhong.getNgayNhan() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Ngày check-in:</strong> " + datPhong.getNgayTra() + "</p>" +
                    "<p style='font-size: 16px;'><strong>Số người tối đa:</strong> " + datPhong.getLoaiPhong().getSoNguoiToiDa() + "</p>" +
                    "<p style='font-size: 16px;'>Chúng tôi rất mong bạn sẽ có một kỳ nghỉ thoải mái tại khách sạn của chúng tôi!</p>" +
                    "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                    "<p style='font-size: 16px;'>Nếu bạn có bất kỳ câu hỏi hoặc yêu cầu nào, vui lòng liên hệ với chúng tôi.</p>" +
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
            return ResponseEntity.ok(Map.of("success", "Check-in thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/cancel-booking")
    public ResponseEntity<Map<String, Object>> cancelBooking(@RequestParam("idDatPhong") Integer idDatPhong,
                                                             @RequestParam("reason") String reason) { // Thêm tham số lý do hủy
        Map<String, Object> response = new HashMap<>();
        try {
            // Tìm đặt phòng theo ID
            DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
            if (datPhong == null) {
                response.put("error", "Đặt phòng không tồn tại.");
                return ResponseEntity.badRequest().body(response);
            }

            // Chỉ cập nhật trạng thái của đặt phòng và lý do hủy

            datPhong.setTinhTrang("Đã Hủy");

            // Lưu lịch sử hủy
            LichSuDatPhong lichSuDatPhong = new LichSuDatPhong();
            lichSuDatPhong.setDatPhong(datPhong);
            lichSuDatPhong
                    .setChiTietThayDoi("Phòng: " + datPhong.getPhong().getTenPhong() + " đã hủy. Lý do: " + reason);
            lichSuDatPhong.setThoiGianThayDoi(new Date()); // Thời gian hủy hiện tại
            lichSuDatPhongService.saveLichSuDatPhong(lichSuDatPhong);  // Lưu lịch sử hủy
            emailService.sendEmailHuyPhong(datPhong.getKhachHang().getEmail(), "Hủy Phòng", datPhong.getKhachHang(),
                    datPhong.getPhong());

            response.put("success", true);
            response.put("message", "Hủy phòng thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
