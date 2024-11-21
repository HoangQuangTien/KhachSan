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
    public ResponseEntity<List<DanhGiaDTO>> getDanhGiaByPhong(@PathVariable String idPhong) {
        // Kiểm tra xem idPhong có phải là một số hợp lệ hay không
        Integer idPhongInt = null;
        try {
            idPhongInt = Integer.valueOf(idPhong);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build(); // Trả về HTTP 400 nếu idPhong không hợp lệ
        }

        // Sau khi xác nhận idPhong hợp lệ, gọi dịch vụ để lấy danh sách đánh giá
        List<DanhGiaDTO> danhGiaList = danhGiaService.getDanhGiaByPhong(idPhongInt);
        return ResponseEntity.ok(danhGiaList);
    }


    @PostMapping
    public ResponseEntity<DanhGiaDTO> addDanhGia(@RequestBody DanhGiaDTO danhGiaDTO) {
        return ResponseEntity.ok(danhGiaService.addDanhGia(danhGiaDTO));
    }
}

