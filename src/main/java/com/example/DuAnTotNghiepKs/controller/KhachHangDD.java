package com.example.DuAnTotNghiepKs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
public class KhachHangDD {


    @GetMapping("/khach-hang")
    public String hienThi(){
        return "list/KhachHang/khachHang";
    }

}
