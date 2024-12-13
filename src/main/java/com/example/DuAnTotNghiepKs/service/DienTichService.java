package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.DienTich;


import java.util.List;
import java.util.Optional;

public interface DienTichService {
    Optional<DienTich> getDienTichById(Integer idDienTich);

    List<DienTich> getAllDienTichPhongs();

    DienTich saveDienTich(DienTich dienTich);

    boolean isTenDienTichTrung(float tenDienTich);
}
