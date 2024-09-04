package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.ThanhVienDoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThanhVienDoanRepo extends JpaRepository<ThanhVienDoan, Integer> {
}
