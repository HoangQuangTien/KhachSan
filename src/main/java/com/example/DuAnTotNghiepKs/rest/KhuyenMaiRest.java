package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
public class KhuyenMaiRest {

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepository;

    @PostMapping("/update/{idKhuyenMai}")
    public ResponseEntity<Map<String, Object>> capNhatSoLuong(@PathVariable Integer idKhuyenMai) {
        Map<String, Object> response = new HashMap<>();

        // Tìm mã khuyến mại theo ID
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(idKhuyenMai);

        if (!optionalKhuyenMai.isPresent()) {
            response.put("success", false);
            response.put("message", "Mã khuyến mại không tồn tại.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        KhuyenMai khuyenMai = optionalKhuyenMai.get();
        System.out.println("Số lượng trước khi cập nhật: " + khuyenMai.getSoLuong());

        // Kiểm tra số lượng còn lại
        if (khuyenMai.getSoLuong() > 0) {
            // Giảm số lượng
            khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
            khuyenMaiRepository.save(khuyenMai);

            System.out.println("Số lượng sau khi cập nhật: " + khuyenMai.getSoLuong());
            response.put("success", true);
            response.put("message", "Cập nhật số lượng mã khuyến mại thành công.");
        } else {
            response.put("success", false);
            response.put("message", "Mã khuyến mại đã hết.");
        }

        return ResponseEntity.ok(response);
    }


}
