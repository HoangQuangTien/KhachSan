package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NhanVienRest {

    private final NhanVienService nhanVienService;

    public NhanVienRest(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping("/api/nhan-vien")
    public ResponseEntity<List<NhanVienDTO>> getAll() {
        List<NhanVienDTO> nhanVienDTOS = nhanVienService.getAll();

        // Lọc danh sách nhân viên có trạng thái là true
        List<NhanVienDTO> filteredNhanVienDTOS = nhanVienDTOS.stream()
                .filter(nhanVienDTO -> nhanVienDTO.getTrangThai() != false)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredNhanVienDTOS);
    }


}
