package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.Tang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TangRepo extends JpaRepository<Tang, Integer> {

    List<Tang> findByLoaiPhong_IdLoaiPhong(Integer idLoaiPhong);
}
