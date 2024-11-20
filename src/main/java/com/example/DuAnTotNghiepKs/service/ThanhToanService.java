package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ThanhToanService {
    boolean xacNhanThanhToan(Integer idDatPhong, Double paymentAmount);

    List<ThanhToanDTO> getAll();

    Page<ThanhToanDTO> findAllByTinhTrang(Pageable pageable);

    ThanhToanDTO save(ThanhToanDTO thanhToanDTO);

    Page<ThanhToan> getLoaiPhongPage(int page, int size);


    long getThoiGianChoPhepChuyenTrangThai();

    byte[] createInvoicePDF(ThanhToanDTO thanhToanDTO) throws IOException;

    List<ThanhToanDTO> search(String query, Date ngayThanhToan);

    ThanhToanDTO getThanhToanById(Integer id);
}
