package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.LichSuDatPhong;
import org.springframework.data.domain.Page;

public interface LichSuDatPhongService {
    LichSuDatPhong saveLichSuDatPhong(LichSuDatPhong lichSuDatPhong);

    Page<LichSuDatPhong> getLichSuDatPhong(int page, int size);
}
