package com.example.DuAnTotNghiepKs.service.Imp;


import com.example.DuAnTotNghiepKs.entity.DienTich;
import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.repository.DienTichRepo;
import com.example.DuAnTotNghiepKs.repository.TangRepo;
import com.example.DuAnTotNghiepKs.service.DienTichService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DienTichServiceImp implements DienTichService {
    @Autowired
    private DienTichRepo dienTichRepo;

    @Override
    public Optional<DienTich> getDienTichById(Integer idDienTich) {
        return dienTichRepo.findById(idDienTich);
    }
    @Override
    public List<DienTich> getAllDienTichPhongs() {
        return dienTichRepo.findAll();
    }


}