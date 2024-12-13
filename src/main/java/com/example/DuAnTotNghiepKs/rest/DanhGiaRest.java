package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.DanhGiaDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DanhGia;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.service.DanhGiaService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danhgia")
public class DanhGiaRest {

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private TaiKhoanService taiKhoanService;

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
        // Lấy thông tin người dùng từ session
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession();

        // Kiểm tra nếu tài khoản hợp lệ và có tên khách hàng
        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null
                && taiKhoanDTO.getKhachHangDTO().getHoVaTen() != null) {

            // Lấy tên khách hàng từ tài khoản
            String tenKhachHang = taiKhoanDTO.getKhachHangDTO().getHoVaTen();

            // Cập nhật tên khách hàng vào DTO
            danhGiaDTO.setTenKhachHang(tenKhachHang);

            // Lưu đánh giá vào cơ sở dữ liệu
            DanhGiaDTO savedDanhGiaDTO = danhGiaService.addDanhGia(danhGiaDTO);  // Dùng DTO khi gọi service

            return ResponseEntity.ok(savedDanhGiaDTO);  // Trả về phản hồi thành công với DanhGiaDTO
        }

        // Nếu không có tài khoản hợp lệ hoặc không tìm thấy tên khách hàng
        return ResponseEntity.badRequest().body(null);  // Trả về 400 nếu có lỗi
    }

    // Lấy danh sách tất cả đánh giá
    @GetMapping("/all")
    public ResponseEntity<List<DanhGiaDTO>> getAllDanhGia() {
        // Gọi dịch vụ để lấy tất cả các đánh giá
        List<DanhGiaDTO> danhGiaList = danhGiaService.getAllDanhGia();
        return ResponseEntity.ok(danhGiaList); // Trả về danh sách đánh giá
    }


    @DeleteMapping("/{idDanhGia}")
    public ResponseEntity<Void> deleteDanhGia(@PathVariable Integer idDanhGia) {
        boolean isDeleted = danhGiaService.deleteDanhGia(idDanhGia);

        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Trả về HTTP 204 nếu xóa thành công
        } else {
            return ResponseEntity.notFound().build(); // Trả về HTTP 404 nếu không tìm thấy đánh giá
        }
    }

}

