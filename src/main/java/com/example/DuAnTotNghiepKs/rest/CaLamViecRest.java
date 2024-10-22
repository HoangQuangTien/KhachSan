package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.CaLamViecDTO;
import com.example.DuAnTotNghiepKs.service.CaLamViecService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CaLamViecRest {

    private final CaLamViecService caLamViecService;

    public CaLamViecRest(CaLamViecService caLamViecService) {
        this.caLamViecService = caLamViecService;
    }

    @GetMapping("/api/ca-lam-viec")
    public ResponseEntity<List<CaLamViecDTO>> getAll(){
        List<CaLamViecDTO> caLamViecDTOS = caLamViecService.getAll();
        return ResponseEntity.ok(caLamViecDTOS);
    }

}
