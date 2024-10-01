package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
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

//    @GetMapping("/{id}")
//    public ResponseEntity<PhongDTO> getPhongById(@PathVariable("id") Integer id) {
//        Optional<Phong> phongOpt = phongService.getPhongById(id);
//        if (phongOpt.isPresent()) {
//            Phong phong = phongOpt.get();
//            PhongDTO phongDTO = new PhongDTO(phong.getTenPhong(), phong.getGia(), ,phong.getTinhTrang(), phong.getTrangThai());
//            return ResponseEntity.ok(phongDTO);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
