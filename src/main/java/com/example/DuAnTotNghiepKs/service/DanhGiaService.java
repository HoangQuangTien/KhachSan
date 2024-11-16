package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.DanhGiaDTO;

import java.util.List;

public interface DanhGiaService {


    List<DanhGiaDTO> getDanhGiaByPhong(Integer idPhong);

    DanhGiaDTO addDanhGia(DanhGiaDTO danhGiaDTO);
}
