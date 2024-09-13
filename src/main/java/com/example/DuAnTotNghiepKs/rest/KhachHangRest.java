package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("khach-hang")
public class KhachHangRest {
    private final KhachHangService khachHangService;

    @Autowired
    public KhachHangRest(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping("hien-thi")
    public ResponseEntity<List<KhachHangDTO>> getAll() {
        List<KhachHangDTO> khachHangDTOS = khachHangService.getAll();
        return ResponseEntity.ok(khachHangDTOS);
    }

    @GetMapping("hien-thi-phan-trang")
    public ResponseEntity<Page<KhachHangDTO>> getAllPaginated(@PageableDefault(size = 5) Pageable pageable) {
        Page<KhachHangDTO> khachHangDTOS = khachHangService.getAll(pageable);
        return ResponseEntity.ok(khachHangDTOS);
    }

    @GetMapping("/search")
    public ResponseEntity<List<KhachHangDTO>> search(@RequestParam String query) {
        List<KhachHangDTO> results = khachHangService.search(query);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/filter")
    public List<KhachHangDTO> filter(@RequestParam String status) {
        return khachHangService.filter(status);
    }


    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkEmailAndPhone(
            @RequestParam String email, @RequestParam String soDienThoai) {

        // Tạo một Map để chứa dữ liệu trả về
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra email
        KhachHangDTO emailCustomer = khachHangService.findByEmail(email);
        boolean emailExists = emailCustomer != null;

        // Kiểm tra số điện thoại
        KhachHangDTO phoneCustomer = khachHangService.findBySoDienThoai(soDienThoai);
        boolean phoneExists = phoneCustomer != null;

        // Nếu có trùng lặp, trả về thông tin khách hàng
        if (emailExists || phoneExists) {
            response.put("exists", true);
            response.put("message", "Khách hàng với email hoặc số điện thoại đã tồn tại.");
            // Nếu cả hai đều tồn tại, ưu tiên trả về thông tin khách hàng từ email
            response.put("khachHang", emailExists ? emailCustomer : phoneCustomer);
        } else {
            response.put("exists", false);
            response.put("message", "Thông tin khách hàng không tồn tại.");
        }

        return ResponseEntity.ok(response);
    }





}
