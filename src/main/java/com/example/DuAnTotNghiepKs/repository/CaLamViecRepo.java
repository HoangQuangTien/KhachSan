package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaLamViecRepo extends JpaRepository<CaLamViec, Integer> {
    Optional<CaLamViec> findBymaCaLamViec(String ma);
}
