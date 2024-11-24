package com.example.DuAnTotNghiepKs.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguageController {

    @GetMapping("/set-lang")
    public String setLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        // Lưu ngôn ngữ vào session hoặc cookie
        request.getSession().setAttribute("lang", lang);
        return "list/KhachHang/khachHang";  // Chuyển hướng về trang chính
    }
}
