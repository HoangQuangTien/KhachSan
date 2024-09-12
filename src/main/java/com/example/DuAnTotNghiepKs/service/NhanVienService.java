package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepo nhanVienRepository;


    public NhanVien findById(Integer idNhanVien) {
        return nhanVienRepository.findById(idNhanVien).orElse(null);
    }
}
