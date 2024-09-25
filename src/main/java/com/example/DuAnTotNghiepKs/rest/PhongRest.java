package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.service.PhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/phong")
public class PhongRest {

    @Autowired
    private PhongService phongService; // Dịch vụ xử lý logic liên quan đến phòng

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Phong>> getPhongById(@PathVariable("id") Integer id) {
        Optional<Phong> phong = phongService.getPhongById(id); // Tìm phòng theo ID
        if (phong != null) {
            return ResponseEntity.ok(phong); // Trả về thông tin phòng nếu tìm thấy
        } else {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy
        }
    }
}
