package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.DanhGiaDTO;

import java.util.List;

public interface DanhGiaService {


    List<DanhGiaDTO> getDanhGiaByPhong(Integer idPhong);

    DanhGiaDTO addDanhGia(DanhGiaDTO danhGiaDTO);

    // Lấy tất cả đánh giá
    List<DanhGiaDTO> getAllDanhGia();

    boolean deleteDanhGia(Integer idDanhGia);
}
