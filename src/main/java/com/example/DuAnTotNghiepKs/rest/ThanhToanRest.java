package com.example.DuAnTotNghiepKs.rest;


import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/km")
public class ThanhToanRest {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @GetMapping("/active")
    public ResponseEntity<List<KhuyenMai>> getAllActiveKhuyenMai() {
        List<KhuyenMai> khuyenMaiList = khuyenMaiService.getAllActiveKhuyenMai();
        return new ResponseEntity<>(khuyenMaiList, HttpStatus.OK);
    }
}
