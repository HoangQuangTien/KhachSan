package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.DienTich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DienTichRepo extends JpaRepository<DienTich, Integer> {
}
