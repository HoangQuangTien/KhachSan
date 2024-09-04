package com.example.DuAnTotNghiepKs.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class ThongKeController {

    @GetMapping("/thongke")
    public String getThongKePage() {
        return "list/QuanLyThongKe/thongke"; // Nếu sử dụng Thymeleaf hoặc trả về view template
    }
}
