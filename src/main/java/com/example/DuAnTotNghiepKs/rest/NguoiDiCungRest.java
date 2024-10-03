package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/nguoidicung")
public class NguoiDiCungRest {

    @Autowired
    private DatPhongService datPhongService;

    @GetMapping()
    public String showDatPhongList(Model model) {
        // Lấy tất cả thông tin đặt phòng chưa check-in
        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaCheckIn();

        // Truyền danh sách đặt phòng vào model
        model.addAttribute("datPhongs", datPhongs);

        return "list/QuanLyDatPhong/nguoidicung"; // Đường dẫn tới trang HTML hiển thị danh sách đặt phòng
    }
}
