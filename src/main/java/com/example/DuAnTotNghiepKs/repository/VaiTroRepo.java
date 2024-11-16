package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroRepo extends JpaRepository<VaiTro,Integer> {

    VaiTro findByIdVaiTro(Integer id);

}
