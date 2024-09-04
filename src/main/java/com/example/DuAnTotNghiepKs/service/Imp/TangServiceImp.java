package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.repository.TangRepo;
import com.example.DuAnTotNghiepKs.service.TangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TangServiceImp implements TangService {
    @Autowired
    private TangRepo tangRepository;

    @Override
    public Optional<Tang> getTangById(Integer idTang) {
        return tangRepository.findById(idTang);
    }

    @Override
    public List<Tang> getAllTangs() {
        return tangRepository.findAll();
    }
}
