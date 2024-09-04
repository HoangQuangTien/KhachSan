package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.Doan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoanRepo extends JpaRepository<Doan, Integer> {
}
