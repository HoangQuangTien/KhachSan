package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiaChiKhachHangService {

    List<DiaChiKhachHangDTO> getAll();

    DiaChiKhachHangDTO getOne(Integer id);

    void save(DiaChiKhachHangDTO diaChiKhachHangDTO);

    void update(Integer id, DiaChiKhachHangDTO diaChiKhachHangDTO);

    void delete (Integer id);

    List<DiaChiKhachHangDTO> getAllJoinFetch();

    List<DiaChiKhachHangDTO> findById(Integer id);

    Page<DiaChiKhachHangDTO> findByIdDC(Integer id, Pageable pageable);
}
