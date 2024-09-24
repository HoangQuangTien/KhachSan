package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.ThamSoDTO;
import com.example.DuAnTotNghiepKs.entity.ThamSo;

import java.util.List;

public interface ThamSoService {

    List<ThamSoDTO> getAllThamSo();

    ThamSoDTO save(ThamSoDTO thamSoDTO);

    ThamSoDTO findById(Long id);

    void deleteById(Long id);

    String getThamSoValue(String tenThamSo);
}
