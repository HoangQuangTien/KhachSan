package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ThanhToanService {
    boolean xacNhanThanhToan(Integer idDatPhong, Double paymentAmount);

    List<ThanhToanDTO> getAll();

    ThanhToanDTO save(ThanhToanDTO thanhToanDTO);

    Page<ThanhToan> getLoaiPhongPage(int page, int size);
}
