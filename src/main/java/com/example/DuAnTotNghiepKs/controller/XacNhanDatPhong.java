package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.LichSuDatPhong;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.EmailService;
import com.example.DuAnTotNghiepKs.service.LichSuDatPhongService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/xacnhan")
public class XacNhanDatPhong {

    private final DatPhongService datPhongService;
    private final TaiKhoanService taiKhoanService;
    private final LichSuDatPhongService lichSuDatPhongService;
    private final EmailService emailService;
    public XacNhanDatPhong(DatPhongService datPhongService, TaiKhoanService taiKhoanService, LichSuDatPhongService lichSuDatPhongService, EmailService emailService) {
        this.datPhongService = datPhongService;
        this.taiKhoanService = taiKhoanService;
        this.lichSuDatPhongService = lichSuDatPhongService;
        this.emailService = emailService;
    }

    @GetMapping()
    public String xacNhan(Model model){
        // Lấy tất cả thông tin đặt phòng chưa check-in
        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaXacNhan();

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }
        // Truyền danh sách đặt phòng vào model
        model.addAttribute("datPhongs", datPhongs);
        return "list/QuanLyDatPhong/xacnhan";
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> xacThuc(@RequestParam List<Integer> idDatPhongs ){
        try {
            for (Integer idDatPhong : idDatPhongs){
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

                datPhong.setTinhTrang("Chưa Checkin");
                datPhongService.saveDatPhong1(datPhong);
            }



            return ResponseEntity.ok().body(Map.of("success","Xác nhận thành công"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error","Đã xảy ra lỗi: "+e));
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
