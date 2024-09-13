package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KhachHangService {

    List<KhachHangDTO> getAll();

    KhachHangDTO getOne(Integer id);

    KhachHangDTO save(KhachHangDTO khachHangDTO);

    String generateMaKhachHang();

    void update(Integer id, KhachHangDTO khachHangDTO);

    void delete(Integer id);

    KhachHangDTO findById(Integer id);

    Page<KhachHangDTO> getAll(Pageable pageable);

    public List<KhachHangDTO> search(String query);

    public List<KhachHangDTO> filter(String status);

    KhachHang convertToEntity(KhachHangDTO khachHangDTO);

    Integer saveKhachHang(KhachHangDTO khachHangDTO);


    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    KhachHangDTO findByEmail(String email);


    KhachHangDTO findBySoDienThoai(String soDienThoai);
}
