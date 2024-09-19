package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.Tang;

import java.util.List;
import java.util.Optional;


public interface TangService {
    Optional<Tang> getTangById(Integer idTang);

    List<Tang> getAllTangs();



    Tang saveTang(Tang tang);

    // Tìm danh sách tầng theo id loại phòng
    List<Tang> findByLoaiPhongId(Integer idLoaiPhong);
}
