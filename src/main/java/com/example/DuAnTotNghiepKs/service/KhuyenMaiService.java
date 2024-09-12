package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
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
        return khuyenMaiRepository.save(khuyenMai);
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
        return khuyenMaiRepository.findByTrangThaiTrue(pageable);
    }
    public Page<KhuyenMai> searchKhuyenMai(String keyword, boolean trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return khuyenMaiRepository.findByMaKhuyenMaiContainingIgnoreCaseOrTenKhuyenMaiContainingIgnoreCaseAndTrangThai(
                keyword, keyword, trangThai, pageable);
    }



    // Chạy mỗi ngày vào lúc 00:00 để kiểm tra trạng thái khuyến mãi
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTrangThaiKhuyenMai() {
        Date currentDate = new Date();
        List<KhuyenMai> khuyenMais = khuyenMaiRepository.findAll();

        for (KhuyenMai khuyenMai : khuyenMais) {
            if (khuyenMai.getNgayKetThuc().before(currentDate)) {
                khuyenMai.setTrangThai(false); // Hết hạn
                khuyenMaiRepository.save(khuyenMai);
            }
        }
    }
}

