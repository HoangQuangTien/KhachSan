package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.Doan;
import com.example.DuAnTotNghiepKs.repository.DoanRepo;
import com.example.DuAnTotNghiepKs.service.DoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoanImp implements DoanService {
    @Autowired
    private DoanRepo doanRepo;

    @Override
    public Optional<Doan> getDoanById(Integer idDoan) {
        return doanRepo.findById(idDoan);
    }

    @Override
    public List<Doan> getAllDoans() {
        return doanRepo.findAll();
    }

    @Override
    public Doan findById(Integer doanId) {
        return doanRepo.findById(doanId).orElse(null);
    }

    @Override
    public void saveDoan(Doan doan) {
        doanRepo.save(doan);
    }
}
