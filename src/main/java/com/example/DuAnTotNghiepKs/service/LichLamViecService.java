package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.LichLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;

import java.util.List;

public interface LichLamViecService {
    List<LichLamViecDTO> getAll();

    LichLamViecDTO save(LichLamViecDTO lichLamViecDTO);

    LichLamViecDTO findById(Integer id);



    LichLamViecDTO findTopByOrderByIdLichLamViecDesc();
}
