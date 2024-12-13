package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaRepo extends JpaRepository<DanhGia, Integer> {
    List<DanhGia> findByPhong_IdPhong(Integer idPhong);


}
