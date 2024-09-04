package com.example.DuAnTotNghiepKs.service;


import com.example.DuAnTotNghiepKs.entity.ThanhVienDoan;

import java.util.List;
import java.util.Optional;

public interface ThanhVienDoanService {

    Optional<ThanhVienDoan> getThanhVienDoanById(Integer idThanhVienDoan);

    List<ThanhVienDoan> getAllThanhVienDoans();
}
