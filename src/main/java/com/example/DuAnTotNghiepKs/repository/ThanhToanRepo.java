package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface ThanhToanRepo extends JpaRepository<ThanhToan, Integer> {
}
