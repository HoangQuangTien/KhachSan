package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class KhuyenMaiService {
    @Autowired
    private KhuyenMaiRepo khuyenMaiRepository;

    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiRepository.findAll();
    }

    public KhuyenMai getKhuyenMaiById(Integer id) {

        return khuyenMaiRepository.findById(id).orElse(null);
    }

    public KhuyenMai getKhuyenMaiByMaKhuyenMai(String maKhuyenMai) {
        return khuyenMaiRepository.findByMaKhuyenMai(maKhuyenMai);
    }

    public boolean existsByMaKhuyenMai(String maKhuyenMai) {
        return khuyenMaiRepository.existsByMaKhuyenMai(maKhuyenMai);
    }
//    public KhuyenMai saveKhuyenMai(KhuyenMai khuyenMai) {
//        Float giamToiThieu = khuyenMai.getGiamToiThieu();
//        Float giamToiDa = khuyenMai.getGiamToiDa();
//        if (giamToiDa < giamToiThieu){
//            throw new IllegalArgumentException("Giảm tối đa phải lơn hơn giảm tối thiểu");
//        }
//        if (khuyenMaiRepository.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())){
//            throw new RuntimeException("Mã khuyến mại đã tồn tại");
//        }
//        return khuyenMaiRepository.save(khuyenMai);
//    }

    public KhuyenMai saveKhuyenMai(KhuyenMai khuyenMai) {
        // Kiểm tra mã khuyến mại
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().isBlank()) {
            throw new IllegalArgumentException("Mã khuyến mại không được để trống");
        }

        // Kiểm tra tên khuyến mại
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().isBlank()) {
            throw new IllegalArgumentException("Tên khuyến mại không được để trống");
        }

        // Kiểm tra mô tả
        String moTa = khuyenMai.getMoTa();
        if (moTa == null || moTa.length() < 10 || moTa.length() > 200) {
            throw new IllegalArgumentException("Mô tả phải có độ dài từ 10 đến 200 ký tự");
        }

        // Kiểm tra ngày bắt đầu
        if (khuyenMai.getNgayBatDau() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }

        // Kiểm tra ngày kết thúc
        if (khuyenMai.getNgayKetThuc() == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }

        // Kiểm tra giảm giá
        Float giamGia = khuyenMai.getGiamGia();
        if (giamGia == null) {
            throw new IllegalArgumentException("Giảm giá không được để trống");
        }

        // Kiểm tra loại giảm
        if (khuyenMai.getLoaiGiam() == null) {
            throw new IllegalArgumentException("Loại giảm không được để trống");
        }

        // Kiểm tra số lượng
        Integer soLuong = khuyenMai.getSoLuong();
        if (soLuong == null || soLuong < 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 0");
        }

        // Kiểm tra giảm tối thiểu và tối đa
        Float giamToiThieu = khuyenMai.getGiamToiThieu();
        Float giamToiDa = khuyenMai.getGiamToiDa();

        if (giamToiThieu == null || giamToiDa == null) {
            throw new IllegalArgumentException("Giảm tối thiểu và tối đa không được để trống");
        }

        if (giamToiDa < giamToiThieu) {
            throw new IllegalArgumentException("Giảm tối đa phải lớn hơn giảm tối thiểu");
        }

        // Kiểm tra mã khuyến mại đã tồn tại
        if (khuyenMaiRepository.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())) {
            throw new RuntimeException("Mã khuyến mại '" + khuyenMai.getMaKhuyenMai() + "' đã tồn tại");
        }

        // Kiểm tra trạng thái
        String trangThai = khuyenMai.getTrangThai();
        if (trangThai == null || trangThai.isBlank()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }


        // Lưu khuyến mại
        return khuyenMaiRepository.save(khuyenMai);
    }


    public KhuyenMai updateKhuyenMai(Integer id, KhuyenMai updatedKhuyenMai) {
        // Kiểm tra xem khuyến mãi có tồn tại không
        KhuyenMai existingKhuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại với ID: " + id));

        // Kiểm tra nếu mã khuyến mãi đã thay đổi và đã tồn tại
        if (!existingKhuyenMai.getMaKhuyenMai().equals(updatedKhuyenMai.getMaKhuyenMai()) &&
                khuyenMaiRepository.existsByMaKhuyenMai(updatedKhuyenMai.getMaKhuyenMai())) {
            throw new RuntimeException("Mã khuyến mãi đã tồn tại.");
        }

        // Cập nhật các trường
        existingKhuyenMai.setMaKhuyenMai(updatedKhuyenMai.getMaKhuyenMai());
        existingKhuyenMai.setTenKhuyenMai(updatedKhuyenMai.getTenKhuyenMai());
        existingKhuyenMai.setMoTa(updatedKhuyenMai.getMoTa());
        existingKhuyenMai.setSoLuong(updatedKhuyenMai.getSoLuong());
        existingKhuyenMai.setNgayBatDau(updatedKhuyenMai.getNgayBatDau());
        existingKhuyenMai.setNgayKetThuc(updatedKhuyenMai.getNgayKetThuc());
        existingKhuyenMai.setLoaiGiam(updatedKhuyenMai.getLoaiGiam());
        existingKhuyenMai.setGiamGia(updatedKhuyenMai.getGiamGia());
        existingKhuyenMai.setGiamToiThieu(updatedKhuyenMai.getGiamToiThieu());
        existingKhuyenMai.setGiamToiDa(updatedKhuyenMai.getGiamToiDa());
        existingKhuyenMai.setTrangThai(updatedKhuyenMai.getTrangThai());

        return khuyenMaiRepository.save(existingKhuyenMai);
    }

    public void deleteKhuyenMaiById(Integer id) {
        khuyenMaiRepository.deleteById(id);
    }


    public List<KhuyenMai> searchKhuyenMai(String keyword) {
        if (keyword != null) {
            return khuyenMaiRepository.searchByKeyword(keyword);
        }
        return khuyenMaiRepository.findAll();
    }


    public Page<KhuyenMai> getKhuyenMaiPage(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size,sort);
        return khuyenMaiRepository.getAllBy(pageable);
    }
    public Page<KhuyenMai> searchKhuyenMai(String keyword, String trangThai, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort); // Tạo pageable với số trang và kích thước

        // Nếu không có từ khóa tìm kiếm
        if (keyword == null || keyword.isEmpty()) {
            // Nếu có trạng thái, tìm theo trạng thái
            if (trangThai != null && !trangThai.isEmpty()) {
                return khuyenMaiRepository.findByTrangThai(trangThai, pageable);
            }
            // Nếu không có trạng thái, trả về tất cả
            return khuyenMaiRepository.findAll(pageable);
        }

        // Nếu có từ khóa và trạng thái
        return khuyenMaiRepository.findByTenKhuyenMaiContainingOrTenKhuyenMaiContainingAndTrangThai(
                keyword, keyword, trangThai, pageable);
    }


    public List<KhuyenMai> getAllActiveKhuyenMai() {
        return khuyenMaiRepository.findByTrangThai("Còn hạn"); // "active" có thể thay đổi theo trạng thái thực tế bạn sử dụng
    }



    // Chạy mỗi ngày vào lúc 00:00 để kiểm tra trạng thái khuyến mãi
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void updateTrangThaiKhuyenMai() {
//        Date currentDate = new Date();
//        List<KhuyenMai> khuyenMais = khuyenMaiRepository.findAll();
//
//        for (KhuyenMai khuyenMai : khuyenMais) {
//            if (khuyenMai.getNgayKetThuc().before(currentDate)) {
//                khuyenMai.setTrangThai(String); // Hết hạn
//                khuyenMaiRepository.save(khuyenMai);
//            }
//        }
//    }



    public List<KhuyenMai> getActiveVouchers() {
        return khuyenMaiRepository.findAllActiveVouchers(new Date());
    }





    public ResponseEntity<Map<String, Object>> capNhatSoLuong(Integer id) {
        Optional<KhuyenMai> khuyenMaiOptional = khuyenMaiRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (khuyenMaiOptional.isPresent()) {
            KhuyenMai khuyenMai = khuyenMaiOptional.get();
            int soLuongHienTai = khuyenMai.getSoLuong();
            System.out.println("Số lượng hiện tại trước khi cập nhật: " + soLuongHienTai);

            if (soLuongHienTai > 0) {
                khuyenMai.setSoLuong(soLuongHienTai - 1); // Giảm số lượng đi 1
                khuyenMaiRepository.save(khuyenMai); // Lưu vào database
                System.out.println("Số lượng sau khi cập nhật: " + (soLuongHienTai - 1)); // Log sau khi cập nhật
                response.put("success", true);
                return ResponseEntity.ok(response); // Trả về thành công
            } else {
                response.put("success", false);
                response.put("message", "Số lượng khuyến mãi đã hết.");
            }
        } else {
            response.put("success", false);
            response.put("message", "Không tìm thấy khuyến mãi với ID đã cho.");
        }

        return ResponseEntity.badRequest().body(response);
    }

    @Scheduled(fixedRate = 60000) // mỗi phút một lần
    public void updateKhuyenMaiStatus() {
        List<KhuyenMai> khuyenMais = khuyenMaiRepository.findAll();
        Date now = new Date();

        for (KhuyenMai khuyenMai : khuyenMais) {
            if (now.before(khuyenMai.getNgayBatDau())) {
                khuyenMai.setTrangThai("Sắp diễn ra");
            } else if (now.after(khuyenMai.getNgayKetThuc())) {
                khuyenMai.setTrangThai("Hết hạn");
            } else {
                khuyenMai.setTrangThai("Còn hạn");
            }
        }

        khuyenMaiRepository.saveAll(khuyenMais); // lưu lại tất cả khuyến mãi đã cập nhật
    }





}
