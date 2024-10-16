package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.LichLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.service.LichLamViecService;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/quan-ly-lich-lam-viec")
public class LichLamViecController {

    @Autowired
    private LichLamViecService lichLamViecService;
    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public String loadAll(){
        return "list/QuanLyLichLamViec/lichLamViec";
    }

    @GetMapping("/load")
    public ResponseEntity<List<LichLamViecDTO>> load(){
        List<LichLamViecDTO> lichLamViecDTOS = lichLamViecService.getAll();
        return ResponseEntity.ok(lichLamViecDTOS);
    }



}
