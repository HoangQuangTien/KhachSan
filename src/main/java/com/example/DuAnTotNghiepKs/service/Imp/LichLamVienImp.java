package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.LichLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.LichLamViec;
import com.example.DuAnTotNghiepKs.repository.LichLamViecRepo;
import com.example.DuAnTotNghiepKs.service.LichLamViecService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LichLamVienImp implements LichLamViecService {

    @Autowired
    private LichLamViecRepo lichLamViecRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<LichLamViecDTO> getAll(){
        List<LichLamViec> lichLamViecs = lichLamViecRepo.findAll();
        return lichLamViecs.stream()
                .map(lichLamViec -> modelMapper.map(lichLamViec, LichLamViecDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LichLamViecDTO> findByNhanVien(NhanVienDTO nhanVienDTO){
        List<LichLamViec> lichLamViecs = lichLamViecRepo.findByNhanVien(nhanVienDTO);
        return lichLamViecs.stream()
                .map(lichLamViec -> modelMapper.map(lichLamViec,LichLamViecDTO.class))
                .collect(Collectors.toList());
    }


}
