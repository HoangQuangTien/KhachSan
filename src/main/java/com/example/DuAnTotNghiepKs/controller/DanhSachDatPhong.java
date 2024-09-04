package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/danhsach")
public class DanhSachDatPhong {

    @Autowired
    private DatPhongService datPhongService;

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
            // Cập nhật trạng thái đặt phòng thành "đang ở"
            datPhong.setTinhTrang(true);
            datPhongService.saveDatPhong1(datPhong);
            return ResponseEntity.ok(Map.of("success", "Check-in thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
