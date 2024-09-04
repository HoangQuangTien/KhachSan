package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.ThanhVienDoan;
import com.example.DuAnTotNghiepKs.repository.ThanhVienDoanRepo;
import com.example.DuAnTotNghiepKs.service.ThanhVienDoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThanhVienDoanImp implements ThanhVienDoanService {
    @Autowired
    private ThanhVienDoanRepo thanhVienDoanRepo;

    @Override
    public Optional<ThanhVienDoan> getThanhVienDoanById(Integer idThanhVienDoan) {
        return thanhVienDoanRepo.findById(idThanhVienDoan);
    }

    @Override
    public List<ThanhVienDoan> getAllThanhVienDoans() {
        return thanhVienDoanRepo.findAll();
    }
}
