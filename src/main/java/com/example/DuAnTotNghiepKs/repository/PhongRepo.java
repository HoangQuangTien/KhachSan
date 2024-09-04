package com.example.DuAnTotNghiepKs.repository;


import com.example.DuAnTotNghiepKs.entity.Phong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PhongRepo extends JpaRepository<Phong, Integer> {
//    Optional<Phong> findById(Integer id);
    Page<Phong> findByLoaiPhong_IdLoaiPhongAndTinhTrang(Integer idLoaiPhong, Boolean tinhTrang, Pageable pageable);
    Page<Phong> findByLoaiPhong_IdLoaiPhong(Integer idLoaiPhong, Pageable pageable);
    Page<Phong> findByTinhTrang(Boolean tinhTrang, Pageable pageable);
    Page<Phong> findAll(Pageable pageable);

    // Thêm phương thức đếm số phòng đang hoạt động
    long countByTinhTrang(boolean tinhTrang);


    long countByTrangThai(Boolean trangThai);


    List<Phong> findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(Integer loaiPhongId);

    List<Phong> findByLoaiPhongIdLoaiPhong(Integer loaiPhongId);

    boolean existsByMaPhong(String maPhong);

    long countByTrangThaiAndTinhTrang(Boolean trangThai, Boolean tinhTrang);
}

