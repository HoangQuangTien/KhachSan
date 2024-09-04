package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LichLamViecRepo extends JpaRepository<LichLamViec, Integer> {
    Optional<LichLamViec> findBymaLichLamViec(String maLichlamViec);
}
