package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.CaLamViecDTO;
import com.example.DuAnTotNghiepKs.entity.CaLamViec;
import com.example.DuAnTotNghiepKs.repository.CaLamViecRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaLamViecService {

    @Autowired
    private CaLamViecRepo caLamViecRepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<CaLamViecDTO> getAll(){
        List<CaLamViec> caLamViecs = caLamViecRepo.findAll();
        return caLamViecs.stream()
                .map(caLamViec -> modelMapper.map(caLamViec,CaLamViecDTO.class))
                .collect(Collectors.toList());
    }

}
