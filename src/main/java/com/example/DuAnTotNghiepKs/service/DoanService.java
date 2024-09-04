package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.Doan;

import java.util.List;
import java.util.Optional;

public interface DoanService {
    Optional<Doan> getDoanById(Integer idDoan);

    List<Doan> getAllDoans();

    Doan findById(Integer doanId);

    void saveDoan(Doan doan);
}
