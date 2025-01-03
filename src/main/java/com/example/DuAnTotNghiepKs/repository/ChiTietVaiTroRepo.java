package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.ChiTietVaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietVaiTroRepo extends JpaRepository<ChiTietVaiTro,Integer> {

    ChiTietVaiTro findTopByOrderByIdChiTietVaiTroDesc();

}
