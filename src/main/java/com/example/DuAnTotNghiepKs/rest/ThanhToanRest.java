package com.example.DuAnTotNghiepKs.rest;


import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.service.KhuyenMaiService;
import com.example.DuAnTotNghiepKs.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/km")
public class ThanhToanRest {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private ThanhToanService thanhToanService;

    @GetMapping("/hienThi")
    public ResponseEntity<Page<ThanhToanDTO>> getThanhToanList(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("idThanhToan").descending());
        Page<ThanhToanDTO> thanhToanPage = thanhToanService.findAllByTinhTrang(pageable);

        return ResponseEntity.ok(thanhToanPage); // Trả về Page<ThanhToanDTO> dưới dạng JSON
    }

    @GetMapping("/active")
    public ResponseEntity<List<KhuyenMai>> getAllActiveKhuyenMai() {
        List<KhuyenMai> khuyenMaiList = khuyenMaiService.getAllActiveKhuyenMai();
        return new ResponseEntity<>(khuyenMaiList, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<List<ThanhToanDTO>> search(@RequestParam String query ,Date ngayThanhToan) {
        List<ThanhToanDTO> results = thanhToanService.search(query,ngayThanhToan);
        return ResponseEntity.ok(results);
    }
}
