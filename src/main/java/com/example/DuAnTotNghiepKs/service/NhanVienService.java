package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepo nhanVienRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<NhanVienDTO> getAll(){
        List<NhanVien> nhanViens = nhanVienRepository.findAll();
        return nhanViens.stream()
                .map(nhanVien -> modelMapper.map(nhanVien,NhanVienDTO.class))
                .collect(Collectors.toList());
    }

    public NhanVien findById(Integer idNhanVien) {
        return nhanVienRepository.findById(idNhanVien).orElse(null);
    }
}
