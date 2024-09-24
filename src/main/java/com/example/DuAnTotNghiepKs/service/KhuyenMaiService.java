package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


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
    public KhuyenMai saveKhuyenMai(KhuyenMai khuyenMai) {
        Float giamToiThieu = khuyenMai.getGiamToiThieu();
        Float giamToiDa = khuyenMai.getGiamToiDa();
        if (giamToiDa < giamToiThieu){
            throw new IllegalArgumentException("Giảm tối đa phải lơn hơn giảm tối thiểu");
        }
        if (khuyenMaiRepository.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())){
            throw new RuntimeException("Mã khuyến mại đã tồn tại");
        }
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
    public Page<KhuyenMai> searchKhuyenMai(String keyword, String trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return khuyenMaiRepository.searchKhuyenMai(keyword, keyword, trangThai, pageable);
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





}
