package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.ThamSo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThamSoRepo extends JpaRepository<ThamSo, Long> {
    ThamSo findByTenThamSo(String tenThamSo);
}

