package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.LichSuDatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.LichSuDatPhongService;
import com.example.DuAnTotNghiepKs.service.PhongService;
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

    @GetMapping()
    public String showDatPhongList(Model model) {
        // Lấy tất cả thông tin đặt phòng chưa check-in
        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaCheckIn();

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

            Phong phong = datPhong.getPhong();
            // Cập nhật trạng thái đặt phòng thành "đang ở"
            datPhong.setTinhTrang("Đã Checkin");
            datPhongService.saveDatPhong1(datPhong);


            phong.setTrangThai(false); // false biểu thị phòng đã hết
            phongService.savePhong(phong); // Giả sử có một dịch vụ lưu phòng

            datPhong.setTrangThai(false);
            datPhong.setNgayCheckIn(LocalDateTime.now()); // lấy ngày hiện tại
            datPhongService.saveDatPhong1(datPhong);
            return ResponseEntity.ok(Map.of("success", "Check-in thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/cancel-booking")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            @RequestParam("idDatPhong") Integer idDatPhong,
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
            lichSuDatPhong.setChiTietThayDoi("Phòng: " + datPhong.getPhong().getTenPhong() + " đã hủy. Lý do: " + reason);
            lichSuDatPhong.setThoiGianThayDoi(new Date());  // Thời gian hủy hiện tại
            lichSuDatPhongService.saveLichSuDatPhong(lichSuDatPhong);  // Lưu lịch sử hủy


            response.put("success", true);
            response.put("message", "Hủy phòng thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
