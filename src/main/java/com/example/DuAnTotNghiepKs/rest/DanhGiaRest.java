package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.DanhGiaDTO;
import com.example.DuAnTotNghiepKs.service.DanhGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danhgia")
public class DanhGiaRest {

    @Autowired
    private DanhGiaService danhGiaService;

    @GetMapping("/{idPhong}")
    public ResponseEntity<List<DanhGiaDTO>> getDanhGiaByPhong(@PathVariable Integer idPhong) {
        List<DanhGiaDTO> danhGiaList = danhGiaService.getDanhGiaByPhong(idPhong);
        return ResponseEntity.ok(danhGiaList);
    }

    @PostMapping
    public ResponseEntity<DanhGiaDTO> addDanhGia(@RequestBody DanhGiaDTO danhGiaDTO) {
        return ResponseEntity.ok(danhGiaService.addDanhGia(danhGiaDTO));
    }
}

