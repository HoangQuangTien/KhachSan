package com.example.DuAnTotNghiepKs.service;
import com.example.DuAnTotNghiepKs.DTO.DatPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.repository.LoaiPhongRepo;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhongService {
    @Autowired
    private PhongRepo phongRepository;

    @Autowired
    private LoaiPhongRepo loaiPhongRepository;

    public Page<Phong> getAllPhongs(Pageable pageable) {
        return phongRepository.findAll(pageable);
    }

    public Page<Phong> getPhongsByLoaiPhong(Integer loaiPhongId, Pageable pageable) {
        return phongRepository.findByLoaiPhong_IdLoaiPhong(loaiPhongId, pageable);
    }

    public Page<Phong> getPhongsByLoaiPhongAndTinhTrang(Integer loaiPhongId, Boolean tinhTrang, Pageable pageable) {
        return phongRepository.findByLoaiPhong_IdLoaiPhongAndTinhTrang(loaiPhongId, tinhTrang, pageable);
    }

    public Page<Phong> getPhongsByTinhTrang(Boolean tinhTrang, Pageable pageable) {
        return phongRepository.findByTinhTrang(tinhTrang, pageable);
    }

    public Phong findById(Integer id) {
        return phongRepository.findById(id).orElse(null);
    }


    public Phong savePhong(Phong phong) {
        // Kiểm tra trùng mã phòng
//        if (isMaPhongTrung(phong.getMaPhong())) {
//            throw new IllegalArgumentException("Mã phòng đã tồn tại.");
//        }
        return phongRepository.save(phong);
    }

    public boolean isMaPhongTrung(String maPhong) {
        return phongRepository.existsByMaPhong(maPhong);
    }

    public Optional<Phong> getPhongById(Integer id) {
        return phongRepository.findById(id);
    }

    public long countActivePhongs1() {
        return phongRepository.countByTrangThai(true); // Tổng số phòng còn phòng
    }

    public long countInactivePhongs1() {
        return phongRepository.countByTrangThai(false); // Tổng số phòng hết phòng
    }


    // Thêm phương thức tính tổng số phòng đang hoạt động
    public long countActivePhongs() {
        return phongRepository.countByTinhTrang(true);
    }

    public void updatePhong(Phong phong) {
        if (phong.getIdPhong() == null) {
            throw new IllegalArgumentException("Phong ID must not be null.");
        }

        if (!phongRepository.existsById(phong.getIdPhong())) {
            throw new IllegalArgumentException("Phong with ID " + phong.getIdPhong() + " does not exist.");
        }

        // Xử lý loại phòng
        if (phong.getLoaiPhong() != null && phong.getLoaiPhong().getIdLoaiPhong() != null) {
            LoaiPhong loaiPhong = loaiPhongRepository.findById(phong.getLoaiPhong().getIdLoaiPhong()).orElse(null);
            phong.setLoaiPhong(loaiPhong);
        } else {
            throw new IllegalArgumentException("LoaiPhong must be selected.");
        }

        phongRepository.save(phong);
    }


    public boolean updateTinhTrang(Integer idPhong, Boolean tinhTrang) {
        Phong phong = phongRepository.findById(idPhong).orElse(null);
        if (phong == null) {
            return false; // Hoặc có thể ném ngoại lệ tùy vào logic ứng dụng
        }
        phong.setTinhTrang(tinhTrang);
        phongRepository.save(phong);
        return true;
    }



    public List<Phong> getPhongsByLoaiPhong1(Integer loaiPhongId) {
        return phongRepository.findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(loaiPhongId);
    }

    public PhongDTO convertToPhongDTO(Phong phong) {
        PhongDTO dto = new PhongDTO();
        dto.setIdPhong(phong.getIdPhong());
        dto.setTenPhong(phong.getTenPhong());
        dto.setMaPhong(phong.getMaPhong());
        dto.setMoTa(phong.getMoTa());
        dto.setTinhTrang(phong.getTinhTrang());
        dto.setTrangThai(phong.getTrangThai());
        dto.setGia(phong.getGia());

        if (phong.getLoaiPhong() != null) {
            dto.setIdLoaiPhong(phong.getLoaiPhong().getIdLoaiPhong());
            dto.setTenLoaiPhong(phong.getLoaiPhong().getTenLoaiPhong());
            dto.setGiaLoaiPhong(phong.getLoaiPhong().getGia());
        }

        return dto;
    }

    // Phương thức để đếm số lượng phòng còn trống và đang hoạt động
    public long countAvailableActiveRooms() {
        return phongRepository.countByTrangThaiAndTinhTrang(true, true); // true: phòng còn trống, true: phòng đang hoạt động
    }



}

