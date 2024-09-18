package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.LoaiPhongDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.service.LoaiPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loaiPhong")
public class LoaiPhongRest {

    @Autowired
    private LoaiPhongService loaiPhongService;

    @GetMapping("/{id}")
    public ResponseEntity<LoaiPhongDTO> getLoaiPhong(@PathVariable int id) {
        LoaiPhong loaiPhong = loaiPhongService.findById(id);
        if (loaiPhong == null) {
            return ResponseEntity.notFound().build();
        }
        LoaiPhongDTO dto = new LoaiPhongDTO();
        dto.setTenTang(loaiPhong.getTang().getTenTang());
        dto.setTenDienTich(loaiPhong.getDienTich().getTenDienTich());
        dto.setGia(loaiPhong.getGia());
        dto.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
        return ResponseEntity.ok(dto);
    }
}

