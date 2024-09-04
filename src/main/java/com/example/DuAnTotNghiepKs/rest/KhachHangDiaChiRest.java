package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import com.example.DuAnTotNghiepKs.service.DiaChiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dia-chi")
public class KhachHangDiaChiRest {

    private DiaChiKhachHangService diaChiKhachHangService;
    @Autowired

    public KhachHangDiaChiRest(DiaChiKhachHangService diaChiKhachHangService) {
        this.diaChiKhachHangService = diaChiKhachHangService;
    }
    @GetMapping("hien-thi")
    public ResponseEntity<List> GetAll2(){
        List<DiaChiKhachHangDTO>diaChiKhachHangDTOS=diaChiKhachHangService.getAll();
        return ResponseEntity.ok(diaChiKhachHangDTOS);
    }

}
